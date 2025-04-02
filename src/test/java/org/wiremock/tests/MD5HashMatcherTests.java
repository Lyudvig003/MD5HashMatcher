package org.wiremock.tests;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.wiremock.AbstractTestBase;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.wiremock.helpers.WiremockStubHelper.createStubMatchesJsonPathPOST;

public class MD5HashMatcherTests extends AbstractTestBase {

    @DisplayName("md5 matcher check flow")
    @ParameterizedTest
    @MethodSource("org.wiremock.dataprovider.MD5HashDataProvider#MD5HashDataProviderForCheck")
    public void test_MD5matcherCheckFlow_matched(String requestBody, String urlPath, String expectedResponse, String customMatcherName, Map<String, Object> matcherParams, Map<String, String> requestFields, Map<String, String> responseBody, HttpHeaders headers) {
        createStubMatchesJsonPathPOST(
            // URL Path
            "/api/check",
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
            });

        createStubMatchesJsonPathPOST(
            // URL Path
            "/api/payment",
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
            });
        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(requestBody)
            .post(wm.getRuntimeInfo().getHttpBaseUrl() + urlPath)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body(responseBody.keySet().iterator().next(), Matchers.equalTo(expectedResponse));
    }

    @DisplayName("md5 matcher payment flow")
    @ParameterizedTest
    @MethodSource("org.wiremock.dataprovider.MD5HashDataProvider#MD5HashDataProviderForPayment")
    public void test_MD5matcherPaymentFlow_matched(String requestBody, String urlPath, String expectedResponse, String customMatcherName, Map<String, Object> matcherParams, Map<String, String> requestFields, Map<String, String> responseBody, HttpHeaders headers) {
        createStubMatchesJsonPathPOST(urlPath, customMatcherName, matcherParams, requestFields, responseBody, headers);
        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(requestBody)
            .post(wm.getRuntimeInfo().getHttpBaseUrl() + urlPath)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body(responseBody.keySet().iterator().next(), Matchers.equalTo(expectedResponse));
    }
}
