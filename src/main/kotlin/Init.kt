import broker.api.MessageBroker
import broker.impl.DateBroker
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.exceptions.TelegramApiException
import source.impl.LithuaniaEmbassy

fun main(args: Array<String>) {
    ApiContextInitializer.init();
    val telegramBotApi = TelegramBotsApi()
    val ltVisaBot = LTVisaBot()
    try {
        telegramBotApi.registerBot(ltVisaBot)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
    val broker = DateBroker()
    broker.addSource(LithuaniaEmbassy())
    broker.subscribe(ltVisaBot)
    broker.run()
}