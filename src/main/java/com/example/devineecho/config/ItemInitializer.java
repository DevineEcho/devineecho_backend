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
                    new Item(
                            "DragonicSword",
                            "드래곤소드",
                            50000,
                            2000,
                            ItemType.EQUIPMENT,
                            null,
                            null,
                            "장착시 공격력을 10% 증가시킵니다."
                    ),
                    new Item(
                            "DragonicHammer",
                            "드래곤해머",
                            50000,
                            2000,
                            ItemType.EQUIPMENT,
                            null,
                            null,
                            "장착시 갓즈해머의 데미지가 30% 증가합니다."
                    ),
                    new Item(
                            "DragonicStaff",
                            "드래곤스태프",
                            50000,
                            2000,
                            ItemType.EQUIPMENT,
                            null,
                            null,
                            "장착시 세인트오라의 쿨타임을 0.5초 감소시킵니다."
                    ),
                    new Item(
                            "MilkywayHolycircle",
                            "은하수볼",
                            100000,
                            4000,
                            ItemType.SKIN,
                            null,
                            null,
                            "홀리서클에 은하수를 담았습니다."
                    ),
                    new Item(
                            "WoodenHolycircle",
                            "오크나무볼",
                            100000,
                            4000,
                            ItemType.SKIN,
                            null,
                            null,
                            "단단한 오크나무로 제작된 홀리서클입니다."
                    ),
                    new Item(
                            "FlameHolycircle",
                            "화염구",
                            100000,
                            4000,
                            ItemType.SKIN,
                            null,
                            null,
                            "드래곤의 숨결이 깃든 홀리서클입니다."
                    ),
                    new Item(
                            "WingedBoots",
                            "윙드부츠",
                            0,
                            3000,
                            ItemType.EQUIPMENT,
                            null,
                            null,
                            "장착시 이동속도를 20% 증가시킵니다."
                    ),
                    new Item(
                            "RaiseSkeleton",
                            "해골병사 소환",
                            100000,
                            0,
                            ItemType.SKILL,
                            null,
                            null,
                            "투사체를 발사하는 해골병사를 소환합니다."
                    ),
                    new Item(
                            "AngelAssasin",
                            "암살천사",
                            300000,
                            0,
                            ItemType.SKIN,
                            null,
                            null,
                            "천사진영의 뛰어난 실력을 가진 악마암살자입니다."
                    )
            );

            for (Item item : items) {
                if (!itemRepository.existsByName(item.getName())) {
                    itemRepository.save(item);
                    System.out.println("Added item: " + item.getDisplayName());
                } else {
                    System.out.println("Item already exists: " + item.getDisplayName());
                }
            }
        };
    }
}
