package org.superbiz.moviefun.blobstore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

public class ServiceCredentials {

    private final String vcapServices;

    public ServiceCredentials(String vcapServices) {
        this.vcapServices = vcapServices;
    }

    public String getCredential(String serviceName, String serviceType, String credentialKey) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root;

        try {
            root = objectMapper.readTree(vcapServices);
        } catch (IOException e) {
            throw new IllegalStateException("No VCAP_SERVICES found", e);
        }

        JsonNode services = root.path(serviceType);

        for (JsonNode service : services) {
            if (Objects.equals(service.get("name").asText(), serviceName)) {
                return service.get("credentials").get(credentialKey).asText();
            }
        }

        throw new IllegalStateException("No "+ serviceName + " found in VCAP_SERVICES");
    }

    /*public static AWSCredentials getAWSCredentials() {
        String awsKey = getAWSAccessKey();
        String awsSecret = getAWSAccessSecret();

        if (awsKey != null && !awsKey.isEmpty() && awsSecret != null && !awsSecret.isEmpty()) {
            return new BasicAWSCredentials(awsKey, awsSecret);
        }
        return null;
    }*/
}