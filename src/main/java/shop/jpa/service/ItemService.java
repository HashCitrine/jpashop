package shop.jpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
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
    public void updateItem(Long id, String name, int price, int stockQuantity, String memo, String itemImage) {
        Item item = itemRepository.findOne(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        item.setMemo(memo);
        if(!(itemImage == null)) {
            item.setItemImage(itemImage);
        }
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }


    // 외부 환경에 따라 수정해야 할 필요 있음
    public String uploadFile(MultipartFile file) throws IOException {
        if(file.getSize() == 0) {
            return null;
        }

        // 확장자를 제외한 파일이름
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        System.out.println(fileName + ", " + index);

        // 파일 확장자
        String fileExtension = fileName.substring(index + 1);

        // 파일 이름 랜덤 설정 후 확장자 부여
        String savedName = UUID.randomUUID().toString() + "." + fileExtension;

        // 지정한 경로에 복사하여 파일 저장
        File target = new File(imgConfig.getUploadPath(), savedName);
        FileCopyUtils.copy(file.getBytes(), target);

        // '/img/파일명'을 db에 저장
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

    public List<Item> findBySort(String sort) {
        String[] sortList = {"home", "sale", "review", "book", "album", "movie"};
        int select = 1;

        for (String list : sortList) {
            if(list.equals(sort)){
                switch (select){
                    case 1 :
                        return findItems();
                    case 2:
                        return orderByReview();
                    case 3:
                        return orderBySaleCount();
                    default:
                        return itemRepository.findByCategory(select);
                }
            }
            select++;
        }

        return null;
        }


    }

