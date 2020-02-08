package shop.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.jpa.domain.Comment;
import shop.jpa.domain.Member;
import shop.jpa.domain.Review;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {


    // 1단계
/*    @PersistenceContext
    private EntityManager em;
*/

    // 2단계 : @PersistenceContext -> @Autowired & 생성자로 주입
/*    @Autowired
    private EntityManager em;

    public MemberRepository(EntityManager em) {
        this.em = em;
    }
*/

    // 3단계 : 롬복의 @RequiredArgsConstructor 이용
    private final EntityManager em;

    public void save(Member member) {
            em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member>  findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name=:name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

    // 불완전
    public List<Member> findByEmailPassword(String email, String password) {
        return em.createQuery("Select m from Member m where m.email=:email And m.password=:password", Member.class)
                .setParameter("email", email)
                .setParameter("password", password)
                .getResultList();
    }

    public List<Member> findByEmail(String email) {
        return em.createQuery("select m from Member m where m.email=:email", Member.class)
                .setParameter("email", email)
                .getResultList();
    }

    // 페이징
    public List<Member> getBoardList(int first, int max) {
        int start = (first - 1) * max;

        if(first < 0) {
            first = 0;
        }

        System.out.println("first =" + first + ", max =" + max);

        return em.createQuery("select m from Member m ORDER BY m.id DESC", Member.class)
                .setFirstResult(start)
                .setMaxResults(max)
                .getResultList();
    }

    public int getRowCount() {
        return em.createQuery("select count(i.id) from Member i", Long.class)
                .getFirstResult();
    }

    public Member findByCode(String code) throws Exception {
        return em.createQuery("select m from Member m where m.verifyCode =:code", Member.class)
                .setParameter("code", code)
                .getSingleResult();
    }

}
