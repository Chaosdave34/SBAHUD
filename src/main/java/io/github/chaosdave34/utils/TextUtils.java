package io.github.chaosdave34.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of text/string related utility methods
 */
public class TextUtils {
    /**
     * Hypixel uses US number format.
     */
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORZ]");
    private static final Pattern NUMBERS_SLASHES = Pattern.compile("[^0-9 /]");
    private static final Pattern MAGNITUDE_PATTERN = Pattern.compile("(\\d[\\d,.]*\\d*)+([kKmMbBtT])");
    private static final NavigableMap<Integer, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000, "k");
        suffixes.put(1_000_000, "M");
        suffixes.put(1_000_000_000, "B");
        NUMBER_FORMAT.setMaximumFractionDigits(2);
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
     *
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





}