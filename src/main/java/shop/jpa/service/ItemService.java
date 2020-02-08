package shop.jpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import shop.jpa.domain.OrderItem;
import shop.jpa.domain.item.Item;
import shop.jpa.repository.ItemRepository;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final shop.jpa.ImgConfig imgConfig;

    // merge 병합
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    // 변경 감지
    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity, String memo) {
        Item item = itemRepository.findOne(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        item.setMemo(memo);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if(file.getSize() == 0) {
            return null;
        }

        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        System.out.println(fileName + ", " + index);
        String fileExtension = fileName.substring(index + 1);

        String savedName = UUID.randomUUID().toString()+ "." + fileExtension;

        File target = new File(imgConfig.getUploadPath(), savedName);
        FileCopyUtils.copy(file.getBytes(), target);

        String path = target.getPath();
        int pathIndex = path.lastIndexOf("/img");

        return path.substring(pathIndex);
    }

    public void deleteItem(Long itemId) {
        itemRepository.delete(itemRepository.findOne(itemId));
    }

    public List<Item> orderByReview() {
        return itemRepository.findAllOrderByReview();
    }

    public List<Item> orderBySaleCount() {
        return itemRepository.findAllOrderBySale();
    }

}
