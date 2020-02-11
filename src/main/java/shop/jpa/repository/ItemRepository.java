package shop.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.jpa.domain.OrderItem;
import shop.jpa.domain.item.Item;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);
        }
    }

    public void delete(Item item) {
        em.remove(item);

    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i order by i.date desc", Item.class)
                .getResultList();
    }

    /*
    public int findItemsCount() {
        return em.createQuery("select count(i) from Item i", Item.class)
                .getFirstResult();
    }

     */

    // 리뷰순 정렬
    public List<Item> findAllOrderByReview() {
        return em.createQuery("select i from Item i left join Review r on i.id =r.item.id group by i.id Order by count(r) desc", Item.class)
                .getResultList();
    }


    // 1차 방법 : orderItem join
    public List<Item> findAllOrderBySale() {
        return em.createQuery("select i from Item i left join OrderItem oi on i.id = oi.item.id group by i.id order by sum(oi.count) DESC", Item.class)
                .getResultList();
    }

    public List<Item> findByCategory(int select) {
        switch (select) {
            case 4:
                return em.createQuery("select b from Book b", Item.class)
                        .getResultList();
            case 5:
                return em.createQuery("select a from Album a", Item.class)
                        .getResultList();
            case 6:
                return em.createQuery("select m from Movie m", Item.class)
                        .getResultList();
        }
        return null;
    }
}