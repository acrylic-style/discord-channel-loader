package xyz.acrylicstyle.discordchannelloader

import dev.kord.core.entity.Message
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.text.SimpleDateFormat
import java.util.*

/**
 * Class Util provides utility methods for inserting data into a remote server.
 */
object Util {
    private val endpoint = System.getenv("ENDPOINT") ?: "http://192.168.100.109:8084/insert"
    private val httpClient = HttpClient(CIO)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    /**
     * Inserts the given content as a POST request to the specified endpoint.
     *
     * @param content the content to be inserted
     * @throws IllegalStateException if SECRET environment variable is not set
     */
    @Deprecated("Use insert(message: Message) instead")
    suspend fun insert(content: String) =
        httpClient.post(endpoint) {
            header("Authorization", System.getenv("SECRET") ?: error("SECRET is not set"))
            header("Content-Type", "application/json")
            setBody(Json.encodeToString(listOf(mapOf("text" to content, "id" to UUID.randomUUID().toString()))))
        }.bodyAsText()

    /**
     * Inserts a list of messages into the specified endpoint.
     *
     * @param list the list of messages to insert
     * @return the response from the endpoint as a string
     */
    suspend fun insert(list: List<Message>): String =
        httpClient.post(endpoint) {
            header("Authorization", System.getenv("SECRET") ?: error("SECRET is not set"))
            header("Content-Type", "application/json")
            setBody(Json.encodeToString(JsonArray(list.map {
                JsonObject(
                    mapOf(
                        "pageContent" to JsonPrimitive(it.content),
                        "text" to JsonPrimitive(toContent(it)),
                        "id" to JsonPrimitive(UUID.randomUUID().toString()),
                        "metadata" to JsonObject(mapOf("timestamp" to JsonPrimitive(it.timestamp.toEpochMilliseconds())))
                    )
                )
            })))
        }.bodyAsText()

    suspend fun insert(message: Message) =
        httpClient.post(endpoint) {
            header("Authorization", System.getenv("SECRET") ?: error("SECRET is not set"))
            header("Content-Type", "application/json")
            setBody(Json.encodeToString(JsonArray(listOf(JsonObject(mapOf(
                "pageContent" to JsonPrimitive(message.content),
                "text" to JsonPrimitive(toContent(message)),
                "id" to JsonPrimitive(UUID.randomUUID().toString()),
                "metadata" to JsonObject(mapOf(
                    "timestamp" to JsonPrimitive(message.timestamp.toEpochMilliseconds())
                ))))
            ))))
        }.bodyAsText()

    /**
     * Converts a [Message] to a formatted content string.
     *
     * @param message the message to convert
     * @return the formatted content string
     */
    suspend fun toContent(message: Message): String = """
[Author]
${message.author?.username ?: "Unknown"}

[Timestamp]
${dateFormat.format(message.timestamp.toEpochMilliseconds())}

[Attachments]
- ${message.attachments.joinToString("\n- ") { it.filename }}

[URL]
https://discord.com/channels/${message.getGuildOrNull()?.id ?: "Unknown"}/${message.channelId}/${message.id}

[Content]
${message.content}
""".trimIndent()
}
