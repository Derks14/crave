package order.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPIDocumentation() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Crave Order Service")
                                .description("this microservice handles order")
                                .version("1.0.0")
                                .contact(
                                        new Contact().email("admin@keteku.dev").url("crave-order.keteku.dev").name("crave")
                                ).license(new License().name("Apache 2.0").url("https://httpd.apache.org/docs/2.4/"))

                )
                .externalDocs(
                        new ExternalDocumentation()
                                .url("docs")
                                .description("mcdonalds franchising")
                                .url("https://www.mcdonalds.com/au/en-au/franchises.html")
                );

    }
}
