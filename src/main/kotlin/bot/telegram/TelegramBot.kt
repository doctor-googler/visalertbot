package bot.telegram

import init.Resources
import messaging.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot

class TelegramBot: TelegramLongPollingBot() {
    private val botName: String
    private val token: String
    private var updateCallBack: ((Update?) -> Unit)? = null
    private var datesCallback: ((Message) -> Unit)? = null

    init {
        val resources = Resources()
        token = resources.getResourse("telegram.bot.token")
        botName = resources.getResourse("telegram.bot.name")
    }

    fun setUpdateCallback(callback: (Update?) -> Unit) {
        this.updateCallBack = callback
    }

    override fun getBotToken(): String {
        return token
    }

    override fun getBotUsername(): String {
        return botName
    }

    override fun onUpdateReceived(update: Update?) {
        this.updateCallBack?.invoke(update)
    }

    fun onDatesReceived(callback: (Message) -> Unit) {
        this.datesCallback = callback
    }

    fun datesReceived(msg: Message) {
        this.datesCallback?.invoke(msg)
    }
}