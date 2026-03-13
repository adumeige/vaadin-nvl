package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonArray
import elemental.json.JsonObject
import elemental.json.JsonType
import elemental.json.JsonValue

/**
 * Safely extracts a [String] value from this [JsonObject], returning `null` if
 * the key is missing or the value is not a string.
 */
internal fun JsonObject.optString(key: String): String? =
    if (hasKey(key) && get<JsonValue>(key).getType() == JsonType.STRING) getString(key) else null

/**
 * Safely extracts a [Double] value from this [JsonObject], returning `null` if
 * the key is missing or the value is not a number.
 */
internal fun JsonObject.optDouble(key: String): Double? =
    if (hasKey(key) && get<JsonValue>(key).getType() == JsonType.NUMBER) getNumber(key) else null

/** Safely extracts an [Int] value, delegating to [optDouble] and converting. */
internal fun JsonObject.optInt(key: String): Int? =
    optDouble(key)?.toInt()

/**
 * Safely extracts a [Boolean] value from this [JsonObject], returning `null` if
 * the key is missing or the value is not a boolean.
 */
internal fun JsonObject.optBoolean(key: String): Boolean? =
    if (hasKey(key) && get<JsonValue>(key).getType() == JsonType.BOOLEAN) getBoolean(key) else null

/** Converts this [JsonArray] of strings into a [List] of [String]. */
internal fun JsonArray.toStringList(): List<String> =
    (0 until length()).map { getString(it) }

/** Converts a list of [JsonObject]s into a [JsonArray]. */
internal fun List<JsonObject>.toJsonArray(): JsonArray =
    Json.createArray().also { arr ->
        forEachIndexed { i, obj -> arr.set(i, obj) }
    }

/** Converts a list of [String]s into a [JsonArray] of JSON string values. */
internal fun List<String>.toJsonStringArray(): JsonArray =
    Json.createArray().also { arr ->
        forEachIndexed { i, s -> arr.set(i, s) }
    }
