package com.sedsoftware.udacity.popularmovies.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class TextFormatUtils {

    /**
     * Builds date string in "day month, year" format.
     *
     * @param rawDate Raw date string extracted from json response data.
     * @return Date string.
     */

    public static String getFormattedDate(String rawDate) {

        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date releaseDate = new Date();

        try {
            releaseDate = oldFormat.parse(rawDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("d MMMM, yyyy", Locale.US);

        return newFormat.format(releaseDate);
    }

    /**
     * Builds rating string in "X/10" format.
     *
     * @param rating Current rating.
     * @param suffix Appended suffix.
     * @return Formatted rating string.
     */
    public static String getFormattedRating(double rating, String suffix) {
        return String.format("%-2.1f" + suffix, rating);
    }

    /**
     * Builds shortened version of the review text.
     *
     * @param source Full review text.
     * @return Shortened text.
     */

    public static String shortenReviewText(String source) {

        final int MINIMUM_PREVIEW_TEXT_LENGTH = 150;

        String[] items = source.split("\\r\\n\\r\\n");
        ArrayList<String> list = new ArrayList<>(Arrays.asList(items));
        int currentLength = 0;
        String result = "";

        for (String s : list) {
            currentLength += s.length();
            result += s;
            result += "\r\n";

            if (currentLength > MINIMUM_PREVIEW_TEXT_LENGTH) {
                break;
            }
        }

        return result;
    }
}
