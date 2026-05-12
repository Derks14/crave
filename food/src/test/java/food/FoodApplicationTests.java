package food;

import food.model.Food;
import food.repository.FoodRepository;
import io.restassured.RestAssured;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.shaded.org.hamcrest.Matchers;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.equalTo;

@Import(TestcontainersConfiguration.class)
// we do this so that test containers spin up a whole new application on a different port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FoodApplicationTests {

//	initialise a mongodb container
	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@LocalServerPort
	private Integer port;

	@Autowired
	private FoodRepository repository;



    @BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = this.port;
	}


	static {
		mongoDBContainer.start();
	}



	@Test
	void shouldCreateFood() {
		String body = """
				{
					"name": "Cheese burger",
					"description": "the best from five guys",
					"price": 100.00
				}
				""";

		RestAssured
			.given()
				.contentType("application/json").body(body)
			.when()
				.post("/api/food")
			.then()
				.statusCode(201)
				.body("name", equalTo("Cheese burger"))
				.body("description", equalTo("the best from five guys"));
	}

	@Test
	void getAllFoods() {

		Food food = Food.builder()
				.name("quarter_pounder")
				.price(BigDecimal.valueOf(12.00))
				.description("this is a normal burger from mcdonalds")
				.build();

		Food food2 = Food.builder()
				.name("big_mac")
				.price(BigDecimal.valueOf(14.00))
				.description("this is a biggg burger from mcdonalds")
				.build();



		repository.save(food);
		repository.save(food2);

		RestAssured
			.given()
				.when().get("/api/food")
			.then()
				.statusCode(HttpStatus.OK.value());
	}

}
