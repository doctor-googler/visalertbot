import bot.service.UserService
import bot.telegram.TelegramBotFabric
import bot.viber.ViberBotFabric
import com.viber.bot.message.TextMessage
import messaging.MessageController
import source.lt.LithuanianEmbassy

fun main(args: Array<String>) {
    val telegramBot = TelegramBotFabric().generateBot()
    val viberBot = ViberBotFabric("localhost", 8086).generateBot()

    val userService = UserService()
    val messageController = MessageController()

    messageController.callbacks.add(telegramBot::alertAll)
    messageController.callbacks.add {
        viberBot.onMessageReceived { event, message, response ->
            userService.addUser(event!!.chatId!!, event!!.sender!!.name!!)
            print(message)
            response.send(TextMessage("Hi there are new dates:\n" + it.dates))
        }
    }

    val ltEmbassy = LithuanianEmbassy()
    ltEmbassy.registerConsumer(messageController)
}