package org.petrowich.gallerychecker.processig.external.pornhub;

import lombok.extern.log4j.Log4j2;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Component
public class PornHubGalleriesFetcher {

    private final SSLConnectionSocketFactory sslConnectionSocketFactory;
    private final HttpClientConnectionManager httpClientConnectionManager;

    @Autowired
    public PornHubGalleriesFetcher(SSLConnectionSocketFactory sslConnectionSocketFactory,
                                   HttpClientConnectionManager httpClientConnectionManager) {
        this.sslConnectionSocketFactory = sslConnectionSocketFactory;
        this.httpClientConnectionManager = httpClientConnectionManager;
    }

    public ResponseEntity<String> fetchStoredGallery(String url) {
        log.info("Fetch {}", url);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setConnectionManager(httpClientConnectionManager)
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        log.debug("Connecting to {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        int statusCode = response.getStatusCodeValue();
        log.debug("Response status code {}", statusCode);

        String html = response.getBody();
        log.trace("Received html:\n{}", html);

        return response;
    }
}
