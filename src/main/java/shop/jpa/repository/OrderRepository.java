package shop.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import shop.jpa.domain.*;
import shop.jpa.domain.Order;
import shop.jpa.domain.item.Item;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }


    public void delete(Order order) {
        em.remove(order);

    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

/*    public List<Order> findAll(OrderSearch orderSearch) {
        return em.createQuery("select o from Order o join o.member m" +
                " where o.status = :status" +
                " and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000)    // 최대 1000권
                .getResultList();
    }
*/

    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }


    public List<Order> findAllByCirteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        // 회원이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);

        return query.getResultList();
    }

    public List<Order> getBoardList(int first, int max) {
        int start = (first - 1) * max;

        if(first < 0) {
            first = 0;
        }

        System.out.println("first =" + first + ", max =" + max);

        return em.createQuery("select o from Order o ORDER BY o.id DESC", Order.class)
                .setFirstResult(start)
                .setMaxResults(max)
                .getResultList();
    }

    public Long getRowCount() {
        return em.createQuery("select count(o) from Order o", Long.class)
                .getSingleResult();
    }

    public List<Order> findAll() {
        return em.createQuery("select o from Order o", Order.class)
                .getResultList();
    }

    public List<Order> findByMemberId(Long memberId) {
        return em.createQuery("select o from Order o where o.member.id =: memberId", Order.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }


    public List<Order> findByStatus(OrderSearch orderSearch){
        if(orderSearch.getOrderStatus() != OrderStatus.CANCEL) {
            return em.createQuery("select o from Order o where o.delivery.Status =:deliveryStatus and o.status =:orderStatus", Order.class)
                    .setParameter("deliveryStatus", orderSearch.getDeliveryStatus())
                    .setParameter("orderStatus", orderSearch.getOrderStatus())
                    .getResultList();
        }
            return em.createQuery("select o from Order o where o.status=:orderStatus", Order.class)
                    .setParameter("orderStatus", orderSearch.getOrderStatus())
                    .getResultList();
    }


    public List<Order> findMyOrderByStatus(OrderSearch orderSearch, Long memberId) {
        if (orderSearch.getOrderStatus() != OrderStatus.CANCEL) {
            return em.createQuery("select o from Order o where o.delivery.Status =:deliveryStatus and o.status =:orderStatus and o.member.id=:memberId", Order.class)
                    .setParameter("deliveryStatus", orderSearch.getDeliveryStatus())
                    .setParameter("orderStatus", orderSearch.getOrderStatus())
                    .setParameter("memberId", memberId)
                    .getResultList();
        }

        return em.createQuery("select o from Order o where o.status=:orderStatus and o.member.id =:memberId", Order.class)
                .setParameter("orderStatus", orderSearch.getOrderStatus())
                .setParameter("memberId", memberId)
                .getResultList();

    }
}
