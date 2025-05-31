package org.caselli.comparativecognitiveworkflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ComparativeCognitiveWorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComparativeCognitiveWorkflowApplication.class, args);
    }

    /**
     * Defines a RestTemplate bean that can be injected into other components.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
