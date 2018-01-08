import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot

class LTVisaBot: TelegramLongPollingBot() {
    val name: String = "bot.name"
    val token: String = "bot.token"
    val resources: Resources = Resources()

    override fun getBotToken(): String {
        return resources.getResourse(token)
    }

    override fun onUpdateReceived(update: Update?) {
        var sendMsg: SendMessage = SendMessage()
                .setChatId(update?.message?.chatId)
                .setText(update?.message?.text)
        execute(sendMsg);
    }

    override fun getBotUsername(): String {
        return resources.getResourse(name)
    }
}