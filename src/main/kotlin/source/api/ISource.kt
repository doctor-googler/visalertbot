package source.api

import consumer.api.IConsumer

interface ISource {
    fun getName(): String
    fun info(): Map<String, List<String>>
    fun registerConsumer(consumer: IConsumer)
    fun unregisterConsumer(consumer: IConsumer)
}