package org.petrowich.gallerychecker.processig.stored.fetch;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Log4j2
@Component
public class StoredGalleryFetcher {

    private final SSLConnectionSocketFactory sslConnectionSocketFactory;
    private final HttpClientConnectionManager httpClientConnectionManager;

    @Autowired
    public StoredGalleryFetcher(SSLConnectionSocketFactory sslConnectionSocketFactory,
                                HttpClientConnectionManager httpClientConnectionManager) {
        this.sslConnectionSocketFactory = sslConnectionSocketFactory;
        this.httpClientConnectionManager = httpClientConnectionManager;
    }

    public ResponseEntity<String> fetchStoredGallery(StoredGallery storedGallery) {
        log.info("Checking \n {}", storedGallery);

        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy() {
            @Override
            public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
                    throws ProtocolException {
                if (super.isRedirected(request, response, context)) {
                    storedGallery.setVideoUrl(response.getFirstHeader("Location").getValue());
                    return true;
                }
                return false;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setConnectionManager(httpClientConnectionManager)
                .setRedirectStrategy(redirectStrategy)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        log.debug("Connecting to {}", storedGallery.getEmbedUrl());
        ResponseEntity<String> response = restTemplate.getForEntity(storedGallery.getEmbedUrl(), String.class);

        int statusCode = response.getStatusCodeValue();
        log.debug("Response status code {}", statusCode);

        String html = response.getBody();
        log.trace("Received html:\n{}", html);

        return response;
    }
}
