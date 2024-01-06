package com.multiplos.cuentas.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class AzureStorageParam {

	@Value("${azure.storage.default-endpoints-protocol}")
    private String defaultEndpointsProtocol;

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    @Value("${azure.storage.endpoint-suffix}")
    private String endpointSuffix;

    /*@Value("${azure.storage.container-reference}")
    private String containerReference;*/

    public String getStorageConnectionString() {
        String storageConnectionString =
            String.format("DefaultEndpointsProtocol=%s;AccountName=%s;AccountKey=%s;EndpointSuffix=%s",
                defaultEndpointsProtocol, accountName, accountKey, endpointSuffix);
        return storageConnectionString;
    }
}
