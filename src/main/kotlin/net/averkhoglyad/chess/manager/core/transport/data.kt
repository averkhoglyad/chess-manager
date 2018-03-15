package net.averkhoglyad.chess.manager.core.transport

import org.apache.http.HttpHeaders
import org.apache.http.client.methods.*
import org.apache.http.client.utils.URIBuilder
import java.net.URI
import java.util.function.Supplier
import kotlin.reflect.KClass

interface Operation {
    val url: String
    val verb: Verb
    val requestClass: KClass<*>
    val responseClass: KClass<*>
}

class Request private constructor(val operation: Operation,
                                  val body: Any?,
                                  val pathVariables: Map<String, String>,
                                  val queryParams: Map<String, List<String>>,
                                  val headers: Map<String, List<String>>) {

    val url: URI
        get() {
            var resultPath = operation.url
            for ((key, value) in pathVariables) {
                resultPath = resultPath.replace("{$key}", value)
            }

            val builder = URIBuilder(resultPath)
            queryParams.entries
                    .flatMap { entry -> entry.value.map { value -> Pair(entry.key, value) } }
                    .forEach { (name, value) -> builder.addParameter(name, value) }

            return builder.build()
        }

    // TODO: Replace with something more Kotlin-like builder
    class Builder(private val operation: Operation) {
        private var authorization: String? = null
        private var body: Any? = null
        private val pathVariables = mutableMapOf<String, String>()
        private val queryParams = mutableMapOf<String, MutableList<String>>()
        private val headers = mutableMapOf<String, MutableList<String>>()

        fun build(): Request {
            authorization?.let {
                header(HttpHeaders.AUTHORIZATION, it)
            }
            return Request(operation, body, pathVariables, queryParams, headers)
        }

        fun authorization(authorization: String, type: String = "Bearer"): Builder {
            if (type.isEmpty()) throw IllegalArgumentException()
            this.authorization = "$type $authorization"
            return this
        }

        fun body(body: Any): Builder {
            this.body = body
            return this
        }

        fun pathVariable(key: String, raw: Any = ""): Builder {
            if (key.isEmpty()) throw IllegalArgumentException()
            pathVariables[key] = raw.toString()
            return this
        }

        fun header(key: String, vararg raw: Any): Builder {
            if (key.isEmpty()) throw IllegalArgumentException()
            headers.putIfAbsent(key, mutableListOf())
            raw.forEach {
                headers[key]!!.add(it.toString())
            }
            return this
        }

        fun addHeader(key: String, raw: Any = ""): Builder {
            if (key.isEmpty()) throw IllegalArgumentException()
            headers.putIfAbsent(key, mutableListOf())
            headers[key]!!.add(raw.toString())
            return this
        }

        fun queryParam(key: String, vararg raw: Any): Builder {
            if (key.isEmpty()) throw IllegalArgumentException()
            queryParams.putIfAbsent(key, mutableListOf())
            raw.forEach {
                queryParams[key]!!.add(it.toString())
            }
            return this
        }

        fun addQueryParam(key: String, raw: Any = ""): Builder {
            if (key.isEmpty()) throw IllegalArgumentException()
            queryParams.putIfAbsent(key, mutableListOf())
            queryParams[key]!!.add(raw.toString())
            return this
        }

    }

    companion object {
        fun operation(operation: Operation): Builder {
            return Builder(operation)
        }
    }

}

enum class Verb : Supplier<HttpRequestBase> {

    GET {
        override fun get(): HttpGet {
            return HttpGet()
        }
    },
    HEAD {
        override fun get(): HttpHead {
            return HttpHead()
        }
    },
    POST {
        override fun get(): HttpPost {
            return HttpPost()
        }
    },
    PUT {
        override fun get(): HttpPut {
            return HttpPut()
        }
    },
    PATCH {
        override fun get(): HttpPatch {
            return HttpPatch()
        }
    },
    DELETE {
        override fun get(): HttpDelete {
            return HttpDelete()
        }
    },
    OPTIONS {
        override fun get(): HttpOptions {
            return HttpOptions()
        }
    },
    TRACE {
        override fun get(): HttpTrace {
            return HttpTrace()
        }
    }

}