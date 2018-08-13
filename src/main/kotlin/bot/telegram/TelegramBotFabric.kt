package bot.telegram;

import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.exceptions.TelegramApiException

class TelegramBotFabric {

    fun generateBot(): TelegramBot {
        ApiContextInitializer.init()
        val telegramBotApi = TelegramBotsApi()
        val telegramBot = TelegramBot()
        try {
            telegramBotApi.registerBot(telegramBot)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
        return telegramBot
    }
}
