package org.petrowich.gallerychecker.utils;

import com.google.common.base.Joiner;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHelper {

    private HttpRequestHelper() {
    }

    public static String parametersToString(HttpServletRequest request) {
        Map<String, String> paramsMap = request.getParameterMap().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.toString(entry.getValue())));
        return Joiner.on(", ").withKeyValueSeparator("=").join(paramsMap);
    }

    public static Integer parseNumberOrDefault(String attributeValue, Integer defaultValue) {
        try {
            return Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
