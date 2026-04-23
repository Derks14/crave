package food;

import org.springframework.boot.SpringApplication;

public class TestFoodApplication {

	public static void main(String[] args) {
		SpringApplication.from(FoodApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
