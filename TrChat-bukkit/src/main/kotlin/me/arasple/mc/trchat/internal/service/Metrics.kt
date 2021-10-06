package me.arasple.mc.trchat.internal.service

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.pluginVersion
import taboolib.module.metrics.Metrics
import taboolib.module.metrics.charts.SingleLineChart

/**
 * @author Arasple
 */
@PlatformSide([Platform.BUKKIT])
object Metrics {

    lateinit var metrics: Metrics
        private set

    private val counts = intArrayOf(0, 0)

    @JvmStatic
    fun increase(index: Int, value: Int = 1) {
        if (counts[index] < Int.MAX_VALUE) {
            counts[index] += value
        }
    }

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        metrics = Metrics(5802, pluginVersion, Platform.BUKKIT)
        metrics.apply {
            // 聊天次数统计
            addCustomChart(SingleLineChart("chat_counts") {
                val i = counts[0]
                counts[0] = 0
                i
            })
            // 敏感词过滤器启用统计
            addCustomChart(SingleLineChart("filter_counts") {
                val i = counts[1]
                counts[1] = 0
                i
            })
        }
    }
}