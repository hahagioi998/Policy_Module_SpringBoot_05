package SpringBoot.Policy_Module_Pro_Max;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PolicyModuleProMaxApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolicyModuleProMaxApplication.class, args);
	}

}
