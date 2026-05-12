package order;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import order.models.Order;
import order.repository.OrderRepository;
import order.stubs.InventoryClientStubs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;


import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
class OrderApplicationTests {

	@RegisterExtension
	static WireMockExtension wireMock = WireMockExtension.newInstance()
			.options(wireMockConfig().dynamicPort())
			.configureStaticDsl(true)
			.build();

	@DynamicPropertySource
	static void wireMockProperties(DynamicPropertyRegistry registry) {
//		registry.add("inventry.url", wireMock::baseUrl);
		registry.add("inventory.url", () -> "http://localhost:" + wireMock.getPort());
	}

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
					"skuCode": "quarter_pounder",
					"price": 12.00,
					"quantity": 2
				}
				""";

		InventoryClientStubs.stubInventoryCall("quarter_pounder");

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
