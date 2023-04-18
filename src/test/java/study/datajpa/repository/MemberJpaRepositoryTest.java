package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.NamedQuery;
import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

	@Autowired MemberJpaRepository memberJpaRepository;

	
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
	@Test
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
}
