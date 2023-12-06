package com.quinnheavyindustries.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static InputStream loadStream(String filename) {
        var loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(filename);
    }

    public static List<String> loadLines(String filename) {
        var loader = Thread.currentThread().getContextClassLoader();
        try (var inputStream = loader.getResourceAsStream(filename)) {
            if (inputStream != null) {
                var reader = new BufferedReader(new InputStreamReader(inputStream));
                return reader.lines().toList();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }
}
