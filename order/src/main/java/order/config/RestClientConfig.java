package order.config;

import lombok.extern.slf4j.Slf4j;
import order.clients.InventoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.URI;
import java.time.Duration;

@Slf4j
@Configuration
public class RestClientConfig {

    @Value("${inventory.url}")
    private String baseUrl;

    @Bean
    public InventoryService inventoryService() {

        // we first build our rest client and create an adapter using that. We then pass that into an http service proxy factory
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(getClientRequestFactory())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    log.info("something wrong with then data we sending");
                }))
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    log.info("something wrong with their server");
                }))
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        // we need an exchange adapter
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        InventoryService service = factory.createClient(InventoryService.class);

        return service;
    }

    private ClientHttpRequestFactory getClientRequestFactory() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(3));
        return factory;
    }
}
