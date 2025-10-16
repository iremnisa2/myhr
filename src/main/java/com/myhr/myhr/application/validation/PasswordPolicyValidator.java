package com.myhr.myhr.application.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


public final class PasswordPolicyValidator {

    private static final int MIN_LENGTH = 8;

    private static final Pattern UPPER = Pattern.compile("[A-Z]");
    private static final Pattern LOWER = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("\\d");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");


    private PasswordPolicyValidator() {}

    public static List<String> validate(String raw) {
        List<String> errors = new ArrayList<>();


        if (WHITESPACE.matcher(raw).find()) {
            errors.add("Şifre boşluk (space, tab vb.) içeremez.");
        }

        if (raw.length() < MIN_LENGTH) {
            errors.add("En az " + MIN_LENGTH + " karakter olmalı.");
        }
        if (!UPPER.matcher(raw).find()) {
            errors.add("En az bir büyük harf içermeli (A-Z).");
        }
        if (!LOWER.matcher(raw).find()) {
            errors.add("En az bir küçük harf içermeli (a-z).");
        }
        if (!DIGIT.matcher(raw).find()) {
            errors.add("En az bir rakam içermeli (0-9).");

        }

        return errors;
    }
}
