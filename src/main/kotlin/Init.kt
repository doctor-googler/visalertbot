import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage

fun main(args: Array<String>) {
    /*ApiContextInitializer.init();
    val telegramBotApi: TelegramBotsApi = TelegramBotsApi()
    try {
        telegramBotApi.registerBot(LTVisaBot())
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }*/
    val client: WebClient = WebClient();
    var url: String = "https://evas2.urm.lt/visit/"
    var page: HtmlPage = client.getPage<HtmlPage>(url)
    val el:HtmlElement = page.body.getElementsByTagName("a")
            .filter { tg -> tg.getAttribute("href").contains("rct77") }
            .last()
    println(el);
    page = client.getPage(url+el.getAttribute("href"));

}