package study.datajpa.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.fasterxml.jackson.core.sym.Name;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

//@SpringBootTest
//@Transactional
//@Rollback(false)
class MemberTest {

	@PersistenceContext
	EntityManager em;
	
//	@Test
	void testEntity() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);
				
		
		Member memberA = new Member("memberA", 10, teamA);
		Member memberB = new Member("memberB", 20, teamB);

		em.persist(memberA);
		em.persist(memberB);
		
		
		
		/**
		 * 
		 * 영속성 컨텍스트가 초기화 되지 않은 경우 쿼리 발생하지 않음.
		 * 
		 * 영속성 컨텍스트 초기화 되었다는 가정하에(1차캐시 삭제). 즉, em.flush(); em.clear(); 한 경우	
		 * Member의 Team이 fetch = FetchType.EAGER 이면, member와 team의 조인 쿼리가 나간다.
		 * Member의 Team이 fetch = FetchType.LAZY 이면, member 쿼리 따로 team 쿼리 따로 나간다. team 쿼리는 team의 식별자 호출 시 발생.
		 */
		Member findMemberA =  em.find(Member.class, memberA.getId());
		Member findMemberB =  em.find(Member.class, memberB.getId());
		
		assertThat(findMemberA.getId()).isEqualTo(memberA.getId());
		assertThat(findMemberA.getTeam().getName()).isEqualTo(teamA.getName());
		assertThat(findMemberA).isEqualTo(memberA);
		assertThat(findMemberA.getTeam()).isEqualTo(teamA);
		
		assertThat(findMemberB.getId()).isEqualTo(memberB.getId());
		assertThat(findMemberB.getTeam().getName()).isEqualTo(teamB.getName());
		assertThat(findMemberB).isEqualTo(memberB);
		assertThat(findMemberB.getTeam()).isEqualTo(teamB);
	}
	
//	@Test
	void testEntity2() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);
		
		
		Member memberA = new Member("member1", 10, teamA);
		Member memberB = new Member("member2", 20, teamA);
		Member memberC = new Member("member3", 30, teamB);
		Member memberD = new Member("member4", 40, teamB);
		
		em.persist(memberA);
		em.persist(memberB);
		em.persist(memberC);
		em.persist(memberD);
		
		em.flush();
		em.clear();
		
		List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
		
		for (Member member : members) {
			System.out.println("member : " + member);
			System.out.println("member.team : " + member.getTeam());
		}
	}
}
