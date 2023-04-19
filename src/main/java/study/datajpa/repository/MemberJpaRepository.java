package study.datajpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import study.datajpa.entity.Member;

@Repository
public class MemberJpaRepository {

	/**
	 * 
	 * JPA 스펙에서 제공하는 기능으로 영속성 컨텍스트를 주입하는 표준 애노테이션
	 	
	 	* 스프링 컨테이너가 @PersistenceContext로 지정된 프로퍼티에 아래 두 가지 중 한 가지로 JPA에 있는 EntityManager를 주입해줍니다.
			· EntityManagerFactory에서 새로운 EntityManager를 생성하여 주입합니다.
			· Transaction에 의해 기존에 생성된 EntityManager를 주입합니다.
	 	
	 	* @Autowired 를 사용하여 EntityManager를 선언하면 안되고 @PersistenceContext 로 선언해야 하는 이유
		  	· EntityManager를 사용할 때 주의해야 할 점은 여러 쓰레드가 동시에 접근하면 동시성 문제가 발생하기때문에 쓰레드 간에는 EntityManager를 공유해서는 안됩니다.
			· 일반적으로 스프링은 싱글톤 기반으로 동작하기에 빈은 모든 쓰레드가 공유합니다.
			· 그러나 @PersistenceContext으로 EntityManager를 주입받아도 동시성 문제가 발생하지 않습니다.

			· 동시성 문제가 발생하지 않는 이유는
				· 스프링 컨테이너가 초기화되면서 @PersistenceContext으로 주입받은 EntityManager를 Proxy로 감쌉니다.
				· 그리고 EntityManager 호출 시 마다 Proxy를 통해 EntityManager를 생성하여 Thread-Safe를 보장합니다.
	 * 
	 */
	@PersistenceContext
	private EntityManager em;
	
	public Member save(Member member) {
		em.persist(member);
		return member;
	}
	
	public void delete(Member member) {
		em.remove(member);
	}
	
	public List<Member> findAll(){
		return em.createQuery("select m from Member m", Member.class)
				.getResultList();
	}
	
	public Optional<Member> findById(Long id){
		Member member = em.find(Member.class, id);
		return Optional.ofNullable(member);
	}
	
	public long count() {
		return em.createQuery("select count(m) from Member m", Long.class)
				.getSingleResult();
	}
	
	public Member find(Long memberId){
		return em.find(Member.class, memberId);
	}
	
	// 메소드명 쿼리 예
	public List<Member> findByUsernameAndAgeGreaterThen(String username, int age){
		return em.createQuery("select m from Member m where m.username = :username and m.age > :age", Member.class)
				.setParameter("username", username)
				.setParameter("age", age)
				.getResultList();
	}
	
	// NamedQuery 예
	public List<Member> findByUsername(String username){
		return em.createNamedQuery("Member.findByUsername", Member.class)
				.setParameter("username", username)
				.getResultList();
	}
	
	public List<Member> findByPage(int age, int offset, int limit){
		return em.createQuery("select m from Member m where m.age = :age order by m.username desc", Member.class)
				.setParameter("age", age)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}
	
	public long totalCount(int age) {
		return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
				.setParameter("age", age)
				.getSingleResult();
	}
	
	public int bulkAgePlus(int age) {
		return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
				.setParameter("age", age)
				.executeUpdate();
	}
	
	
	
	
	
	
	
	
	
	
	
}
