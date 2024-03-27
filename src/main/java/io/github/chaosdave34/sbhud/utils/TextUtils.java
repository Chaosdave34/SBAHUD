package io.github.chaosdave34.sbhud.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of text/string related utility methods
 */
@SuppressWarnings("unused")
public class TextUtils {
    /**
     * Hypixel uses US number format.
     */
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORZ]");
    private static final Pattern NUMBERS_SLASHES = Pattern.compile("[^0-9 /]");
    private static final Pattern MAGNITUDE_PATTERN = Pattern.compile("(\\d[\\d,.]*\\d*)+([kKmMbBtT])");
    private static final Pattern TRIM_WHITESPACE_RESETS = Pattern.compile("^(?:\\s|§r)*|(?:\\s|§r)*$");
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    /**
     * Strips color codes from a given text
     *
     * @param input Text to strip colors from
     * @return Text without color codes
     */
    public static String stripColor(final String input) {
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    /**
     * Removes any character that isn't a number from a given text.
     *
     * @param text Input text
     * @return Input text with only numbers
     */
    public static String getNumbersOnly(String text) {
        return NUMBERS_SLASHES.matcher(text).replaceAll("");
    }

    /**
     * Converts all numbers with magnitudes in a given string, e.g. "10k" -> "10000" and "10M" -> "10000000." Magnitudes
     * are not case-sensitive.
     * <b>Supported magnitudes:</b>
     * <p>k - thousand</p>
     * <p>m - million</p>
     * <p>b - billion</p>
     * <p>t - trillion</p>
     * <p>
     * <p>
     * <b>Examples:</b>
     * <p>1k -> 1,000</p>
     * <p>2.5K -> 2,500</p>
     * <p>100M -> 100,000,000</p>
     *
     * @param text - Input text
     * @return Input text with converted magnitudes
     */
    public static String convertMagnitudes(String text) throws ParseException {
        Matcher matcher = MAGNITUDE_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            double parsedDouble = NUMBER_FORMAT.parse(matcher.group(1)).doubleValue();
            String magnitude = matcher.group(2).toLowerCase(Locale.ROOT);

            switch (magnitude) {
                case "k":
                    parsedDouble *= 1_000;
                    break;
                case "m":
                    parsedDouble *= 1_000_000;
                    break;
                case "b":
                    parsedDouble *= 1_000_000_000;
                    break;
                case "t":
                    parsedDouble *= 1_000_000_000_000L;
            }

            matcher.appendReplacement(sb, NUMBER_FORMAT.format(parsedDouble));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public static String abbreviate(long number) {
        if (number == Long.MIN_VALUE) return abbreviate(Long.MIN_VALUE + 1);
        if (number < 0) return "-" + abbreviate(-number);

        if (number < 1000) return String.valueOf(number);

        Map.Entry<Long, String> entry = suffixes.floorEntry(number);
        Long divideBy = entry.getKey();
        String suffix = entry.getValue();

        long truncated = number / (divideBy / 10);

        //noinspection IntegerDivisionInFloatingPointContext
        boolean hasDecimal = truncated < 100 && truncated / 10.0 != truncated / 10;
        return hasDecimal ? (truncated / 10.0) + suffix : (truncated / 10) + suffix;
    }

    public static double parseDouble(String string) {
        try {
            return TextUtils.NUMBER_FORMAT.parse(string).doubleValue();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static int parseInt(String string) {
        try {
            return TextUtils.NUMBER_FORMAT.parse(string).intValue();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static long parseLong(String string) {
        try {
            return TextUtils.NUMBER_FORMAT.parse(string).longValue();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static float parseFloat(String string) {
        try {
            return TextUtils.NUMBER_FORMAT.parse(string).floatValue();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static String fillFractionEqually(String fraction) {
        String[] splits = fraction.split("/");
        if (splits.length != 2) return fraction;

        int fraction1Length = splits[0].length();
        int fraction2Length = splits[1].length();

        StringBuilder newFraction = new StringBuilder();
        for (int i = 0; i < fraction2Length - fraction1Length; i++)
            newFraction.append(" ");

        newFraction.append(splits[0]).append("/").append(splits[1]);

        return newFraction.toString();
    }

    public static String trimWhitespaceAndResets(String input) {
        return TRIM_WHITESPACE_RESETS.matcher(input).replaceAll("");
    }
}