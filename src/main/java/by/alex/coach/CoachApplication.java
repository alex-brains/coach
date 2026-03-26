package by.alex.coach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CoachApplication {
	public static void main(String[] args) {
		SpringApplication.run(CoachApplication.class, args);
	}
}
