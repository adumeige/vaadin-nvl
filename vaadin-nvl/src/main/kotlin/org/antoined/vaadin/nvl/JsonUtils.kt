package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonArray
import elemental.json.JsonObject
import elemental.json.JsonType
import elemental.json.JsonValue

internal fun JsonObject.optString(key: String): String? =
    if (hasKey(key) && get<JsonValue>(key).getType() == JsonType.STRING) getString(key) else null

internal fun JsonObject.optDouble(key: String): Double? =
    if (hasKey(key) && get<JsonValue>(key).getType() == JsonType.NUMBER) getNumber(key) else null

internal fun JsonObject.optInt(key: String): Int? =
    optDouble(key)?.toInt()

internal fun JsonObject.optBoolean(key: String): Boolean? =
    if (hasKey(key) && get<JsonValue>(key).getType() == JsonType.BOOLEAN) getBoolean(key) else null

internal fun JsonArray.toStringList(): List<String> =
    (0 until length()).map { getString(it) }

internal fun List<JsonObject>.toJsonArray(): JsonArray =
    Json.createArray().also { arr ->
        forEachIndexed { i, obj -> arr.set(i, obj) }
    }

internal fun List<String>.toJsonStringArray(): JsonArray =
    Json.createArray().also { arr ->
        forEachIndexed { i, s -> arr.set(i, s) }
    }
