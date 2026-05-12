package api_gateway.routes;

import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration(proxyBeanMethods = false)
public class Routes {

    // this uses functional endpoint programing model ( check it out on spring cloud )
//    docs.spring.io/spring-framework/reference/web/webmvc-functional.html

    // Check out the RequestPRedicate class for headers matching, GET,



    @Bean
    public RouterFunction<ServerResponse> foodServiceRoute() {
        return route("food_service")
                .route( RequestPredicates.path("/api/food"), http() )
                .before(uri("http://localhost:8080") )
                .filter(circuitBreaker("foodServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return route("order_service")
                .route(RequestPredicates.path("/api/order"), http())
                .before(uri("http://localhost:8081"))
                .filter(circuitBreaker("orderServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryService() {
        return route("inventory_service")
                .route(RequestPredicates.path("/api/inventory"), http())
                .before(uri("http://localhost:8082"))
                .filter(circuitBreaker("inventoryServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }


//    when it comes to the gateway, keep the swagger ui only on the gateway,
//    expose the services api docs ( json format )
//    and route the gateway to the api docs of the services

    @Bean
    public RouterFunction<ServerResponse> foodServiceDocs() {
        return route("food-service-docs")
                .route( RequestPredicates.path("/aggregate/food/ui-docs.html"), http() )
                .before(uri("http://localhost:8080"))
                .before(setPath("/docs"))
                .filter(circuitBreaker("foodServiceDocsCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceDocs() {
        return route("order-service-docs")
                .route(RequestPredicates.path("/aggregate/order/ui-docs.html"), http())
                .before(uri("http://localhost:8081"))
                .before(setPath("/docs"))
                .filter(circuitBreaker("orderServiceDocsCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceDocs() {
        return route("inventory-service-docs")
                .route(RequestPredicates.path("/aggregate/inventory/ui-docs.html"), http())
                .before(uri("http://localhost:8082"))
                .filter(circuitBreaker("inventoryServiceDocsCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .before(setPath("/docs"))

                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("service unavailable, please try again later"))
                .build();
    }


}
