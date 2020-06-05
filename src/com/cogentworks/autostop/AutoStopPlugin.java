package com.cogentworks.autostop;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AutoStopPlugin extends JavaPlugin {

    public void onEnable() {

        saveDefaultConfig();


        String[] times = getConfig().getString("time").split(":");
        String[] warningTimes = getConfig().getString("warning-time").split(":");

        Calendar now = new GregorianCalendar();
        Calendar next = new GregorianCalendar();
        Calendar warning;

        // Set the next execution time
        next.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
        next.set(Calendar.MINUTE, Integer.parseInt(times[1]));
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);

        // If after specified time, set next execution for next day
        if (now.after(next)) {
            next.add(Calendar.DATE, 1); // Add a day
        }

        warning = (Calendar) next.clone();
        warning.add(Calendar.MINUTE, Integer.parseInt(warningTimes[0]) * -1);
        warning.add(Calendar.SECOND, Integer.parseInt(warningTimes[1]) * -1);


        // Ticks until execution
        long ticks = (next.getTimeInMillis() - now.getTimeInMillis()) / 1000 * 20;
        long warningTicks = (warning.getTimeInMillis() - now.getTimeInMillis()) / 1000 * 20;

        // Schedule WARNING
        new BukkitRunnable() {
            @Override
            public void run() {
                getServer().dispatchCommand(getServer().getConsoleSender(), "title @a times 20 80 20");
                getServer().dispatchCommand(getServer().getConsoleSender(), "title @a subtitle {\"text\":\"" + getConfig().getString("warning-msg") + "\"}");
                getServer().dispatchCommand(getServer().getConsoleSender(), "title @a title {\"text\":\"WARNING\",\"bold\":true,\"color\":\"red\"}");
            }
        }.runTaskTimer(this, warningTicks, 24 * 60 * 60 * 20);

        // Schedule STOP
        new BukkitRunnable() {
            @Override
            public void run() {
                getServer().getConsoleSender().sendMessage("Shutting down server...");
                getServer().dispatchCommand(getServer().getConsoleSender(), "stop");
            }
        }.runTaskTimer(this, ticks, 24 * 60 * 60 * 20); // Repeat again in 24h
    }
}
