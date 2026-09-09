package com.juanfedevmaster.salesmicroservice.config;

import com.juanfedevmaster.salesmicroservice.domain.entity.Product;
import com.juanfedevmaster.salesmicroservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) return;

        List<Product> products = List.of(
            Product.builder().name("Arroz Diana 500g").description("Arroz blanco de grano largo")
                .price(new BigDecimal("3200.00")).category("COMESTIBLE").stock(100).build(),
            Product.builder().name("Aceite Girasol 1L").description("Aceite vegetal de girasol puro")
                .price(new BigDecimal("8500.00")).category("COMESTIBLE").stock(80).build(),
            Product.builder().name("Leche Alquería 1L").description("Leche entera pasteurizada")
                .price(new BigDecimal("4200.00")).category("COMESTIBLE").stock(150).build(),
            Product.builder().name("Azúcar Manuelita 1kg").description("Azúcar blanco refinado")
                .price(new BigDecimal("4800.00")).category("COMESTIBLE").stock(60).build(),
            Product.builder().name("Harina de trigo 1kg").description("Harina todo uso")
                .price(new BigDecimal("3600.00")).category("COMESTIBLE").stock(90).build(),
            Product.builder().name("Chocolate Corona 500g").description("Chocolate de mesa oscuro")
                .price(new BigDecimal("7100.00")).category("COMESTIBLE").stock(40).build(),
            Product.builder().name("Atún Van Camps 170g").description("Atún en agua, lata")
                .price(new BigDecimal("5300.00")).category("COMESTIBLE").stock(200).build(),
            Product.builder().name("Sal Refisal 500g").description("Sal refinada yodada")
                .price(new BigDecimal("1800.00")).category("COMESTIBLE").stock(120).build(),
            Product.builder().name("Frijol bolo rojo 500g").description("Frijol rojo seleccionado")
                .price(new BigDecimal("4500.00")).category("COMESTIBLE").stock(70).build(),
            Product.builder().name("Avena Quaker 500g").description("Avena en hojuelas instantánea")
                .price(new BigDecimal("6200.00")).category("COMESTIBLE").stock(55).build()
        );

        productRepository.saveAll(products);
    }
}
