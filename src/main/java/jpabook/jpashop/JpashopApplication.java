package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module.Feature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpashopApplication.class, args);
    }

    @Bean
    Hibernate6Module hibernate6Module() {
        Hibernate6Module hibernate6Module = new Hibernate6Module();

        hibernate6Module.configure(Feature.FORCE_LAZY_LOADING, true);
        return new Hibernate6Module();
    }
}
