package com.ediluana.minhasfinancas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MinhasfinancasApplication {

    public void testandoLiveReload() {
        System.out.println("Live Reload funcionando");
    }

    public static void main(String[] args) {
        SpringApplication.run(MinhasfinancasApplication.class, args);
    }

}
