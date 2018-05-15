package source.impl

import source.api.Source
import com.beust.klaxon.JsonArray
import com.beust.klaxon.Klaxon
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpResponse
import com.google.api.client.http.apache.ApacheHttpTransport
import java.io.StringReader
import java.net.URL

class LithuaniaEmbassy : Source<Map<String, List<String>>> {

    private val embassyUrl = "https://evas2.urm.lt/visit/"
    private val aHtmlTag = "a"
    private val aHtmlTagAttribute = "href"
    private val mysticValue = "rct77"
    private val jsonDatesUrl = "/calendar/json?_d=&_aby=3&_cry=6&_c=1&_t="
    private val jsonTimesUrl = "/calendar/json?_d=%s&_aby=3&_cry=6&_c=1&_t=1"

    override fun info(): Map<String, List<String>> {
        val baseUrl: URL = mineJsonUrls();
        val rawDatesJson: String =
                retrieveXHRJson("${baseUrl.protocol}://${baseUrl.host}$jsonDatesUrl", baseUrl.toString())
        return parseJsonArray(rawDatesJson)
                .map {
                    it to parseJsonArray(
                            retrieveXHRJson("${baseUrl.protocol}://${baseUrl.host}${String.format(jsonTimesUrl, it)}",
                                    baseUrl.toString()))
                }
                .toMap()
    }

    private fun mineJsonUrls(): URL {
        val client = WebClient()
        var page: HtmlPage = client.getPage(embassyUrl)
        val el: HtmlElement = page.body.getElementsByTagName(aHtmlTag)
                .last { tg -> tg.getAttribute(aHtmlTagAttribute).contains(mysticValue) }
        page = client.getPage(embassyUrl + el.getAttribute(aHtmlTagAttribute))
        return page.url
    }

    private fun retrieveXHRJson(url: String, refererUrl: String): String {
        val datesUrl = GenericUrl(url)
        println(url)
        val jsonRequest: HttpRequest = ApacheHttpTransport()
                .createRequestFactory({ request: HttpRequest? ->
                    run {
                        request?.headers?.set("Referer", refererUrl)
                        request?.headers?.set("X-Requested-With", "XMLHttpRequest")
                    }
                })
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