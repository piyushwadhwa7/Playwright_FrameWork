package com.qa.opencart.Utilities;

import java.util.regex.Pattern;

/** Masks credentials and common personal-data fields before report/log output. */
public final class SensitiveDataMasker {

    private static final Pattern SENSITIVE_HEADER = Pattern.compile(
            "(?i)((?:authorization|cookie|set-cookie|x-api-key)\\s*[:=]\\s*(?:Bearer\\s+)?)[^,;\\r\\n}\\]]+"
    );
    private static final Pattern SENSITIVE_PARAMETER = Pattern.compile(
            "(?i)((?:token|access[_-]?token|api[_-]?key|password|email|name)=)[^&\\s,}\\]]+"
    );
    private static final Pattern SENSITIVE_JSON_FIELD = Pattern.compile(
            "(?i)(\\\"(?:token|access_token|api_key|password|email|name|cookie|set-cookie)\\\"\\s*:\\s*\\\")[^\\\"]*(\\\")"
    );
    private static final Pattern EMAIL = Pattern.compile(
            "(?i)\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b"
    );

    private SensitiveDataMasker() {
    }

    /**
     * Masks secrets in headers, URLs, JSON, and plain-text API output.
     * The method deliberately returns a safe placeholder instead of partial values.
     */
    public static String mask(String value) {
        if (value == null || value.isBlank()) {
            return value == null ? "" : value;
        }

        String masked = SENSITIVE_HEADER.matcher(value)
                .replaceAll("$1[REDACTED]");
        masked = SENSITIVE_PARAMETER.matcher(masked)
                .replaceAll("$1[REDACTED]");
        masked = SENSITIVE_JSON_FIELD.matcher(masked)
                .replaceAll("$1[REDACTED]$2");
        return EMAIL.matcher(masked).replaceAll("[REDACTED_EMAIL]");
    }
}
