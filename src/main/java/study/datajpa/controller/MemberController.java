package study.datajpa.controller;

import java.util.Iterator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberRepository memberRepository;
	
	@GetMapping("/member/{id}")
	public String findMember(@PathVariable("id") Long id) {
		Member findMember = memberRepository.findById(id).get();
		return findMember.getUsername();
	}
	
	/**
	 * 
	 	* 도메인 클래스 컨버터
	 		· HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩
	 
			 · HTTP 요청은 회원 id 를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환한다.
			 · 도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾는다.

	 	* 주의
		 	· 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용해야 한다. 
			  (트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않는다.)
	 */
	@GetMapping("/member2/{id}")
	public String findMember2(@PathVariable("id") Member findMember) {
		return findMember.getUsername();
	}
	
	/**
	 * 
	 * 
	 	* 파라미터로 Pageable 을 받을 수 있다. 
	 		· Pageable 은 인터페이스, 실제는 org.springframework.data.domain.PageRequest 객체 생성
	 	 
	 	* 요청 파라미터
			· 예) /members?page=0&size=3&sort=id,desc&sort=username,desc
			· page: 현재 페이지, 0부터 시작한다.
			· size: 한 페이지에 노출할 데이터 건수
			· sort: 정렬 조건을 정의한다. 예) 정렬 속성,정렬 속성...(ASC | DESC), 정렬 방향을 변경하고 싶으면 sort
			· 파라미터 추가 ( asc 생략 가능)

		 * 기본값
			· 글로벌 설정: 스프링 부트( 적용 안하면 기본값은 아래처럼 셋팅된다 )
				· spring.data.web.pageable.default-page-size=20 /# 기본 페이지 사이즈/
				· spring.data.web.pageable.max-page-size=2000 /# 최대 페이지 사이즈/
		
			· 개별 설정은 
				· @PageableDefault 어노테이션을 사용

	 */
	@GetMapping("/members")
	public Page<Member> list(@PageableDefault(size = 5) Pageable pageable){
		return memberRepository.findAll(pageable);
	}
	
	/**
	 * Member -> MemberDTO 객체로 변환하여 리턴
	 */
	@GetMapping("/membersDto")
	public Page<MemberDto> listDto(@PageableDefault(size = 5) Pageable pageable){
		Page<Member> page = memberRepository.findAll(pageable);
		// 1. MemberDto에 파라미터를 각각 직접 넘김
//		return page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
		
		// 2. MemberDto에 member를 파라미터로 넘김
//		return page.map(member -> new MemberDto(member));
		// 3. 2번을 아래처럼 가능
		return page.map(MemberDto::new);
	}
	
	/**
	 * Spring은 bean을 초기화 한 이후에 @PostConstruct을 한번만 호출한다. 
	 * 즉 @PostConstruct는 WAS 가 뜰 때 bean이 생성된 다음 딱 한번만 실행된다. 
	 * 따라서 @PostConstruct 를 사용하여 기본 사용자라던가, 딱 한번만 등록하면 되는 key 값 등을 등록하여 사용할 수 있다.
	 */
	@PostConstruct
	public void init() {
		for (int i = 1; i < 101; i++) {
//			memberRepository.save(new Member("member"+i));
		}
	}
}
