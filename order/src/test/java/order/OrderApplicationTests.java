package order;

import order.dto.OrderRequest;
import order.models.Order;
import order.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.mysql.MySQLContainer;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
class OrderApplicationTests {

	@Container
	@ServiceConnection
	static MySQLContainer sqlContainer = new MySQLContainer("mysql:8.3.0");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderRepository orderRepository;
    @Autowired
    private ObjectMapper objectMapper;

	@Test
	void testPlaceNewOrder() throws Exception {
		String orderRequest = """
				{
					"skuCode": "double-quarter-pound",
					"price": 12.00,
					"quantity": 2
				}
				""";

		MvcResult result = mockMvc
				.perform(
						post("/api/order")
								.contentType(MediaType.APPLICATION_JSON)
								.content(orderRequest))
				.andExpect(status().isCreated())
				.andReturn();

		Order placedOrder = objectMapper.readValue(result.getResponse().getContentAsString(), Order.class);
		Long orderId = placedOrder.getId();

		// check that placed order exists in db.
		Assertions.assertTrue(orderRepository.findById(orderId).isPresent());

		// check cache if it exist
//		Cache cache = cacheManager.getCache(ProductService.PRODUCT_CACHE);
//		assertNotNull(cache);
//		assertNotNull(cache.get(productId, ProductDto.class));
	}
}
