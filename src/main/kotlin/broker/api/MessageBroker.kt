package broker.api

import source.api.Source

interface MessageBroker<T> {
    fun addSource(source: Source<T>)
    fun removeSource(source: Source<T>)
    fun subscribe(subscriber: Subscriber<T>)
    fun unsubscribe(subscriber: Subscriber<T>)
}