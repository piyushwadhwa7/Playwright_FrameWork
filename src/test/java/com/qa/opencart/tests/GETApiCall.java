package com.qa.opencart.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

public class GETApiCall {
    @Test
    public void getUsersApiTest(){
        Playwright playwright=Playwright.create();
        APIRequest request=playwright.request();
        APIRequestContext requestContext=request.newContext();
        APIResponse apiResponse=requestContext.get("https://gorest.co.in/public/v2/users");
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
