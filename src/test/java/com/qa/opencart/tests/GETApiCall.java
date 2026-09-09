package com.qa.opencart.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.qa.opencart.Utilities.SensitiveDataMasker;
import io.qameta.allure.Allure;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class GETApiCall {
    @Test
    public void getUsersApiTest(){
        Playwright playwright=Playwright.create();
        APIRequest request=playwright.request();
        APIRequestContext requestContext=request.newContext();
        String requestUrl = "https://gorest.co.in/public/v2/users";
        APIResponse apiResponse=requestContext.get(requestUrl);
        Allure.addAttachment(
                "Playwright API request and response",
                "text/plain",
                "Request: GET " + SensitiveDataMasker.mask(requestUrl)
                        + "\nResponse status: " + apiResponse.status()
                        + "\nResponse headers: " + SensitiveDataMasker.mask(String.valueOf(apiResponse.headers()))
                        + "\nResponse body: " + SensitiveDataMasker.mask(
                        new String(apiResponse.body(), StandardCharsets.UTF_8))
        );
        int statusCode=apiResponse.status();
        System.out.println("API Status code: "+statusCode);
        Assert.assertEquals(statusCode,200);
        Assert.assertEquals(apiResponse.ok(),true);
        Assert.assertTrue(apiResponse.ok());
        String statusReponseText=apiResponse.statusText();
        System.out.println("API Status text: "+statusReponseText);
        //apiResponse.body();
        ObjectMapper mapper=new ObjectMapper();
        try {
            JsonNode jsonResponse=mapper.readTree(apiResponse.body());
            String jsonPrettyResponse=jsonResponse.toPrettyString();
            System.out.println("JSON Pretty Response: "+jsonPrettyResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("--------------print API URL ----------");
        System.out.println(apiResponse.url());
        System.out.println("--------------print Response header ----------");
        Map<String ,String> headerMap=apiResponse.headers();
        System.out.println("Headers:"+ headerMap);
        Assert.assertEquals(headerMap.get("content-type"), "application/json; charset=utf-8");
    }
}
