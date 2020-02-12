package com.anfantanion.traintimes1.repositories

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.MalformedJsonException
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * https://stackoverflow.com/questions/43412261/make-gson-accept-single-objects-where-it-expects-arrays
 */
class SingleObjectToListFactory  : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
        if (!MutableList::class.java.isAssignableFrom(type!!.rawType)) return null

        var elementType : Type = Any::class.java
        if (type is ParameterizedType) elementType = type.actualTypeArguments[0]

        val elementTypeAdapter =
            gson!!.getAdapter(TypeToken.get(elementType)) as TypeAdapter<T>

        return SingleObjectToList2TypeAdapter(elementTypeAdapter) as TypeAdapter<T>


    }

    class SingleObjectToList2TypeAdapter<E> constructor(val elementTypeAdapter: TypeAdapter<E>) :
        TypeAdapter<List<E>?>() {
        override fun write(
            out: JsonWriter,
            list: List<E>?
        ) {
            throw UnsupportedOperationException()
        }

        @Throws(IOException::class)
        override fun read(`in`: JsonReader): List<E>? { // This is where we detect the list "type"
            val list: MutableList<E> = ArrayList()
            val token = `in`.peek()
            when (token) {
                JsonToken.BEGIN_ARRAY -> {
                    // If it's a regular list, just consume [, <all elements>, and ]
                    `in`.beginArray()
                    while (`in`.hasNext()) {
                        list.add(elementTypeAdapter.read(`in`))
                    }
                    `in`.endArray()
                }
                JsonToken.BEGIN_OBJECT, JsonToken.STRING, JsonToken.NUMBER, JsonToken.BOOLEAN ->  // An object or a primitive? Just add the current value to the result list
                    list.add(elementTypeAdapter.read(`in`))
                JsonToken.NULL -> throw AssertionError("Must never happen: check if the type adapter configured with .nullSafe()")
                JsonToken.NAME, JsonToken.END_ARRAY, JsonToken.END_OBJECT, JsonToken.END_DOCUMENT -> throw MalformedJsonException(
                    "Unexpected token: $token"
                )
                else -> throw AssertionError("Must never happen: $token")
            }
            return list
        }

    }


}