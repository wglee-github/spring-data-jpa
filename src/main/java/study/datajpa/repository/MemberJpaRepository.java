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
	 * 스프링 컨테이너가 JPA에 있는 entityManager를 @PersistenceContext 이 선언된 필드에 주입해 준다. 
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
	
	public Long count() {
		return em.createQuery("select count(m) from Member m", Long.class)
				.getSingleResult();
	}
	
	public Member find(Long memberId){
		return em.find(Member.class, memberId);
	}
}
