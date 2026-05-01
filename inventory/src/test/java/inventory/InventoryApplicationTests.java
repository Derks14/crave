package inventory;

import inventory.models.Inventory;
import inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.TestcontainersConfiguration;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class InventoryApplicationTests {

	@Container
	@ServiceConnection
	static MySQLContainer sqlContainer = new MySQLContainer("mysql:8.3.0");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private InventoryRepository repository;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testItemAvailability() throws Exception {

		Inventory newInventory = Inventory.builder().skuCode("dqp").quantity(10).build();
		repository.save(newInventory);

		MvcResult result =  mockMvc
				.perform(
						get("/api/inventory")
								.param("skuCode", "dqp")
				)
				.andExpect(status().isOk())
				.andReturn();

		boolean inventoryCheckResult = objectMapper.readValue(result.getResponse().getContentAsString(), Boolean.class);

		Assertions.assertTrue(inventoryCheckResult);
	}

	@Test
	void testOutOfStockFor() throws Exception {
		Inventory newInventory = Inventory.builder().skuCode("dqp").quantity(0).build();
		repository.save(newInventory);

		MvcResult mvcResult = mockMvc.perform(
				get("/api/inventory").param("skuCode").param("skuCode", "dqp")
				)
				.andExpect(status().isOk())
				.andReturn();
		boolean inventoryCheckResult = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Boolean.class);

		Assertions.assertFalse(inventoryCheckResult);


	}
}
