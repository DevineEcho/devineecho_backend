package com.example.devineecho.service;

import com.example.devineecho.exception.InsufficientCurrencyException;
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
        return itemRepository.findAll();
    }

    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 아이템 없음"));
    }

    public void purchaseItem(Player player, Long itemId, String currencyType) {
        Item item = getItemById(itemId);

        if ("GOLD".equals(currencyType) && item.getRequiredGold() > 0) {
            if (player.getGold() < item.getRequiredGold()) {
                int missingGold = item.getRequiredGold() - player.getGold();
                throw new InsufficientCurrencyException("골드", missingGold);
            }
            player.subtractGold(item.getRequiredGold());
        } else if ("DIAMOND".equals(currencyType) && item.getRequiredDiamond() > 0) {
            if (player.getDiamond() < item.getRequiredDiamond()) {
                int missingDiamond = item.getRequiredDiamond() - player.getDiamond();
                throw new InsufficientCurrencyException("다이아몬드", missingDiamond);
            }
            player.subtractDiamond(item.getRequiredDiamond());
        } else {
            throw new IllegalArgumentException("구매할 수 없는 아이템입니다.");
        }


        item.assignToPlayer(player);
        player.addItemToInventory(item);


        itemRepository.save(item);
    }

}
