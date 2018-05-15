package broker.impl

import broker.api.MessageBroker
import broker.api.Subscriber
import source.api.Source
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class DateBroker: MessageBroker<Map<String, List<String>>> {
    private val sources: MutableList<Source<Map<String, List<String>>>> = ArrayList()
    private val subscribers: MutableList<Subscriber<Map<String, List<String>>>> = ArrayList()

    override fun addSource(source: source.api.Source<Map<String, List<String>>>) {
        sources.add(source)
    }

    override fun removeSource(source: source.api.Source<Map<String, List<String>>>) {
        sources.remove(source)
    }

    override fun subscribe(subscriber: Subscriber<Map<String, List<String>>>) {
        subscribers.add(subscriber)
    }

    override fun unsubscribe(subscriber: Subscriber<Map<String, List<String>>>) {
        subscribers.remove(subscriber)
    }

    fun run() {
        ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(
                        {
                            sources
                                    .map { s -> s.info() }
                                    .forEach { i -> subscribers.forEach { sub -> sub.notify(ScheduleMessage(i)) }}
                        },
                        0,
                        2 * 60,
                        TimeUnit.SECONDS)
    }
}