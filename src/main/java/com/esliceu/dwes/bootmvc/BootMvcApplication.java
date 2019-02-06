package com.esliceu.dwes.bootmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.esliceu.dwes")
public class BootMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootMvcApplication.class, args);
    }

}

