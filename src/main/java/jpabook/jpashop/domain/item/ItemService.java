package jpabook.jpashop.domain.item;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        // merge를 하면 엔티티가 아예 갈아끼워버려짐.
        // 갈아끼우는건 원하지 않는 값 변경도 일어날 위험이 존재. null 값이 포함되게 될 가능성도 있음.
        // 그러므로 가급적으로 merge를 자제하고, 변경 감지로 변경이 필요한 값만 업데이트하도록 구현하는 것이 안전함
        Item item = itemRepository.findOne(itemId);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
