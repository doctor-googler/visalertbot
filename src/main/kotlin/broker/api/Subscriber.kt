package broker.api

interface Subscriber<T> {
    fun notify(message: Message<T>)
}