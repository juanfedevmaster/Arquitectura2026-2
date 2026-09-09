package com.juanfedevmaster.inventorymicroservice.config;

import com.juanfedevmaster.inventorymicroservice.domain.entity.ProductStock;
import com.juanfedevmaster.inventorymicroservice.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductStockRepository productStockRepository;

    @Override
    public void run(String... args) {
        if (productStockRepository.count() > 0) return;

        List<ProductStock> initial = List.of(
            ProductStock.builder().productId("1").productName("Arroz Diana 500g").currentStock(100).build(),
            ProductStock.builder().productId("2").productName("Aceite Girasol 1L").currentStock(80).build(),
            ProductStock.builder().productId("3").productName("Leche Alquería 1L").currentStock(150).build(),
            ProductStock.builder().productId("4").productName("Azúcar Manuelita 1kg").currentStock(60).build(),
            ProductStock.builder().productId("5").productName("Harina de trigo 1kg").currentStock(90).build(),
            ProductStock.builder().productId("6").productName("Chocolate Corona 500g").currentStock(40).build(),
            ProductStock.builder().productId("7").productName("Atún Van Camps 170g").currentStock(200).build(),
            ProductStock.builder().productId("8").productName("Sal Refisal 500g").currentStock(120).build(),
            ProductStock.builder().productId("9").productName("Frijol bolo rojo 500g").currentStock(70).build(),
            ProductStock.builder().productId("10").productName("Avena Quaker 500g").currentStock(55).build()
        );

        productStockRepository.saveAll(initial);
    }
}
