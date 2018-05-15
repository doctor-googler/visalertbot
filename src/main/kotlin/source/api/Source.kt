package source.api

interface Source<T> {
    fun info() : T
}