package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.annotation.Rollback;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest

/**
 * 
 	* @Transactional 트랜잭션 적용
		· JPA의 모든 변경은 트랜잭션 안에서 동작
		· 스프링 데이터 JPA는 변경(등록, 수정, 삭제) 메서드를 트랜잭션 안에서 처리한다.
		· 서비스 계층에서 트랜잭션을 시작하지 않으면 리파지토리에서 트랜잭션 시작
		· 서비스 계층에서 트랜잭션을 시작하면 리파지토리는 해당 트랜잭션을 전파 받아서 사용
		· 그래서 스프링 데이터 JPA를 사용할 때 트랜잭션이 없어도 데이터 등록, 변경이 가능했음(사실은
		· 트랜잭션이 리포지토리 계층에 걸려있는 것임) 
		
	* @Transactional(readOnly = true)
		· 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 readOnly = true 옵션을 사용하면 플러시를 생략해서 DB에 데이터를 보내지 않는다. 
		  이로 인해 약간의 성능 향상을 얻을 수 있음	
 *
 */
@Transactional
@Rollback(false)
class MemberRepositoryTest {

	@Autowired MemberRepository memberRepository;
	@Autowired TeamRepository teamRepository;
	@PersistenceContext EntityManager em;
	
	private MemberQueryRepository memberQueryRepository;
	
	@Autowired
	public MemberRepositoryTest(MemberQueryRepository memberQueryRepository) {
		this.memberQueryRepository = memberQueryRepository;
	}
	
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
//	@Test
	public void paging() {
		Team team  = new Team("teamA");
		teamRepository.save(team);
		
		// given
		memberRepository.save(new Member("member1", 10, team));
		memberRepository.save(new Member("member2", 10, team));
		memberRepository.save(new Member("member3", 10, team));
		memberRepository.save(new Member("member4", 10, team));
		memberRepository.save(new Member("member5", 10, team));
		memberRepository.save(new Member("member6", 10, team));
		
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
		Page<Member> page = memberRepository.findPageByAge(age, pageRequest);
		
		// then
		assertThat(page.getContent().size()).isEqualTo(3);
		assertThat(page.getTotalElements()).isEqualTo(6);
		assertThat(page.getNumber()).isEqualTo(0);
		assertThat(page.getTotalPages()).isEqualTo(2);
		assertThat(page.isFirst()).isTrue();
		assertThat(page.hasNext()).isTrue();
		
		
		// when slice는 요청한 limit + 1 을 요청한다. 더보기 페이징을 할 경우 사용.
		Slice<Member> pageSlice = memberRepository.findSliceByAge(age, pageRequest);
		
		// then
		assertThat(pageSlice.getContent().size()).isEqualTo(3);
		assertThat(pageSlice.getNumber()).isEqualTo(0);
		assertThat(pageSlice.isFirst()).isTrue();
		assertThat(pageSlice.hasNext()).isTrue();
		
		/**
		 *  단순하게 리스트로 리턴타입 적용해서 받아도 됨
		 */
		// when
		List<Member> pageList = memberRepository.findListByAge(age, pageRequest);
		
		/**
		 *  카운트 쿼리 분리
		 */
		// when
		Page<Member> pageCount = memberRepository.findCountQueryByAge(age, pageRequest);
		System.out.println("size : "+pageCount.getContent().size());
		System.out.println("totalCount : "+pageCount.getTotalElements());
		
		/**
		 * 
		 * Top, First 사용 참고
			https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limitquery-result
		 */ 
		List<Member> top3Result = memberRepository.findTop3ByAge(age);
		
		/**
		 * 페이지를 유지하면서 엔티티를 DTO로 변환하기
		 * API의 response로 전달하면 페이징 정보도 같이 JSON으로 전달 된다.
		 */
		Page<Member> mePage = memberRepository.findPageByAge(age, pageRequest);
		Page<MemberDto> mapDto = mePage.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
	}
	
	/**
	 * 
	 	* 벌크성 수정, 삭제 쿼리는 @Modifying 어노테이션을 사용
			· 사용하지 않으면 다음 예외 발생
			· org.hibernate.hql.internal.QueryExecutionRequestException: Not supported for DML operations
			· 벌크성 쿼리를 실행하고 나서 영속성 컨텍스트 초기화: @Modifying(clearAutomatically = true)
			  (이 옵션의 기본값은 false )
			· 이 옵션 없이 회원을 findById 로 다시 조회하면 영속성 컨텍스트에 과거 값이 남아서 문제가 될 수 있다. 
			· 만약 다시 조회해야 하면 꼭 영속성 컨텍스트를 초기화 하자.

	 	* 참고: 벌크 연산은 영속성 컨텍스트를 무시하고 실행하기 때문에, 영속성 컨텍스트에 있는 엔티티의 상태와 DB에 엔티티 상태가 달라질 수 있다.

	 	* 권장하는 방안
 			1. 영속성 컨텍스트에 엔티티가 없는 상태에서 벌크 연산을 먼저 실행한다.
 			2. 부득이하게 영속성 컨텍스트에 엔티티가 있으면 벌크 연산 직후 영속성 컨텍스트를 초기화 한다
	 * 
	 */
//	@Test
	public void bulkUpdate() {
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 15));
		memberRepository.save(new Member("member3", 20));
		memberRepository.save(new Member("member4", 34));
		memberRepository.save(new Member("member5", 40));
		
		// when
		int age = 20;
		int bulkCount = memberRepository.bulkAgePlus(age);
		
//		em.flush();
//		em.clear();
		
		/**
		 *  save 에서 member 정보를 저장하면서 영속성 컨텍스트(1차 캐시)에 저장 되었다.
		 *  그래서 아래에서 JPQL로 조회를 하면 SQL은 나가겠지만 실제 데이터는 영속성 컨텍스트에서 가지고 오기 때문에
		 *  bulkUpdate 에서 변경된 데이터를 얻어오지 못한다. 
		 *  따라서 제대로된 검증을 하려면
		 	1. em.clear(); 를 통해 1차 캐시를 삭제해준다. 혹시 모르니 flush();도 해주자.
		   	2. repository에  @Modifying 에 clearAutomatically = true 를 선언해 주면 em.clear(); 를 해주지 않아도 된다.
		   	   Ex. @Modifying(clearAutomatically = true) 
		 */
		List<Member> members3 = memberRepository.findByUsername("member3");
		List<Member> members4 = memberRepository.findByUsername("member4");
		List<Member> members5 = memberRepository.findByUsername("member5");
		
		System.out.println(members3.get(0).getAge());
		
		// then
		assertThat(bulkCount).isEqualTo(3);
		assertThat(members3.get(0).getAge()).isEqualTo(21);
		assertThat(members4.get(0).getAge()).isEqualTo(35);
		assertThat(members5.get(0).getAge()).isEqualTo(41);
	}
	
	/**
	 * (fetch = FetchType.EAGER)
	 * EAGER 는 Member와 Team을join 해서 가지고 오는게 아니라 Member 조회 쿼리 나가고 바로 Team 쿼리 나가는 방식이다. 헷갈리지 말자. join 아니다!
	 * 
	 * (fetch = FetchType.LAZY)
	 * LAZY 참조 객체의 식별자를 호출 시, Team 객체의 getName() 호출 시 쿼리가 나가는 방식. 그 전까지 Team객체는 proxy 객체이다.
	 */
//	@Test
	public void findMemberLazy() {
		
		// given
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamA);
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		em.flush();
		em.clear();
		
		// when
		/**
		 * 일반 조인과 Fetch 조인의 차이
		 * 
		 * @Query("select m from Member m left join m.team t") -> left join을 하면 select m from member 로 나가고 join 쿼리가 나가지 않음.
		 * 일반 조인은 Member 객체 정보만 조회한다. select 파라미터에 Member 컬럼만 있음.
		 * 
		 * @Query("select m from Member m left join fetch m.team t")
		 * fetch 조인은 Member 객체정보를 조회 할 때 연관된(조인한) Team 객체 정보도 같이 한번에 조회한다.
		 * select 파라미터에 Member 컬럼, Team 컬럼 모두 있음. 
		 */
//		List<Member> members = memberRepository.findMemberFetchJoin();
		
		/**
		 * 
		 * @EntityGraph  @NamedEntityGraph
		 * JPA에서 제공. JPA 표준 스펙
		 * 
		 *  JPA에서 제공하는 메소도 오버라이드해서 @EntityGraph 적용 가능
		 */
//		List<Member> members = memberRepository.findAll();
		
		// JPQL에 기본 쿼리만 작성하고, @EntityGraph 적용 가능
//		List<Member> members = memberRepository.findMemberEntityGraph();
		
		// 메소드 이름으로 쿼리 생성 한 경우에도 @EntityGraph 적용 가능
		List<Member> members = memberRepository.findEntityGraphByUsername("member1");
		
		for (Member member : members) {
			System.out.println("member = " + member);
			System.out.println("member.team = " + member.getTeam().getClass());
			System.out.println("member.team.name = " + member.getTeam().getName());
		}
		
		// then
		
	}
	
	/**
	 *
	 	* JPA Hint : 진짜 트래픽이 많은 곳을 선별해서 
   			· JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
   		
   			· 변경감지(dirty checking)를 위해서는 원본과 수정본 두개를 관리해야한다.
   			· 그렇다는건 그만큼 메모리를 사용한다는 뜻인데, 변경감지를 사용하지 않도록 Hint를 사용하여 조회만 할 수 있다.
	 
	 	* 참고
		  · 성능테스트 해보고 결정하면 된다.
		  · 전체 애플리케이션에 최적화 한다고 readOnly 옵션을 다 넣고 최적화 한다고 해봐야 큰 이점이 없다.
		  · 전체 애플리케이션 중 성능 이슈가 가장 많이 발생하는 곳이 복잡한 쿼리로 조회하는 경우가 대부분이다.
		  · 이때 해당 쿼리를 공수를 들여서 튜닝을 할건지는 고민해 봐야 한다. 결국 성능테스트를 해보고 정말 중요한 몇 곳만 추려서 해당 튜닝이 이점이 있는 경우에만 적용하는게 좋다.
		  · 그리고 진짜 중요한 쿼리인데 조회성능 이슈가 있으면 이미 cache를 활용하든, redis를 사용해서 풀어야 하는게 맞을 수도 있다.
		  · 좋아 보여서 처음부터 튜닝을 싹 하는건 좋지않은 접근이다.  
	 * 
	 */
//	@Test
	public void queryHint() {
		// given
		Member member = new Member("member1");
		memberRepository.save(member);

		em.flush();
		em.clear();
		
		// when
		Member findMember =  memberRepository.findReadOnlyByUsername(member.getUsername());
		findMember.setUsername("member2"); 

		// 필수 값 아님. 마치 flush 해야 업데이트 쿼리 실행되는것 처럼 말함. ㅠ
		// @Transactional 선언하면 메소드 끝날때 자동으로 flush 일어남.
		// 설명을 직관적으로 하려고 사용한듯 ( 헷갈릴뻔 했잖아.. ㅡ.ㅡ)
		em.flush(); // update 쿼리 실행되지 않는다.
	}
	
	/**
	 * select for update
	 * 실시간 트래픽이 많은 서비스에서도 가급적 lock을 사용하면 안된다.
	 * PESSIMISTIC : 실제 락을 건다
	 * OPTIMISTIC : 실제 락을 걸지 않고 처리? 
	 * 
	 */
//	@Test
	public void lock() {
		// given
		Member member = new Member("member1");
		memberRepository.save(member);

		em.flush();
		em.clear();
		
		// when
		List<Member> members =  memberRepository.findLockByUsername("member1");
	}
	
	/**
	 * 
	 * 사용자 정의 리포지토리
	 * - JpaRepository를 상속받은 MemberRepository에 새로운 메소드를 정의하여 직접 구현하고 싶은경우
	 * - MemberRepository는 인터페이스 이기때문에 새롭게 메소드를 정의한 후 구현하기 위해서는 MemberRepository 인터페이스를 implements 해야한다.
	 * - 그러면 기존에 MemberRepository에 선언한 모든 메소드 + 상속받은 JpaRepository 메소드 까지 모두 구현해야 한다. 이해되지? 
	 * - 따라서 별도의 인터페이스와 구현클래스를 생성해서 활용하는 방법을 알아보자
	 * 
	 * 1. 사용자 정의 interface A를 만든다.
	 * 2. A interface의 구현체 클래스를 만든다.
	 * - 클래스명은 기존 리포지토리명 + Impl 로 지어줘야 한다. 그러면 스프링 데이터 JPA가 인식해서 스프링 빈으로 등록해 준다.
	 * 3. JpaRepository를 상속받은 기존 인터페이스에 extends A 인터페이스를 상속 받는다. 
	 * 
	 * Ex. MemberRepository 에서 사용할 사용자 정의 리포지토리를 만든다고 가정하면
	 	  1. MemberRepositoryCustom : 사용자 정의 interface를 만든다
	 	  2. MemberRepositoryImpl : 사용자 정의 interface 구현체 클래스를 만든다.
	 	  3. public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom 이렇게 해주면 됨.
	 	  
	 	  
		참고: 실무에서는 주로 QueryDSL이나 SpringJdbcTemplate을 함께 사용할 때 사용자 정의 리포지토리 기능 자주 사용한다.
		
		* 참고 
			· 항상 사용자 정의 리포지토리가 필요한 것은 아니다. 그냥 임의의 리포지토리를 만들어도 된다. 
			
			· 핵심 비지니스 로직 repository와 화면에 맞춘 복잡한 쿼리 용 repository는 분리하는게 좋다. 
				· 사용자 정의 리포지토리 활용법을 사용하여 한곳에 다 넣으면 복잡도가 높은 올라간다.
				· 핵심 비지니스 로직 repository는 수정할 이슈가 많지 않지만
				· 화면에 맞춘 복잡한 쿼리용 repository 수정 이슈가 많다. 
			
			· 예를들어 MemberQueryRepository를 인터페이스가 아닌 클래스로 만들고 스프링 빈으로 등록해서
			· 그냥 직접 사용해도 된다. 물론 이 경우 스프링 데이터 JPA와는 아무런 관계 없이 별도로 동작한다
	 	  
	 */
//	@Test
	public void callCustom() {
		// given
		Member member = new Member("member1");
		memberRepository.save(member);
		
		em.flush();
		em.clear();
		
		// when
		List<Member> members =  memberRepository.findMemberCustom();
	}
	
	/**
	 * 화면에 맞춘 쿼리나, 복잡한 조회 쿼리 등은 repository를 분리해서 사용하자
	 */
//	@Test
	public void queryRepository() {
		new MemberRepositoryTest(new MemberQueryRepository(em));
		memberQueryRepository.findAllMembers();
	}
	
	/**
	 * 
	 * spring main class에 @EnableJpaAuditing 선언해줘야 한다.
	 * 
	 * 공통 컬럼이 존재하는 경우 별도의 class로 분리할 수 있다.
	 * 그리고 분리한 class에 @MappedSuperclass 를 선언해 주면 된다.
	 * 
	 
	 * 참고
	 	· 저장시점에 등록일, 등록자는 물론이고, 수정일, 수정자도 같은 데이터가 저장된다. 
	 	· 데이터가 중복 저장되는 것 같지만, 이렇게 해두면 변경 컬럼만 확인해도 마지막에 업데이트한 유저를 확인 할 수 있으므로 유지보수 관점에서 편리하다. 
	 	· 이렇게 하지 않으면 변경 컬럼이 null 일때 등록 컬럼을 또 찾아야 한다.
	
	 * 참고 
	 	· 저장시점에 저장데이터만 입력하고 싶으면 spring main clas에 @EnableJpaAuditing(modifyOnCreate = false) 옵션을 사용하면 된다.
	 * 
	 */
//	@Test
	public void baseEntity() throws Exception {
		// given
		Member member = new Member("member1");
		memberRepository.save(member); //@PrePersist 실행
		
		Thread.sleep(1000);
//		member.setUsername("member2");
//		System.out.println("member createDate = " + member.getCreateDate());
//		System.out.println("member updateDate 1 = " + member.getUpdateDate());
		
		em.flush(); //@PreUpdate 실행
//		System.out.println("member updateDate 2 = " + member.getUpdateDate());
		em.clear();
		
		
		// when
		Member findMember = memberRepository.findById(member.getId()).get();
		
		
		// then
		System.out.println("findmember username = " + findMember.getUsername());
		System.out.println("findmember lastModifiedDate = " + findMember.getLastModifiedDate());
		System.out.println("findmember createBy = " + findMember.getCreateBy());
		System.out.println("findmember lastModifiedBy = " + findMember.getLastModifiedBy());
	}
	
	/**
	 * 
	 * 우선 class 상단에 @Transactional 주석처리 한 후 테스트 해야한다.
	 * 
	 * memberRepository 의 save는 JpaRepository의 구현체 메소드를 사용한다.
	 * 내부적으로 들어가보며 save 메소드에 @Transactional 이 선언되어 있는걸 볼 수 있다.
	 * 그래서 별도의 @Transactional 를 선언하지 않아도 저장이 되는 것이다.
	 * 그런데 영속성 컨텍스트 입장에서 보면 동일한 transaction 내에서는 엔티티의 동일성을 보장해준다.
	 * 그래서 save를 실행한 후에는 transaction이 끝나기 때문에 이후의 영속성 컨텍스트도 사라지게 된다.
	 * 따라서 로직을 구현할 때 이 부분을 주의해서 작성해야 한다.
	 * 
	 * 아래 save 와 findById는 다른 transaction 이다.
	 * 
	 */
//	@Test
	public void persistenceContextTest() {
		Member member = new Member("member1");
		
		memberRepository.save(member); // insert 쿼리 생성
		
		Member findMember = em.find(Member.class, member.getId());	// select 쿼리 생성 ( 같은 transaction 내에 없기 때문에 조히 할 때 마다 쿼리가 나간다)
		Member findMember2 = em.find(Member.class, member.getId()); // select 쿼리 생성 ( 같은 transaction 내에 없기 때문에 조히 할 때 마다 쿼리가 나간다)
		
//		Member findMember = memberRepository.findById(member.getId()).get(); // select 쿼리 생성
		System.out.println(" findMember = " + findMember);
		
	}
	
	/**
	 * 
	 	* Projections - 인터페이스
		 	· 엔티티 대신에 DTO를 편리하게 조회할 때 사용
			· 전체 엔티티가 아니라 만약 회원 이름만 딱 조회하고 싶으면?
			
		· Closed Projections
			· Ex. String getUsername(); getter 메소드를 필드명에 맞게 잘 생성해 주면 된다. 
			· 매칭 되는 필드만 DB에서 조회해 온다. select username from member;
		· Open Projections
			· Ex. 메소드 위에 @Value("#{target.username + ' ' + target.age}") 를 적어 주면 된다.	
			· 단! 이렇게 SpEL문법을 사용하면, DB에서 엔티티 필드를 다 조회해온 다음에 계산한다! 따라서 JPQL SELECT 절 최적화가 안된다.
			  select * from member;
	 */
//	@Test
	public void projectionsInf() {
		Team teamA = new Team("teamA");
		teamRepository.save(teamA);
		
		Member member1 = new Member("member1", 0, teamA);
		Member member2 = new Member("member2", 0, teamA);
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		em.flush();
		em.clear();
		
		List<UsernameOnly> usernameOnlies = memberRepository.findProjectionsByUsername("member1");
		for (UsernameOnly usernameOnly : usernameOnlies) {
			System.out.println("usernameOnly = " + usernameOnly.getUsername());
		}
		
	}
	
	/**
	 * 
	 	* Projection - 클래스 기반
	 		· 생성자의 파라미터 이름으로 매칭
	 */
//	@Test
	public void projectionsDTO() {
		Team teamA = new Team("teamA");
		teamRepository.save(teamA);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		em.flush();
		em.clear();
		
		List<UsernameOnlyDto> usernameOnlies = memberRepository.findProjectionsDTOByUsername("member1");
		for (UsernameOnlyDto usernameOnly : usernameOnlies) {
			System.out.println("usernameOnly = " + usernameOnly.getUsername());
			System.out.println("usernameOnly = " + usernameOnly.getAge());
		}
		
	}
	
	/**
	 * 
	 	* Projection - 동적
	 		·  Generic type을 주면, 동적으로 프로젝션 데이터 번경 가능
	 */
//	@Test
	public void projectionsGeneric() {
		Team teamA = new Team("teamA");
		teamRepository.save(teamA);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		/**
		 * 해당 메소드는 entitymanger를 직접 사용하는게 아니기 때문에 repository 별로 Transaction이 분리되어 있다.  
		 * save() 메소드 별로 각각 독립된 Transaction이다.
		 * 그런데 지금 테스트를 위해 MemberRepository class에 @Transactional 를 선언했기 때문에 해당 메소드 안에서는 영속성 컨텍스트가 공유가 된다.
		 * 그래서 이런 케이스인 경우에만 em.flush(); em.clear(); 가 의미가 있다.
		 * 
		 * 현재 해당 class의 상단에 @Transactional 이 없어도 동작에는 전혀 문제가 없다.
		 */
		em.flush();
		em.clear();
		
		List<UsernameOnlyDto> usernameOnlyDtos = memberRepository.findProjectionsGenericByUsername("member1", UsernameOnlyDto.class);
		for (UsernameOnlyDto usernameOnly : usernameOnlyDtos) {
			System.out.println("UsernameOnlyDto username = " + usernameOnly.getUsername());
			System.out.println("UsernameOnlyDto age = " + usernameOnly.getAge());
		}
		
		List<UsernameOnly> usernameOnlies = memberRepository.findProjectionsGenericByUsername("member2", UsernameOnly.class);
		for (UsernameOnly usernameOnly : usernameOnlies) {
			System.out.println("UsernameOnly username = " + usernameOnly.getUsername());
			System.out.println("UsernameOnly age = " + usernameOnly.getAge());
		}
		
	}
	
	/**
	 * Native Query Projections
	 * 
	 * 네이티브 쿼리를 활용하여 projections를 사용.
	 * 
	 */
	@Test
	public void nativeQuery() {
		Team teamA = new Team("teamA");
		teamRepository.save(teamA);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		em.flush();
		em.clear();
		
		Page<MemberProjections> page = memberRepository.findByNativeQuery(PageRequest.of(0, 10));
		List<MemberProjections> projections = page.getContent();
		
		for (MemberProjections memberProjections : projections) {
			System.out.println("memberProjections id = " + memberProjections.getId());
			System.out.println("memberProjections usernmae = " + memberProjections.getUsername());
			System.out.println("memberProjections teamName = " + memberProjections.getTeamName());
		}
		
		
		
	}
}
