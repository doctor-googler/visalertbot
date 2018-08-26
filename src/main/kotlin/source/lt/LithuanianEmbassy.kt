package source.lt

import com.beust.klaxon.JsonArray
import com.beust.klaxon.Klaxon
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpResponse
import com.google.api.client.http.apache.ApacheHttpTransport
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import consumer.api.IConsumer
import messaging.Message
import source.api.ISource
import java.io.StringReader
import java.net.URL
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class LithuanianEmbassy : ISource {

    private val embassyUrl = "https://evas2.urm.lt/visit/"
    private val aHtmlTag = "a"
    private val aHtmlTagAttribute = "href"
    private val mysticValue = "rct77"
    private val jsonDatesUrl = "/calendar/json?_d=&_aby=3&_cry=6&_c=1&_t="
    private val jsonTimesUrl = "/calendar/json?_d=%s&_aby=3&_cry=6&_c=1&_t=1"
    private val consumers: MutableList<IConsumer> = CopyOnWriteArrayList()
    private var lastInfo: Map<String, List<String>> = HashMap()

    init {
        ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(this::updateInfo, 5, 60, TimeUnit.SECONDS)
    }

    override fun registerConsumer(consumer: IConsumer) {
        this.consumers.add(consumer)
    }

    override fun unregisterConsumer(consumer: IConsumer) {
        this.consumers.remove(consumer)
    }

    override fun getName(): String {
        return "Литовское посольство"
    }

    override fun info(): Map<String, List<String>> {
        return this.lastInfo
    }

    private fun updateInfo() {
        /**val baseUrl: URL = mineJsonUrls()
        val rawDatesJson: String =
                retrieveXHRJson("${baseUrl.protocol}://${baseUrl.host}$jsonDatesUrl", baseUrl.toString())
        this.lastInfo = parseJsonArray(rawDatesJson)
                .map {
                    it to parseJsonArray(
                            retrieveXHRJson("${baseUrl.protocol}://${baseUrl.host}${String.format(jsonTimesUrl, it)}",
                                    baseUrl.toString()))
                }
                .toMap()**/
        this.lastInfo = ImmutableMap.of("20-10-1995", ImmutableList.of("Kwak data 2015", "cyka blyat 2018"))
        consumers.forEach { c -> c.onMessageReceive(Message(this.getName(), this.lastInfo)) }
    }

    private fun mineJsonUrls(): URL {
        val client = WebClient(BrowserVersion.CHROME)
        client.options.timeout = 30000
        client.options.isThrowExceptionOnFailingStatusCode = false
        client.options.isThrowExceptionOnScriptError = false
        client.options.isCssEnabled = true
        client.options.isJavaScriptEnabled = true
        client.waitForBackgroundJavaScript(10000)

//        client.use {
            var page: HtmlPage = client.getPage(embassyUrl)
            val el: HtmlElement = page.body.getElementsByTagName(aHtmlTag)
                    .last { tg -> tg.getAttribute(aHtmlTagAttribute).contains(mysticValue) }
            page = client.getPage(embassyUrl + el.getAttribute(aHtmlTagAttribute))
            return page.url
  //      }
    }

    private fun retrieveXHRJson(url: String, refererUrl: String): String {
        val datesUrl = GenericUrl(url)
        println(url)
        val jsonRequest: HttpRequest = ApacheHttpTransport()
                .createRequestFactory { request: HttpRequest? ->
                        request?.headers?.set("Referer", refererUrl)
                        request?.headers?.set("X-Requested-With", "XMLHttpRequest")
                }
                .buildGetRequest(datesUrl)
        val jsonResponse: HttpResponse = jsonRequest.execute()
        val result: String = jsonResponse.parseAsString()
        jsonResponse.disconnect()
        return result
    }

    private fun parseJsonArray(rawJson: String): List<String> {
        val strings: ArrayList<String> = ArrayList()
        val raw: JsonArray<String> = Klaxon().parser.parse(StringReader(rawJson)) as JsonArray<String>
        raw.forEach { str: String -> strings.add(str) }
        return strings
    }
}