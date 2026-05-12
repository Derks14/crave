package food.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Doc {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Crave Food Service")
                                .description("this microservice is used add meals into our system")
                                .version("1.0.0")
                                .contact(
                                        new Contact().email("admin@keteku.dev").url("crave-food.keteku.dev").name("crave")
                                ).license(new License().name("Apache 2.0").url("https://httpd.apache.org/docs/2.4/"))
                ).externalDocs(
                        new ExternalDocumentation()
                                .url("docs")
                                .description("mcdonalds franchising")
                                .url("https://www.mcdonalds.com/au/en-au/franchises.html")

                );
    }
}
