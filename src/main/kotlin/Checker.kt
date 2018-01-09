import com.beust.klaxon.JsonArray
import com.beust.klaxon.Klaxon
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.github.shyiko.skedule.Schedule
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpResponse
import com.google.api.client.http.apache.ApacheHttpTransport
import java.io.StringReader
import java.net.URL
import java.time.ZonedDateTime
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class Checker {
    private val EMBASSY_URL = "https://evas2.urm.lt/visit/"
    private val A_HTML_TAG = "a"
    private val A_HTML_TAG_ATTRIBUTE = "href"
    private val MYSTIC_URL_VALUE = "rct77"
    private val JSON_DATES_URL = "/calendar/json?_d=&_aby=3&_cry=6&_c=1&_t="
    private val JSON_TIMES_URL_TEMPLATE = "/calendar/json?_d=%s&_aby=3&_cry=6&_c=1&_t=1"
    private val FREE_DATES: MutableMap<String, MutableList<String>> = HashMap()

    fun startChecks(callback: (Map<String, List<String>>) -> Unit) {
        ScheduledThreadPoolExecutor(1)
                .scheduleWithFixedDelay(
                        {
                            if (isSomethingNew()) {
                                callback(FREE_DATES)
                            }
                        },
                        0,
                        2 * 60,
                        TimeUnit.SECONDS)
    }

    private fun isSomethingNew(): Boolean {
        var result = false
        val updatedDates: MutableMap<String, MutableList<String>> = getDates()
        updatedDates.entries.forEach { entry: Map.Entry<String, MutableList<String>> ->
            run {
                if (FREE_DATES.containsKey(entry.key)) {
                    if (!FREE_DATES[entry.key]?.containsAll(entry.value)!!) {
                        FREE_DATES[entry.key]?.union(entry.value)
                        result = true
                    } else {
                        result = false
                    }
                } else {
                    FREE_DATES.put(entry.key, entry.value)
                    result = true
                }
            }
        }
        return result
    }

    private fun getDates(): MutableMap<String, MutableList<String>> {
        val result: MutableMap<String, MutableList<String>> = HashMap()

        val bsUrl: URL = mineJsonUrls()
        var rawDatesJson: String = retrieveXHRJson("${bsUrl.protocol}://${bsUrl.host}$JSON_DATES_URL", bsUrl.toString())
        parseJsonArray(rawDatesJson).forEach {
            date: String -> run {
                val template: String = String.format(JSON_TIMES_URL_TEMPLATE, date)
                val rawTimesJson: String = retrieveXHRJson("${bsUrl.protocol}://${bsUrl.host}$template", bsUrl.toString())

                val parsedTimes: MutableList<String> = ArrayList()
                parseJsonArray(rawTimesJson).forEach { time: String -> parsedTimes.add(time) }
                result.put(date, parsedTimes)
            }
        }
        return result
    }

    private fun mineJsonUrls(): URL {
        val client = WebClient()
        var page: HtmlPage = client.getPage(EMBASSY_URL)
        val el: HtmlElement = page.body.getElementsByTagName(A_HTML_TAG)
                .last { tg -> tg.getAttribute(A_HTML_TAG_ATTRIBUTE).contains(MYSTIC_URL_VALUE) }
        page = client.getPage(EMBASSY_URL + el.getAttribute(A_HTML_TAG_ATTRIBUTE))
        return page.url
    }

    private fun retrieveXHRJson(url: String, refererUrl: String): String {
        val datesUrl = GenericUrl(url)
        println(url)
        val jsonRequest: HttpRequest = ApacheHttpTransport()
                .createRequestFactory({
                    request: HttpRequest? -> run {
                    request?.headers?.set("Referer", refererUrl)
                    request?.headers?.set("X-Requested-With", "XMLHttpRequest")
                }
                })
                .buildGetRequest(datesUrl)
        val jsonResponse: HttpResponse = jsonRequest.execute()
        val result:String = jsonResponse.parseAsString()
        jsonResponse.disconnect()
        return result
    }

    private fun parseJsonArray(rawJson: String): List<String> {
        val strings: ArrayList<String> = ArrayList()
        var raw: JsonArray<String> = Klaxon().parser.parse(StringReader(rawJson)) as JsonArray<String>
        raw.forEach { str: String -> strings.add(str) }
        return strings
    }
}