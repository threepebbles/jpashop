package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpashopApplication.class, args);
    }

//    @Bean
//    Hibernate6Module hibernate6Module() {
//        Hibernate6Module hibernate6Module = new Hibernate6Module();
//
//        hibernate6Module.configure(Feature.FORCE_LAZY_LOADING, true);
//        return new Hibernate6Module();
//    }
}
