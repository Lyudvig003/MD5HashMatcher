package org.wiremock.extensions.matchers;

import com.github.tomakehurst.wiremock.core.ConfigurationException;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.responsetemplating.RequestTemplateModel;
import com.github.tomakehurst.wiremock.extension.responsetemplating.TemplateEngine;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension;
import lombok.SneakyThrows;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;
import static org.wiremock.helpers.MD5HashGenerator.calculateMD5Hash;

/**
 * Custom WireMock request matcher that validates a request based on an MD5 hash.
 * The computed hash is generated using specific JSON fields then
 * compared against the provided checksum in the request body.
 */
public class MD5HashMatcher extends RequestMatcherExtension {
    private static final String paramsChecksum = "Checksum";
    private static final String paramsUrlPath = "UrlPath";
    private static final String paramsHashFields = "HashFields";

    private final TemplateEngine templateEngine;

    public MD5HashMatcher(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String getName() {
        return "MD5Hash-matcher";
    }

    @SneakyThrows
    @Override
    public MatchResult match(Request request, Parameters parameters) {
            try {
                String hashFields = parameters.getString(paramsHashFields);
                Map<String, Object> model = new HashMap<>(Map.of("request", RequestTemplateModel.from(request)));
                String concatenatedValues = renderTemplate(model, hashFields);
                String computedChecksum = calculateMD5Hash(concatenatedValues).toLowerCase();
                String providedChecksum = renderTemplate(model, parameters.getString(paramsChecksum));

                if (providedChecksum.equals(computedChecksum)) {
                    return MatchResult.exactMatch();
                } else {
                    String errorMessage = "Checksum mismatch: expected '" + computedChecksum + "' but got '" + providedChecksum + "'";
                    notifier().error(errorMessage);
                    throw new ConfigurationException(errorMessage);
                }
            } catch (Exception e) {
                String errorMessage = "Error processing request: " + e.getClass().getSimpleName() + " - " + e.getMessage();
                notifier().error(errorMessage);
                throw new ConfigurationException(errorMessage);
            }
        }

    String renderTemplate(Object context, String value) {
        return templateEngine.getUncachedTemplate(value).apply(context);
    }

}

