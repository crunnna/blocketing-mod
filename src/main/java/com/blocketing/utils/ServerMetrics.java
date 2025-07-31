package com.blocketing.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ThreadLocalRandom;

import com.sun.management.OperatingSystemMXBean;

/**
 * Utility class for tracking server metrics such as TPS, MSPT, memory usage, CPU load, and uptime.
 *
 * TPS (Ticks Per Second) is dynamically calculated using a moving average of recent tick durations.
 * To make the TPS value smoother and more accurate—especially when tick times fluctuate rapidly—
 * an Exponential Moving Average (EMA) is applied on top of the moving average.
 */
public class ServerMetrics {

    // TPS calculation fields
    /** Number of ticks to store for TPS calculation */
    private static final int MAX_TICKS = 100;
    /** Minimum and maximum window size for averaging */
    private static final int DYNAMIC_WINDOW_MIN = 30;
    private static final int DYNAMIC_WINDOW_MAX = 200;
    /** Smoothing factor for EMA */
    private static final double ALPHA = 0.1;
    /** Stores the duration (in nanoseconds) of the last MAX_TICKS ticks */
    private static final long[] tickTimes = new long[MAX_TICKS];
    private static int tickIndex = 0;
    private static boolean tickBufferFilled = false;
    private static long lastTickTime = 0;
    private static int intervalCounter = 0;
    private static int dynamicWindow = 50;
    private static double tps = 20.0;
    private static double emaTps = 20.0;

    // Uptime fields
    /** Stores the server start time in milliseconds */
    private static long serverStartTimeMillis = -1;

    /**
     * Registers the server tick event to track tick durations and calculate TPS.
     * Should be called during mod initialization.
     */
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            setServerStartTimeIfNeeded();
            final long now = System.nanoTime();

            // Dynamic TPS calculation
            // On each tick, store the tick duration in a ring buffer. Every 5 ticks, pick a new averaging window size.
            // Calculate the average tick time over the current window, then update the TPS using an EMA for smoothing.
            if (lastTickTime != 0) {
                final long diff = now - lastTickTime;
                tickTimes[tickIndex] = diff;
                tickIndex = (tickIndex + 1) % MAX_TICKS;
                if (tickIndex == 0) tickBufferFilled = true;

                intervalCounter++;
                // Dynamically adjust the averaging window every 5 ticks
                if (intervalCounter >= 5) {
                    intervalCounter = 0;
                    dynamicWindow = ThreadLocalRandom.current().nextInt(DYNAMIC_WINDOW_MIN, DYNAMIC_WINDOW_MAX + 1);
                }

                updateTps();
            }
            lastTickTime = now;
        });
    }

    /**
     * Sets the server start time if it hasn't been set yet.
     */
    private static void setServerStartTimeIfNeeded() {
        if (serverStartTimeMillis == -1) {
            serverStartTimeMillis = System.currentTimeMillis();
        }
    }

    /**
     * Calculates the average tick time over the last 'samples' ticks.
     * @param samples Number of recent ticks to average
     * @return Average tick duration in nanoseconds
     */
    private static double calculateAverageTickTime(int samples) {
        double avg = 0;
        for (int i = 0; i < samples; i++) {
            int index = (tickIndex - i - 1 + MAX_TICKS) % MAX_TICKS;
            avg += tickTimes[index];
        }
        return samples > 0 ? avg / samples : 0;
    }

    /**
     * Updates the TPS value using an Exponential Moving Average (EMA) for smoother and more accurate results.
     * Should only be called if at least one tick has passed (lastTickTime != 0).
     */
    private static void updateTps() {
        int availableTicks = tickBufferFilled ? MAX_TICKS : tickIndex;
        int samples = Math.min(dynamicWindow, availableTicks);
        double avg = calculateAverageTickTime(samples);
        if (samples > 0 && avg > 0) {
            double mspt = avg / 1_000_000.0;
            double instantTps = msptToTps(mspt);
            tps = applyEma(instantTps, emaTps);
            emaTps = tps;
        }
    }

    /**
     * Converts milliseconds per tick (mspt) to TPS.
     * @param mspt Milliseconds per tick
     * @return Calculated TPS
     */
    private static double msptToTps(double mspt) {
        return 1000.0 / mspt;
    }

    /**
     * Applies Exponential Moving Average (EMA) smoothing.
     * @param newValue The new value to incorporate
     * @param prevEma The previous EMA value
     * @return The updated EMA value
     */
    private static double applyEma(double newValue, double prevEma) {
        return ALPHA * newValue + (1 - ALPHA) * prevEma;
    }

    /**
     * Gets the current TPS (Ticks Per Second) for the server.
     * @return The smoothed TPS value, rounded to two decimals
     */
    public static double getTps() {
        return Math.round(tps * 100.0) / 100.0;
    }

    /**
     * Gets the average milliseconds per tick (MSPT) for the server.
     * @param server The Minecraft server instance
     * @return The average MSPT in milliseconds
     */
    public static double getAverageMspt(final MinecraftServer server) {
        return server.getAverageNanosPerTick() / 1_000_000.0;
    }

    /**
     * Gets the current CPU load of the process.
     * @return The CPU load as a value between 0.0 and 1.0, or -1 if not available
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
     * @return A string representing the used and maximum memory in MB
     */
    public static String getMemoryUsage() {
        final long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        final long max = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        return used + " / " + max + " MB";
    }

    /**
     * Gets the server uptime in milliseconds.
     * @return Uptime in milliseconds, or -1 if not started
     */
    public static long getUptimeMillis() {
        if (serverStartTimeMillis == -1) return -1;
        return System.currentTimeMillis() - serverStartTimeMillis;
    }

    /**
     * Gets the formatted server uptime as a string.
     * The format is "HHh MMm SSs".
     * @return A string representing the formatted uptime
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
}