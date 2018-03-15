package net.averkhoglyad.chess.manager.core.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.lang.reflect.ParameterizedType
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

// Slf4j
class Slf4j private constructor(private val name: String?, private val clazz: Class<*>?) {

    constructor() : this(name = null, clazz = null)
    constructor(name: String) : this(name = name, clazz = null)
    constructor(clazz: KClass<*>) : this(name = null, clazz = clazz.java)
    constructor(clazz: Class<*>) : this(name = null, clazz = clazz)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger = when {
        name != null -> LoggerFactory.getLogger(name)
        clazz != null -> LoggerFactory.getLogger(clazz)
        else -> LoggerFactory.getLogger(thisRef!!::class.java)
    }

}

// Generics
@Suppress("UNCHECKED_CAST")
fun <T : Any> parseGenericClass(entity: KClass<*>): Class<T> {
    val type = (entity.java.genericSuperclass as ParameterizedType).actualTypeArguments[0]
    if (type is ParameterizedType) return type.rawType as Class<T>
    return type as Class<T>
}

// HEX
fun Long.toHex(): String = java.lang.Long.toHexString(this)
fun Long.toBin(): String = java.lang.Long.toBinaryString(this)

fun Int.toHex(): String = java.lang.Integer.toHexString(this)
fun Int.toBin(): String = java.lang.Integer.toBinaryString(this)

// Collections
inline fun <reified E : Enum<E>> emptyEnumSet(): EnumSet<E> = EnumSet.noneOf(E::class.java)
inline fun <reified E : Enum<E>> enumSetOf(vararg elements: E): EnumSet<E> = if (elements.isNotEmpty()) EnumSet.of(elements[0], *elements) else emptyEnumSet()
inline fun <reified E : Enum<E>> enumSetAll(): EnumSet<E> = EnumSet.allOf(E::class.java)

inline fun <reified K : Enum<K>, V: Any> emptyMapOf(): EnumMap<K, V> = EnumMap<K, V>(K::class.java)
inline fun <reified K : Enum<K>, V: Any> enumMapOf(pair: Pair<K, V>): EnumMap<K, V> = EnumMap<K, V>(K::class.java).apply { this.put(pair.first, pair.second) }
inline fun <reified K : Enum<K>, V : Any> enumMapOf(vararg pairs: Pair<K, V>): EnumMap<K, V> = EnumMap<K, V>(K::class.java).apply { pairs.forEach { pair -> this[pair.first] = pair.second } }

// Stream API
inline fun <T> Stream<T>.toList() = this.collect(Collectors.toList())
inline fun <T> Stream<T>.toSet() = this.collect(Collectors.toSet())
inline fun <T> Stream<T>.toCollection(crossinline factory: () -> Collection<T>) = this.collect(Collectors.toCollection { factory() })

inline fun <T> Iterator<T>.asIterable(): Iterable<T> = object : Iterable<T> {
    override fun iterator(): Iterator<T> = this@asIterable
}

inline fun <T> Iterator<T>.toStream(): Stream<T> = this.asIterable().toStream()
inline fun <T> Iterable<T>.toStream(): Stream<T> = this.spliterator().toStream()
inline fun <T> Spliterator<T>.toStream(): Stream<T> = StreamSupport.stream(this, false)

// Assertions
inline infix fun Boolean.orThrow(error: String) {
    if (!this) throw IllegalArgumentException(error)
}

inline infix fun Boolean.orThrow(errorFn: () -> String) {
    if (!this) throw IllegalArgumentException(errorFn())
}

inline infix fun Boolean.orElse(elseFn: () -> String) {
    elseFn()
}

// NOOPs
val noop0: () -> Unit = {}
val noop1: (_1: Any?) -> Unit = {}
val noop2: (_1: Any?, _2: Any?) -> Unit = { _, _ -> }
val noop3: (_1: Any?, _2: Any?, _3: Any?) -> Unit = { _, _, _ -> }
val noop4: (_1: Any?, _2: Any?, _3: Any?, _4: Any?) -> Unit = { _, _, _, _ -> }
val noop5: (_1: Any?, _2: Any?, _3: Any?, _4: Any?, _5: Any?) -> Unit = { _, _, _, _, _ -> }
