package inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class Docs {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Crave Inventory Service")
                                .description("this microservice is checks our inventory")
                                .version("1.0.0")
                                .contact(
                                        new Contact().email("admin@keteku.dev").url("crave-inventory.keteku.dev").name("crave")
                                ).license(new License().name("Apache 2.0").url("https://httpd.apache.org/docs/2.4/"))
                ).externalDocs(
                        new ExternalDocumentation()
                                .url("docs")
                                .description("mcdonalds franchising")
                                .url("https://www.mcdonalds.com/au/en-au/franchises.html")

                );

    }

}
