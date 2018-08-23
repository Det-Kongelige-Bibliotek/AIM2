package dk.kb.aim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * The AimApplication servlet initializer.
 */
@SpringBootApplication
public class AimApplication  extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AimApplication.class);
	}
	
	/**
	 * Main method.
	 * @param args Arguments for the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(AimApplication.class, args);
	}
}
