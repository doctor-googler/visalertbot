import broker.api.Message
import broker.api.Subscriber
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot

class LTVisaBot: TelegramLongPollingBot(), Subscriber<Map<String, List<String>>> {

    override fun notify(message: Message<Map<String, List<String>>>) {
        alertAll(message.getVal())
    }

    val currentChats: MutableSet<Long> = HashSet()
    val name: String = "bot.name"
    val token: String = "bot.token"
    val resources: Resources = Resources()

    override fun getBotToken(): String {
        return resources.getResourse(token)
    }

    override fun onUpdateReceived(update: Update?) {
        var sendMsg: SendMessage = SendMessage()
                .setChatId(update?.message?.chatId)
                .setText("Ура! Вы добавлены к списку рассылки! \uD83D\uDE0B")
        execute(sendMsg)
        currentChats.add(update?.message?.chatId!!)
    }

    override fun getBotUsername(): String {
        return resources.getResourse(name)
    }

    fun alertAll(dates: Map<String, List<String>>) {
        val sb = StringBuilder("Появились даты! \uD83D\uDCC5 \n")
        dates.keys.forEach { key: String -> run {
            sb.append("На дату ").append(key).append(" доступно время: \n")
            dates[key]?.forEach { time: String ->  sb.append("◾ ").append(time).append("\n")}
        } }
        currentChats.forEach { chatId:Long -> run {
            execute(SendMessage()
                    .setChatId(chatId)
                    .setText(sb.toString()))
        }}
    }
}