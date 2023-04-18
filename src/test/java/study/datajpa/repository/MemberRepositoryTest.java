package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
//	@Test
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
	
	/**
	 * 컬렉션 파라미터 바인딩
	 */
//	@Test
	public void findByNames() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 10);
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		HashMap<String, String> map = new HashMap<>();
		map.put("A", "member1");
		map.put("B", "member2");
		
		List<Member> result = memberRepository.findByNames(map.values());
//		List<Member> result = memberRepository.findByNames(Arrays.asList("member1","member2"));
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getUsername()).isEqualTo("member1");
		assertThat(result.get(1).getUsername()).isEqualTo("member2");
		
	}
	
	/**
	 * 반환타입
	 */
//	@Test
	public void returnType() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 10);
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		/**
		 * jpa에서 List 반환타입인 경우 실제 조회 데이터가 Null 이여도 null이 아닌 빈 객체를 준다.
		 */
		List<Member> result = memberRepository.findListByUsername("1234");
		// 출력결과 - reusltList : []
		System.out.println("reusltList : " + result);
		
		Optional<Member> resultOptional = memberRepository.findOptionalByUsername("1234");
		System.out.println("resultOptional : " + resultOptional.orElse(member1));
	}
	
	/**
	 * 
	 * 
	 * 스프링 데이터 JPA 페이징과 정렬
		· 페이징과 정렬 파라미터
			· org.springframework.data.domain.Sort : 정렬 기능 인터페이스
			· org.springframework.data.domain.Pageable : 페이징 기능 인터페이스 (내부에 Sort 포함)

		· 특별한 반환 타입
			· org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징 인터페이스
			· org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 기능을 제공 인터페이스 (자동으로 limit + 1 요청함)
			  (내부적으로 limit + 1조회)
			· List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
			
			
		Page 인터페이스
			· int getTotalPages(); //전체 페이지 수
	 		· long getTotalElements(); //전체 데이터 수	
			· <U> Page<U> map(Function<? super T, ? extends U> converter); //변환기	
			
		Slice 인터페이스
			· int getNumber(); //현재 페이지
			· int getSize(); //페이지 크기
			· int getNumberOfElements(); //현재 페이지에 나올 데이터 수
			· List<T> getContent(); //조회된 데이터
			· boolean hasContent(); //조회된 데이터 존재 여부
			· Sort getSort(); //정렬 정보
			· boolean isFirst(); //현재 페이지가 첫 페이지 인지 여부
			· boolean isLast(); //현재 페이지가 마지막 페이지 인지 여부
			· boolean hasNext(); //다음 페이지 여부
			· boolean hasPrevious(); //이전 페이지 여부
			· Pageable getPageable(); //페이지 요청 정보
			· Pageable nextPageable(); //다음 페이지 객체
			· Pageable previousPageable();//이전 페이지 객체
			· <U> Slice<U> map(Function<? super T, ? extends U> converter); //변환기	
	 * 
	 */
	@Test
	public void paging() {
//		Team team  = new Team("teamA");
//		teamRepository.save(team);
		
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));
		memberRepository.save(new Member("member6", 10));
//		memberRepository.save(new Member("member1", 10, team));
//		memberRepository.save(new Member("member2", 10, team));
//		memberRepository.save(new Member("member3", 10, team));
//		memberRepository.save(new Member("member4", 10, team));
//		memberRepository.save(new Member("member5", 10, team));
//		memberRepository.save(new Member("member6", 10, team));
		
		int age = 10;
		PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
		
		/**
		 * 
		 	· 두 번째 파라미터로 받은 Pageable 은 인터페이스다. 따라서 실제 사용할 때는 해당 인터페이스를 구현한
			  org.springframework.data.domain.PageRequest 객체를 사용한다. 
			· PageRequest 생성자의 첫 번째 파라미터에는 현재 페이지를, 두 번째 파라미터에는 조회할 데이터 수를 입력한다. 
			  여기에 추가로 정렬 정보도 파라미터로 사용할 수 있다. 
			· 참고로 페이지는 0부터 시작한다.

			주의: Page는 1부터 시작이 아니라 0부터 시작이다
		 * 
		 */
		// when
//		Page<Member> page = memberRepository.findPageByAge(age, pageRequest);
//		
//		// then
//		assertThat(page.getContent().size()).isEqualTo(3);
//		assertThat(page.getTotalElements()).isEqualTo(6);
//		assertThat(page.getNumber()).isEqualTo(0);
//		assertThat(page.getTotalPages()).isEqualTo(2);
//		assertThat(page.isFirst()).isTrue();
//		assertThat(page.hasNext()).isTrue();
//		
//		
//		// when slice는 요청한 limit + 1 을 요청한다. 더보기 페이징을 할 경우 사용.
//		Slice<Member> pageSlice = memberRepository.findSliceByAge(age, pageRequest);
//		
//		// then
//		assertThat(pageSlice.getContent().size()).isEqualTo(3);
//		assertThat(pageSlice.getNumber()).isEqualTo(0);
//		assertThat(pageSlice.isFirst()).isTrue();
//		assertThat(pageSlice.hasNext()).isTrue();
//		
//		// 단순하게 리스트로 리턴타입 적용해서 받아도 됨
//		List<Member> pageList = memberRepository.findListByAge(age, pageRequest);
		
		// 카운트 쿼리 분리
		Page<Member> pageCount = memberRepository.findCountQueryByAge(age, pageRequest);
		System.out.println("size : "+pageCount.getContent().size());
		System.out.println("totalCount : "+pageCount.getTotalElements());
	}
}
