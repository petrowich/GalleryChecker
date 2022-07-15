package org.petrowich.gallerychecker.config;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class HttpClientConfig {

    @Bean
    public SSLConnectionSocketFactory sslConnectionSocketFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return new SSLConnectionSocketFactory(sslContext(), NoopHostnameVerifier.INSTANCE);
    }

    @Bean
    public HttpClientConnectionManager httpClientConnectionManager() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory> create()
                        .register("https", sslConnectionSocketFactory())
                        .register("http", new PlainConnectionSocketFactory())
                        .build();

        return new BasicHttpClientConnectionManager(socketFactoryRegistry);
    }

    private SSLContext sslContext() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return SSLContexts.custom().loadTrustMaterial(null, trustStrategy()).build();
    }
    private TrustStrategy trustStrategy() {
        return (cert, authType) -> true;
    }
}
