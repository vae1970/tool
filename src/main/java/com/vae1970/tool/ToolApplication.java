package com.vae1970.tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author dongzhou.gu
 */
@SpringBootApplication
@EnableScheduling
public class ToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolApplication.class, args);
    }

}
