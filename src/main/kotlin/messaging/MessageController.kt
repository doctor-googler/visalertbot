package messaging

import consumer.api.IConsumer

class MessageController: IConsumer {
    val callbacks: MutableList<(Message) -> Unit>

    init {
        this.callbacks = ArrayList()
    }

    override fun getName(): String {
        return "MessageController"
    }

    override fun onMessageReceive(msg: Message) {
        callbacks.forEach { c -> c(msg) }
    }
}