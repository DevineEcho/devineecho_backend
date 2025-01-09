package com.example.devineecho.service;

import com.example.devineecho.model.Item;
import com.example.devineecho.model.Player;
import com.example.devineecho.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll(); // 모든 아이템 가져오기
    }

    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 아이템 없음"));
    }

    public void purchaseItem(Player player, Long itemId, String currencyType) {
        Item item = getItemById(itemId);

        // 가격 검증
        int cost = currencyType.equals("GOLD") ? item.getRequiredGold() : item.getRequiredDiamond();
        if (cost > 0) {
            if (currencyType.equals("GOLD") && player.getGold() < cost) {
                throw new IllegalArgumentException("골드가 부족합니다");
            } else if (currencyType.equals("DIAMOND") && player.getDiamond() < cost) {
                throw new IllegalArgumentException("다이아가 부족합니다");
            }
        } else {
            throw new IllegalArgumentException("재화가 부족합니다");
        }

        // 재화 차감
        if (currencyType.equals("GOLD")) {
            player.subtractGold(cost);
        } else {
            player.subtractDiamond(cost);
        }

        // 아이템 인벤토리에 추가
        player.addItemToInventory(item);
    }
}
