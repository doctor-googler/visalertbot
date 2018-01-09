import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.exceptions.TelegramApiException

fun main(args: Array<String>) {
    ApiContextInitializer.init();
    val telegramBotApi = TelegramBotsApi()
    val ltVisaBot = LTVisaBot()
    try {
        telegramBotApi.registerBot(ltVisaBot)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
    Checker().startChecks(ltVisaBot::alertAll)
}