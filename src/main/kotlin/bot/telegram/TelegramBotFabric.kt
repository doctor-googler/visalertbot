package bot.telegram;

import dao.TelegramUserDao
import model.TelegramUser
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.exceptions.TelegramApiException

class TelegramBotFabric(val telegramUserDao: TelegramUserDao) {

    fun generateBot(): TelegramBot {
        ApiContextInitializer.init()
        val telegramBotApi = TelegramBotsApi()
        val telegramBot = TelegramBot()
        telegramBot.setUpdateCallback {
            val id = it?.message?.from?.id
            if (id != null) {
                val usr = telegramUserDao.getById(id.toString())
                when(usr) {
                    null -> telegramUserDao.save(TelegramUser(
                            null,
                            id.toString(),
                            it.message?.chatId!!.toString(),
                            it.message?.from!!.userName,
                            null,
                            it.message?.from!!.languageCode,
                            it.message?.from!!.languageCode,
                            true))
                }

            }
        }

        telegramBot.onDatesReceived {message ->
            telegramUserDao.getAll().forEach {
                val sm = SendMessage()
                sm.chatId = it.chatId
                sm.text = """
                    Новые даты: ${message.dates}
                """.trimIndent()
            }
        }

        try {
            telegramBotApi.registerBot(telegramBot)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
        return telegramBot
    }
}
