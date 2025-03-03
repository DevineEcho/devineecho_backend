package com.example.devineecho.repository;

import com.example.devineecho.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsByName(String name);

}
