package study.datajpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

/**
 * 
 * 화면에 맞춘 쿼리, 복잡한 조회 쿼리 등을 위한 repository
 * 
 * @Repository
 * 1. 스프링이 로드 될 때 componet scan를 통해 해당 어노테이션이 걸려있는 클랙스를 bean에 등록한다.
 * 2. JDBC나 JPA에서 예외가 발생하면 영속성 계층에 있는 예외들을 스프링에서 사용할 수 있는 예외로 바꿔준다.
 *    그래서 서비스계층이나, 컨트롤러 계층으로 예외를 던질 때 JDBC나 JPA의 예외를 보내는게 아니라 스프링이 제공하고 예외로 바꿔서 넘긴다.
 *    ** 하부 구현 기술을 JDBC -> JPA를 바꿔도 컨트롤러 계층에서는 예외를 처리하는 매커니즘의 변경이 안일어난다. 기존 비지니스 로직에 영향을 주지 않는다.  
 * 
 * 
 */
@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

	private final EntityManager em;
	
	public List<Member> findAllMembers(){
		return em.createQuery("select m from Member m", Member.class)
				.getResultList();
	}
}
