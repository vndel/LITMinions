package me.drman.litminions.extra;

import org.bukkit.ChatColor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class NumberUtil {
    private static final DecimalFormat CHANCE_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat SINGLE_DECIMAL = new DecimalFormat("#,##0.0");
    private static final NavigableMap<Long, String> SUFFIXES = new TreeMap();

    public static String format(double number) {
        return NumberFormat.getInstance(Locale.US).format(number);
    }

    public static String format(BigDecimal bigDecimal) {
        return NumberFormat.getInstance().format(bigDecimal);
    }

    public static String formatDoubleDigit(double number) {
        return CHANCE_FORMAT.format(number);
    }

    public static String formatSingleDigit(double number) {
        return SINGLE_DECIMAL.format(number);
    }

    public static String formatToSuffix(long amount) {
        if (amount < 0L) {
            return "-" + formatToSuffix(-amount);
        } else if (amount < 1000L) {
            return Long.toString(amount);
        } else {
            Map.Entry<Long, String> entry = SUFFIXES.floorEntry(amount);
            Long divideBy = (Long)entry.getKey();
            String suffix = (String)entry.getValue();
            return SINGLE_DECIMAL.format((double)amount / (double)divideBy) + suffix;
        }
    }

    public static String getProgressBar(int current, int max, int totalBars, String symbol, ChatColor completeColor, ChatColor notCompleteColor) {
        float percent = (float)current / (float)max;
        int progressBars = (int)((float)totalBars * percent);
        int leftOver = totalBars - progressBars;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(completeColor.toString());

        int i;
        for(i = 0; i < progressBars; ++i) {
            stringBuilder.append(symbol);
        }

        stringBuilder.append(notCompleteColor.toString());

        for(i = 0; i < leftOver; ++i) {
            stringBuilder.append(symbol);
        }

        return stringBuilder.toString();
    }

    public static int getPercent(int current, int max) {
        double onePercent = (double)max * 0.01D;
        return (int)((double)current / onePercent);
    }

    public static String shortenChance(double chance) {
        return CHANCE_FORMAT.format(chance);
    }

    private NumberUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        SUFFIXES.put(1000L, "k");
        SUFFIXES.put(1000000L, "M");
        SUFFIXES.put(1000000000L, "B");
        SUFFIXES.put(1000000000000L, "T");
        SUFFIXES.put(1000000000000000L, "Q");
    }
}
