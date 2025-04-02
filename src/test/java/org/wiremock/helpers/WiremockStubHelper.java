package org.wiremock.helpers;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.HttpHeaders;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.wiremock.AbstractTestBase.wm;

public class WiremockStubHelper {
    public static void createStubMatchesJsonPathPOST(String urlPath,
                            String matcherName,
                            Map<String, Object> matcherParams,
                            Map<String, String> requestFields,
                            Map<String, String> responseBody,
                            HttpHeaders headers) {
        wm.stubFor(
            WireMock.post(urlPathMatching(urlPath))
                .andMatching(matcherName, Parameters.from(matcherParams))
                .withRequestBody(
                    requestFields.entrySet().stream()
                        .map(entry -> WireMock.matchingJsonPath(entry.getKey()))
                        .reduce((a, b) -> a.and(b))
                        .orElseThrow()
                )
                .willReturn(
                    WireMock.ok()
                        .withHeaders(headers)
                        .withJsonBody(Json.node(Json.write(responseBody)))
                )
        );
    }
}
