/*
 * Copyright (c) 2020.
 */

package me.sul.crackshotaddition.util

import org.bukkit.Bukkit
import java.util.logging.Level

object DebugUtil {
    fun printStackTrace(message: String?) {
        val stElements = Thread.currentThread().stackTrace
        Bukkit.getLogger().log(Level.WARNING, "§cFrom DebugUtil: ")
        if (message != null) {
            Bukkit.getLogger().log(Level.WARNING, "§cMessage: $message")
            Bukkit.getLogger().log(Level.WARNING, "")
            for (i in 1 until stElements.size) {
                val ste = stElements[i]
                Bukkit.getLogger().log(Level.WARNING, "${stElements[i]}")
            }
        }
    }
}
