package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NamedQuery;
import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

	@Autowired MemberJpaRepository memberJpaRepository;
	@Autowired EntityManager em;
	
//	@Test
	public void testMember() {
		Member member = new Member("memberA");
		Member saveMember =  memberJpaRepository.save(member);
		
		Member findMember = memberJpaRepository.find(saveMember.getId());
		
		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member);
	}
	
	
//	@Test
	public void basicCRUD() {
		
		Member member1 = new Member("memeber1");
		Member member2 = new Member("memeber2");
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);
		
		// 단건 조회  검증
		Member findMember1 = memberJpaRepository.find(member1.getId());
		Member findMember2 = memberJpaRepository.find(member2.getId());
		
		// dirty check(변경감지)
//		findMember1.setUsername("memberAA");
		
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);
		
		// 리스트 조회 검증
		List<Member> members = memberJpaRepository.findAll();
		assertThat(members.size()).isEqualTo(2);
		
		// 카운트 검증
		long memberCnt = memberJpaRepository.count();
		assertThat(memberCnt).isEqualTo(2);
		
		// 삭제 검증
		memberJpaRepository.delete(member1);
		memberJpaRepository.delete(member2);
		
		long memberCount =  memberJpaRepository.count();
		assertThat(memberCount).isEqualTo(0);
		
	}
	
	/*
	 * 쿼리 메소드 기능
	 	· 메소드 이름으로 쿼리 생성
	 */
//	@Test
	public void findByUsernameAndAgeGreaterThen() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member1", 20);
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);
		
		List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThen("member1", 15);
		assertThat(members.get(0).getUsername()).isEqualTo("member1");
		assertThat(members.get(0).getAge()).isEqualTo(20);
		assertThat(members.size()).isEqualTo(1);
		
	}
	
	/**
	 * 쿼리 메소드 기능
	 	· @NamedQuery 테스트
	 */
//	@Test
	public void testNamedQuery() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 10);
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);
		
		List<Member> findMemer1 = memberJpaRepository.findByUsername("member1");
		List<Member> findMemer2 = memberJpaRepository.findByUsername("member1");
		assertThat(findMemer1.get(0).getUsername()).isEqualTo("member1");
		assertThat(findMemer1.size()).isEqualTo(1);
		assertThat(findMemer2).isNotEqualTo(findMemer2);
	}
	
//	@Test
	public void paging() {
		// given
		memberJpaRepository.save(new Member("member1", 10));
		memberJpaRepository.save(new Member("member2", 10));
		memberJpaRepository.save(new Member("member3", 10));
		memberJpaRepository.save(new Member("member4", 10));
		memberJpaRepository.save(new Member("member5", 10));
		memberJpaRepository.save(new Member("member6", 10));
		
		int age = 10;
		int offset = 0;	// 시작 인덱스
		int limit = 3;	// 조회할 갯수
		// when
		List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
		long totalCount = memberJpaRepository.totalCount(age);
		
		// then
		assertThat(members.size()).isEqualTo(3);
		assertThat(totalCount).isEqualTo(6);
		members.stream().forEach(m -> System.out.println(m));
	}
	
	
	@Test
	public void bulkUpdate() {
		// given
		memberJpaRepository.save(new Member("member1", 10));
		memberJpaRepository.save(new Member("member2", 15));
		memberJpaRepository.save(new Member("member3", 20));
		memberJpaRepository.save(new Member("member4", 34));
		memberJpaRepository.save(new Member("member5", 40));
		
		// when
		int age = 20;
		int bulkCount = memberJpaRepository.bulkAgePlus(age);
		
//		em.flush();
//		em.clear();
		/**
		 *  1. save 에서 member 정보를 저장하면서 영속성 컨텍스트(1차 캐시)에 저장 되었다.
		 *  2. 그리고 bulkUpdate 에서 JPQL이 실행되어 flush가 일어나서 Member의 save 쿼리도 DB에 전달 된다.
		 *  3. 그런데 아래 조회 JPQL의 경우 SQL은 나가겠지만 실제 데이터는 영속성 컨텍스트에서 가지고 오기 때문에
		 *     bulkUpdate 에서 변경된 데이터를 얻어오지 못한다. 
		 *  4. 따라서 제대로된 검증을 하려면 em.clear(); 를 통해 1차 캐시를 삭제해 줘야 한다. 혹시 모르니 flush();도 해주자.
		 */
		List<Member> members3 = memberJpaRepository.findByUsername("member3");
		List<Member> members4 = memberJpaRepository.findByUsername("member4");
		List<Member> members5 = memberJpaRepository.findByUsername("member5");
		
		System.out.println(members3.get(0).getAge());
		
		// then
		assertThat(bulkCount).isEqualTo(3);
		assertThat(members3.get(0).getAge()).isEqualTo(21);
		assertThat(members4.get(0).getAge()).isEqualTo(35);
		assertThat(members5.get(0).getAge()).isEqualTo(41);
	}
	
	
	
	
	
	
	
	
	
	
	
}
