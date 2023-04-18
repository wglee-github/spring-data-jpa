package study.datajpa.repository;

import java.util.List;

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
}
