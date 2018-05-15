package broker.api;

interface Message<T> {
    fun getVal(): T
    fun setVal(value: T)
}