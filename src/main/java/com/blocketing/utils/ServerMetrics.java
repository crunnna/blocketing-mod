package com.blocketing.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

/**
 * Utility class for tracking server metrics such as TPS, MSPT, memory usage, CPU load, and uptime.
 * <p>
 * This class is not meant to be instantiated.
 */
public class ServerMetrics {
    // Number of ticks to average for TPS calculation
    private static final int MAX_TICKS = 100;
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final double TPS_BASE = 20.0;

    // Stores the duration (in nanoseconds) of the last MAX_TICKS ticks
    private static final long[] tickTimes = new long[MAX_TICKS];
    private static int tickIndex = 0;
    private static boolean filled = false;
    private static long lastTickTime = 0;
    private static int intervalCounter = 0;
    private static double smoothedTps = TPS_BASE;

    // Stores the server start time in milliseconds
    private static long serverStartTimeMillis = -1;

    /**
     * Registers the server tick event to track tick durations and calculate TPS.
     * Should be called during mod initialization.
     */
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Set the server start time on the first tick
            if (serverStartTimeMillis == -1) {
                serverStartTimeMillis = System.currentTimeMillis();
            }

            final long now = System.nanoTime();

            if (lastTickTime != 0) {
                final long diff = now - lastTickTime;
                tickTimes[tickIndex] = diff;
                tickIndex = (tickIndex + 1) % MAX_TICKS;
                if (tickIndex == 0) filled = true;

                intervalCounter++;
                // Update smoothed TPS every 5 ticks
                if (intervalCounter >= 5) {
                    intervalCounter = 0;
                    smoothedTps = calculateTps();
                }
            }
            lastTickTime = now;
        });
    }

    /**
     * Calculates the smoothed TPS based on the recorded tick times.
     * Averages the last MAX_TICKS tick durations and returns the TPS value.
     *
     * @return The calculated TPS value.
     */
    private static double calculateTps() {
        final int count = filled ? MAX_TICKS : tickIndex;
        if (count == 0) return TPS_BASE;
        long totalNanos = 0;
        for (int i = 0; i < count; i++) {
            totalNanos += tickTimes[i];
        }
        final double averageTickNanos = (double) totalNanos / count;
        if (averageTickNanos == 0) return TPS_BASE;
        final double tps = NANOS_PER_SECOND / averageTickNanos;
        return Math.min(tps, TPS_BASE);
    }

    /**
     * Gets the current TPS (Ticks Per Second) for the server.
     *
     * @return The smoothed TPS value.
     */
    public static double getTps() {
        return smoothedTps;
    }

    /**
     * Gets the average milliseconds per tick (MSPT) for the server.
     *
     * @param server The Minecraft server instance.
     * @return The average MSPT in milliseconds.
     */
    public static double getAverageMspt(final MinecraftServer server) {
        return server.getAverageNanosPerTick() / 1_000_000.0;
    }

    /**
     * Gets the current CPU load of the process.
     *
     * @return The CPU load as a value between 0.0 and 1.0, or -1 if not available.
     */
    public static double getProcessCpuLoad() {
        try {
            final OperatingSystemMXBean bean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            return bean.getProcessCpuLoad();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Gets the current memory usage of the server.
     *
     * @return A string representing the used and maximum memory in MB.
     */
    public static String getMemoryUsage() {
        final long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        final long max = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        return used + " / " + max + " MB";
    }

    /**
     * Gets the server uptime in milliseconds.
     * @return Uptime in milliseconds, or -1 if not started.
     */
    public static long getUptimeMillis() {
        if (serverStartTimeMillis == -1) return -1;
        return System.currentTimeMillis() - serverStartTimeMillis;
    }

    /**
     * Gets the formatted server uptime as a string.
     * The format is "HHh MMm SSs".
     *
     * @return A string representing the formatted uptime.
     */
    public static String getFormattedUptime() {
        final long uptimeMillis = getUptimeMillis();
        if (uptimeMillis < 0) return "N/A";
        final long uptimeSeconds = uptimeMillis / 1000;
        final long hours = uptimeSeconds / 3600;
        final long minutes = (uptimeSeconds % 3600) / 60;
        final long seconds = uptimeSeconds % 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }

    /**
     * Resets the server uptime. This is typically used when the server restarts.
     * Call this method to reset the uptime tracking.
     */
    public static void resetUptime() {
        serverStartTimeMillis = -1;
    }
}