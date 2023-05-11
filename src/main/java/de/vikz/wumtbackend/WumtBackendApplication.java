package de.vikz.wumtbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(basePackageClasses = {WumtBackendApplication.class, Jsr310JpaConverters.class})
public class WumtBackendApplication {
	public static void main(String[] args) {

        SpringApplication.run(WumtBackendApplication.class, args);
    }

}
