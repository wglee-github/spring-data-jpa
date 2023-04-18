package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import jakarta.transaction.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

	@Autowired MemberRepository memberRepository;
	@Autowired TeamRepository teamRepository;
	
	
//	@Test
	void testMember() {
		
		Member member  = new Member("memberA");
		Member saveMember = memberRepository.save(member);
		
		Member findMember = memberRepository.findById(saveMember.getId()).get();
		
		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member);
	}

	
//	@Test
	public void basicCRUD() {
		Member member1 = new Member("memeber1");
		Member member2 = new Member("memeber2");
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		// 단건 조회  검증
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();
		
		// dirty check(변경감지)
//		findMember1.setUsername("memberAA");
		
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);
		
		// 리스트 조회 검증
		List<Member> members = memberRepository.findAll();
		assertThat(members.size()).isEqualTo(2);
		
		// 카운트 검증
		long memberCnt = memberRepository.count();
		assertThat(memberCnt).isEqualTo(2);
		
		// 삭제 검증
		memberRepository.delete(member1);
		memberRepository.delete(member2);
		
		long memberCount =  memberRepository.count();
		assertThat(memberCount).isEqualTo(0);
		
	}
	
//	@Test
	public void findByUsernameAndAgeGreaterThen() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member1", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("member1", 15);
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
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		List<Member> findMemer1 = memberRepository.findByUsername("member1");
		List<Member> findMemer2 = memberRepository.findByUsername("member2");
		assertThat(findMemer1.get(0).getUsername()).isEqualTo("member1");
		assertThat(findMemer1.size()).isEqualTo(1);
		assertThat(findMemer2).isNotEqualTo(member2);
	}
	
	/**
	 * 쿼리 메소드 기능
	 	· @Query - 리파지토리 메소드에 쿼리 정의
	 */
//	@Test
	public void testQuery() {
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		
		List<Member> findMemer1 = memberRepository.findUser("member1",10);
		assertThat(findMemer1.get(0)).isEqualTo(member1);
	}
	
	/**
	 * 쿼리 메소드 기능
	 	· @Query - 리파지토리 메소드에 쿼리 정의
	 		· 값 조회
	 */
//	@Test
	public void testUsernameList() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 10);
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		List<String> result = memberRepository.findUsernameList();
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0)).isEqualTo("member1");
		assertThat(result.get(1)).isEqualTo("member2");
		
	}
	
	/**
	 * 쿼리 메소드 기능
	 	· @Query - 리파지토리 메소드에 쿼리 정의
	 		· DTO 조회
	 */
	@Test
	public void findMemberDto() {
		Team team1 = new Team("team1");
		teamRepository.save(team1);
		
		Member member1 = new Member("member1", 10, team1);
		memberRepository.save(member1);
		
		List<MemberDto> result = memberRepository.findMemberDto();
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0).getId()).isEqualTo(member1.getId());
		assertThat(result.get(0).getUsername()).isEqualTo("member1");
		assertThat(result.get(0).getTeamName()).isEqualTo(member1.getTeam().getName());
		
	}
}
