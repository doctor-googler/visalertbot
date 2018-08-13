package consumer.api

import messaging.Message

interface IConsumer {
    fun getName(): String
    fun onMessageReceive(msg: Message)
}