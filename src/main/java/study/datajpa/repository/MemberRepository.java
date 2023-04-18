package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{

	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
 	
	/**
	 * @Query 생략가능
	 * 조건 - 메소드명과 일치하는 Named 쿼리를 찾아서 쿼리를 실행해 준다.
	 */ 
//	@Query(name = "Member.findByUsername")
	List<Member> findByUsername(@Param("username") String username);
	
	// 애플리케이션 로딩 시점에 해당 쿼리를 파싱해서 문법 오류가 있는경우 오류를 알려주고, 애플리케이션 로딩 되지 않음.
	@Query("select m from Member m where m.username = :username and m.age = :age")
	List<Member> findUser(@Param("username") String username, @Param("age") int age);
	
	@Query("select m.username from Member m")
	List<String> findUsernameList();
	
	@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
	List<MemberDto> findMemberDto();
	
	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") Collection<String> names);
	
	/**
	 * 반환타입
	 * 1. 리스트
	 * 2. 단일객체
	 * 3. Optional 
	 */
	List<Member> findListByUsername(String username);
	Member findMemberByUsername(String username);
	Optional<Member> findOptionalByUsername(String username);
	
	Page<Member> findPageByAge(int age, Pageable pageable);
	
	Slice<Member> findSliceByAge(int age, Pageable pageable);
	
	List<Member> findListByAge(int age, Pageable pageable);
	
	/**
	 * @Query 안에 여러 파라미터를 넣을 경우 기본 query는 value 프로퍼티 사용해야 된다.
	 * countQuery 페이징 시 전체 카운트 쿼리를 분리할 수 있다.
	 * 
	 * value애 작성한 쿼리의 결과 값이 null인 경우. countQuery 안나감  
	 * 
	 */  
	@Query(value = "select m from Member m right join fetch m.team t where m.age = :age", countQuery = "select count(m) from Member m")
	Page<Member> findCountQueryByAge(@Param("age") int age, Pageable pageable);
}
