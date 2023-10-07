package com.relengxing.rebase.es.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

@Data
@Slf4j
@Configuration
public class ElasticsearchConfig {
    private static final String TAG = "elasticsearch配置类ElasticsearchConfig:";

    private static final int ADDRESS_LENGTH = 2;

    /**
     * 多个地址之间用逗号隔开
     */
    @Value("${elasticsearch.hosts:192.168.238.128:9200}")
    private String hosts;
    @Value("${elasticsearch.username:null}")
    private String username;
    @Value("${elasticsearch.password:null}")
    private String password;


    @Bean(name = "esClient", destroyMethod = "close")
    public RestHighLevelClient esClient() {
        HttpHost[] httpHosts = Arrays.asList(hosts.split(",")).stream().map(this::makeHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);

        RestClientBuilder builder = RestClient.builder(httpHosts);
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
        }
        log.info("{}elasticsearch is start httpHosts:{} ", TAG, httpHosts);
        return new RestHighLevelClient(builder);
    }


    private HttpHost makeHttpHost(String s) {
        assert StringUtils.hasText(s);
        String[] address = s.split(":");
        if (address.length == ADDRESS_LENGTH) {
            String ip = address[0];
            int port = Integer.parseInt(address[1]);
            return new HttpHost(ip, port, HttpHost.DEFAULT_SCHEME_NAME);
        } else {
            return null;
        }
    }

}
