@file:JvmName("MainKt")
package xyz.acrylicstyle.discordchannelloader

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.NON_PRIVILEGED
import dev.kord.gateway.PrivilegedIntent
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DiscordChannelLoader")
private val channels =
    System.getenv("CHANNELS")?.split(",")?.map { s -> Snowflake(s) }?.toSet()
        ?: setOf(Snowflake("725947473452990485"), Snowflake("724325894784548936"))

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val client = Kord(System.getenv("TOKEN"))
    System.getenv("SECRET") ?: error("SECRET is not set")

    client.on<ReadyEvent> {
        logger.info("Logged in as ${client.getSelf().username}")
        if (System.getenv("LOAD_ALL") != null) {
            channels.forEach { channelId ->
                val channel = client.getChannelOf<TopGuildMessageChannel>(channelId) ?: return@forEach
                println("Starting fetching messages from ${channel.name}")
                val list = mutableListOf<Message>()
                channel.getMessagesBefore(Snowflake.max).collect {
                    list += it
                    if (list.size >= 100) {
                        println("Inserting ${list.size} messages")
                        println(Util.insert(list))
                        list.clear()
                    }
                }
                if (list.isNotEmpty()) {
                    println("Inserting ${list.size} messages")
                    println(Util.insert(list))
                }
            }
        }
    }

    client.on<MessageCreateEvent> {
        if (message.channelId !in channels) return@on
        Util.insert(message)
    }

    client.login {
        intents {
            +Intents.NON_PRIVILEGED
            +Intent.MessageContent
        }
    }
}
