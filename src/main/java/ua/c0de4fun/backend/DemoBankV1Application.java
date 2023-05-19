package ua.c0de4fun.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "ua.c0de4fun.backend")
public class DemoBankV1Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
        return applicationBuilder.sources(DemoBankV1Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoBankV1Application.class, args);

    }


}
