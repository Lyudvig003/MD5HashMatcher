package org.wiremock.dataprovider;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.tngtech.java.junit.dataprovider.DataProvider;

import java.util.Map;

public class MD5HashDataProvider {

    @DataProvider
    public static Object[][] MD5HashDataProviderForCheck() {
        return new Object[][]{
            {
                // Request Body
                "{\n" +
                    "  \"Lang\": \"AM\",\n" +
                    "  \"Currency\": \"AMD\",\n" +
                    "  \"Checksum\": \"2139147b89b5ff13739d6a35a787ca47\",\n" +
                    "  \"Inputs\": [\"3030\"]\n" +
                    "}",
                // URL Path
                "/api/check",
                //Expected response
                "matched",
                // Matcher Name
                "MD5Hash-matcher",
                // Matcher Parameters
                Map.of(
                    "HashFields", "{{{join 'SecretToken'(jsonPath request.body '$.Inputs[0]')(jsonPath request.body '$.Lang')''}}}",
                    "Checksum", "{{jsonPath request.body '$.Checksum'}}",
                    "UrlPath", "/api/check"
                ),
                // Request Fields (JSON Paths for Matching)
                Map.of(
                    "Lang", ".*",
                    "Currency", ".*",
                    "Checksum", ".*",
                    "Inputs", ".*"
                ),
                // Expected Response Body
                Map.of("request-matcher-result", "matched"),
                // Headers
                new HttpHeaders(new HttpHeader("Content-Type", "application/json")) {
                }
            },
        };
    }

    @DataProvider
    public static Object[][] MD5HashDataProviderForPayment() {
        return new Object[][]{
            {
                // Request Body
                "{\n" +
                    "  \"Lang\": \"AM\",\n" +
                    "  \"Currency\": \"AMD\",\n" +
                    "  \"Checksum\": \"511dd4c808dcc3d7a2d27ff06b5952e0\",\n" +
                    "  \"Inputs\": [\"3030\"],\n" +
                    "  \"DtTime\": \"2025-03-20T10:00:00\",\n" +
                    "  \"Amount\": 100,\n" +
                    "  \"TransactID\": \"22120100000248\"\n" +
                    "}"
                ,
                // URL Path
                "/api/payment",
                //Expected response
                "matched",
                // Matcher Name
                "MD5Hash-matcher",
                // Matcher Parameters
                Map.of(
                    "HashFields", "{{{join 'SecretToken'(jsonPath request.body '$.Inputs[0]')(jsonPath request.body '$.Amount')(jsonPath request.body '$.TransactID')''}}}",
                    "Checksum", "{{jsonPath request.body '$.Checksum'}}",
                    "UrlPath", "/api/payment"
                ),
                // Request Fields (JSON Paths for Matching)
                Map.of(
                    "Lang", ".*",
                    "Currency", ".*",
                    "Checksum", ".*",
                    "Inputs", ".*",
                    "DtTime", ".*",
                    "Amount", ".*",
                    "TransactID", ".*"
                ),
                // Expected Response Body
                Map.of("request-matcher-result", "matched"),
                // Headers
                new HttpHeaders(new HttpHeader("Content-Type", "application/json")) {
                }
            },
        };
    }
}
