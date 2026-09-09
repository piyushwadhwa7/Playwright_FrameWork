package com.qa.opencart.listners;

import com.qa.opencart.Utilities.SensitiveDataMasker;
import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/** Captures masked Rest Assured request/response exchanges in Allure. */
public class AllureRestAssuredFilter implements Filter {

    @Override
    public Response filter(
            FilterableRequestSpecification request,
            FilterableResponseSpecification responseSpecification,
            FilterContext context) {

        Object requestBody = request.getBody();
        String requestDetails = "Request: " + request.getMethod() + " "
                + SensitiveDataMasker.mask(request.getURI()) + "\n"
                + "Headers: " + SensitiveDataMasker.mask(String.valueOf(request.getHeaders())) + "\n"
                + "Body: " + SensitiveDataMasker.mask(String.valueOf(requestBody));

        try {
            Response response = context.next(request, responseSpecification);
            String exchange = requestDetails + "\n\n"
                    + "Response status: " + response.getStatusCode() + "\n"
                    + "Response headers: " + SensitiveDataMasker.mask(String.valueOf(response.getHeaders())) + "\n"
                    + "Response body: " + SensitiveDataMasker.mask(response.asString());
            attach(exchange);
            return response;
        } catch (RuntimeException exception) {
            attach(requestDetails + "\n\nRequest failed: " + exception.getMessage());
            throw exception;
        }
    }

    private static void attach(String exchange) {
        try {
            Allure.addAttachment("API request and response", "text/plain", exchange);
        } catch (RuntimeException attachmentFailure) {
            System.err.println("Unable to attach API exchange to Allure: "
                    + attachmentFailure.getMessage());
        }
    }
}
