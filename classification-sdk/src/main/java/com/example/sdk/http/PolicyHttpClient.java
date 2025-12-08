package com.example.sdk.http;

import com.example.sdk.config.SdkConfig;
import com.example.sdk.model.Ruleset;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Handles communication with the remote policy service over HTTP. Supports
 * conditional GET requests using the If-None-Match header to avoid
 * downloading unchanged rulesets.
 */
public class PolicyHttpClient {

    private static final Logger log = LoggerFactory.getLogger(PolicyHttpClient.class);

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Fetch the latest ruleset from the policy service. If the server returns
     * 304 Not Modified this method will return null to signal no update. On
     * network errors or non-success codes it also returns null.
     */
    public VersionedResponse fetchLatest() {
        Request.Builder requestBuilder =
                new Request.Builder().url(SdkConfig.getPolicyUrl());

        // Set conditional request header if we have a previous ETag
        String eTag = SdkConfig.getLastETag();
        if (eTag != null) {
            requestBuilder.header("If-None-Match", eTag);
        }

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {

            // Handle no content change
            if (response.code() == 304) {
                log.info("Ruleset not modified, ETag={}", eTag);
                return null;
            }

            // Any non-successful response is treated as a failure; do not update
            if (!response.isSuccessful()) {
                log.warn("Fetch rules failed: {}", response.code());
                return null;
            }

            String respETag = response.header("ETag");
            ResponseBody bodyObj = response.body();
            String body = (bodyObj != null) ? bodyObj.string() : "";
            Ruleset ruleset = MAPPER.readValue(body, Ruleset.class);

            log.info("Fetched new ruleset: version={}, ETag={}",
                    ruleset.getVersion(), respETag);

            return new VersionedResponse(ruleset, respETag);

        } catch (IOException e) {
            log.error("Network error while fetching rules: {}", e.getMessage());
            return null; // swallow exceptions to allow fallback to cached rules
        }
    }
}