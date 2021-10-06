package me.arasple.mc.trchat.common.chat

import me.arasple.mc.trchat.api.TrChatFiles.settings
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.getDataFolder
import taboolib.common.util.replaceWithOrder
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

/**
 * @author Arasple
 * @date 2019/11/30 16:08
 */
@PlatformSide([Platform.BUKKIT])
object ChatLogs {

    private val waveList = mutableListOf<String>()
    private val dateFormat0 = SimpleDateFormat("yyyy-MM-dd")
    private val dateFormat1 = SimpleDateFormat("HH:mm:ss")

    @Schedule(delay = 20 * 15, period = 20 * 60 * 5, async = true)
    @Awake(LifeCycle.DISABLE)
    fun writeToFile() {
        val logFile = newFile(File(getDataFolder(), "logs"), "${dateFormat0.format(System.currentTimeMillis())}.txt", create = true)
        logFile.writer().use {
            waveList.forEach { line ->
                it.write(line)
                it.appendLine()
            }
        }
        waveList.clear()
    }

    @Schedule(delay = 20 * 15, period = 20 * 60 * 5, async = true)
    fun autoDelete() {
        val days = settings.getLong("GENERAL.LOG-DELETE-TIME", 0L)
        if (days > 0) {
            val millis = TimeUnit.DAYS.toMillis(days)
            kotlin.runCatching {
                File(getDataFolder(), "logs").walk().forEach {
                    if (it.lastModified() < (System.currentTimeMillis() - millis)) {
                        it.delete()
                    }
                }
            }
        }
    }

    fun log(player: Player, originalMessage: String) {
        waveList.add(
            settings.getString("GENERAL.LOG-NORMAL", "[{0}] {1}: {2}").replaceWithOrder(
                dateFormat1.format(System.currentTimeMillis()),
                player.name,
                originalMessage
            )
        )
    }

    fun logPrivate(from: String, to: String, originalMessage: String) {
        waveList.add(
            settings.getString("GENERAL.LOG-PRIVATE", "[{0}] {1} -> {2}: {3}").replaceWithOrder(
                dateFormat1.format(System.currentTimeMillis()),
                from,
                to,
                originalMessage
            )
        )
    }
}