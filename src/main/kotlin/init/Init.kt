package init

import bot.telegram.TelegramBotFabric
import bot.viber.ViberBotFabric
import dao.TelegramUserDao
import dao.ViberUserDao
import messaging.MessageController
import org.litote.kmongo.KMongo
import source.lt.LithuanianEmbassy

fun main(args: Array<String>) {
    val mongoClient = KMongo.createClient()
    val telegramBot = TelegramBotFabric(TelegramUserDao(mongoClient)).generateBot()
    val viberBot = ViberBotFabric(ViberUserDao(mongoClient)).generateBot()

    val messageController = MessageController()

    messageController.callbacks.add(telegramBot::datesReceived)
    messageController.callbacks.add(viberBot::datesReceived)

    val ltEmbassy = LithuanianEmbassy()
    ltEmbassy.registerConsumer(messageController)
}