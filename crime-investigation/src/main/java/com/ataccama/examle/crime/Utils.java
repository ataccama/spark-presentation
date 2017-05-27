package com.ataccama.examle.crime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final DateTimeFormatter AMERICAN_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");

    private Utils() {
    }

    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6368.607; // in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2)
                + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }

    public static String[] split(String line, String delim, String quote) {
        List<String> list = new ArrayList<>();

        String q = Pattern.quote(quote);
        String d = Pattern.quote(delim);
        String nd = "[^" + d + "]";
        String nqd = "[^" + q + d + "]";

        Pattern pattern = Pattern.compile("(" + nqd + nd + "*|" + q + ".+?" + q + "|)" + d);
        Matcher m = pattern.matcher(line + ",");
        while (m.find())
            list.add(m.group(1).replace(quote, ""));
        return list.toArray(new String[list.size()]);
    }

    public static String padLeftZeros(String str, int n) {
        return String.format("%1$" + n + "s", str).replace(' ', '0');
    }

    public static double distancePriority(int d) {
        double area = Math.PI * ((d + 1) * (d + 1) - d * d);
        return 1.0 / area;
    }

    public static void printCountMap(Map<String, Long> notJoinedCounts) {
        ArrayList<Entry<?, Long>> list = new ArrayList<>(notJoinedCounts.entrySet());
        list.sort((e1, e2) -> Long.compare(e1.getValue(), e2.getValue()));
        list.forEach(Utils::printEntry);
    }

    public static void printEntry(Entry<?, ?> entry) {
        System.out.println(entry.getKey() + " = " + entry.getValue());
    }
}
