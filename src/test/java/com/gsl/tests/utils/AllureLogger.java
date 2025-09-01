package com.gsl.tests.utils;

import io.qameta.allure.Allure;
import java.nio.charset.StandardCharsets;

public class AllureLogger {

    public static void logRequest(String title, String request) {
        System.out.println("[REQUEST] " + title + ":\n" + request);
        Allure.addAttachment(title, "application/json", request, StandardCharsets.UTF_8.name());
    }

    public static void logResponse(String title, String response) {
        System.out.println("[RESPONSE] " + title + ":\n" + response);
        Allure.addAttachment(title, "application/json", response, StandardCharsets.UTF_8.name());
    }
}