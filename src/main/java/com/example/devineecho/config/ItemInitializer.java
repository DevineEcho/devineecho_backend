package com.example.devineecho.config;

import com.example.devineecho.model.Item;
import com.example.devineecho.model.Item.ItemType;
import com.example.devineecho.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ItemInitializer {

    @Bean
    public CommandLineRunner initializeItems(ItemRepository itemRepository) {
        return args -> {
            List<Item> items = List.of(
                    new Item("DragonicSword", 50000, 2000, ItemType.EQUIPMENT, null, null),
                    new Item("DragonicHammer", 50000, 2000, ItemType.EQUIPMENT, null, null),
                    new Item("DragonicStaff", 50000, 2000, ItemType.EQUIPMENT, null, null),
                    new Item("MilkywayHolycircle", 100000, 4000, ItemType.SKIN, null, null),
                    new Item("WoodenHolycircle", 100000, 4000, ItemType.SKIN, null, null),
                    new Item("FlameHolycircle", 100000, 4000, ItemType.SKIN, null, null),
                    new Item("WingedBoots", 0, 3000, ItemType.EQUIPMENT, null, null),
                    new Item("RaiseSkeleton", 100000, 0, ItemType.SKILL, null, null),
                    new Item("AngelAssasin", 300000, 0, ItemType.SKIN, null, null)
            );

            for (Item item : items) {
                if (!itemRepository.existsByName(item.getName())) {
                    itemRepository.save(item);
                    System.out.println("Added item: " + item.getName());
                } else {
                    System.out.println("Item already exists: " + item.getName());
                }
            }
        };
    }
}
