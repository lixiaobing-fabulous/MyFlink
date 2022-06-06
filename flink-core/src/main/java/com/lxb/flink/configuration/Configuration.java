package com.lxb.flink.configuration;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Configuration {
    protected final HashMap<String, Object> confData;

    public Configuration() {
        this.confData = new HashMap<>();
    }

    public Map<String, String> toMap() {
        synchronized (this.confData) {
            Map<String, String> ret = new HashMap<>(this.confData.size());
            for (Map.Entry<String, Object> entry : confData.entrySet()) {
                ret.put(entry.getKey(), convertToString(entry.getValue()));
            }
            return ret;
        }
    }

    private String convertToString(Object o) {
        if (o.getClass() == String.class) {
            return (String) o;
        } else if (o.getClass() == Duration.class) {
            Duration duration = (Duration) o;
            return String.format("%d ns", duration.toNanos());
        } else if (o instanceof List) {
            return ((List<?>) o).stream()
                    .map(e -> escapeWithSingleQuote(convertToString(e), ";"))
                    .collect(Collectors.joining(";"));
        } else if (o instanceof Map) {
            return ((Map<?, ?>) o).entrySet().stream()
                    .map(e -> {
                        String escapedKey   = escapeWithSingleQuote(e.getKey().toString(), ":");
                        String escapedValue = escapeWithSingleQuote(e.getValue().toString(), ":");

                        return escapeWithSingleQuote(escapedKey + ":" + escapedValue, ",");
                    })
                    .collect(Collectors.joining(","));
        }

        return o.toString();
    }

    static String escapeWithSingleQuote(String string, String... charsToEscape) {
        boolean escape = Arrays.stream(charsToEscape).anyMatch(string::contains)
                || string.contains("\"") || string.contains("'");

        if (escape) {
            return "'" + string.replaceAll("'", "''") + "'";
        }

        return string;
    }

}
