package notification;

import org.springframework.boot.SpringApplication;

public class TestNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
