package study.datajpa.entity;

import org.springframework.data.jpa.repository.EntityGraph;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
// @NamedQuery 장점 - 애플리케이션 로딩 시점에 해당 쿼리를 한번 파싱해 본다. 따라서 문법 오류가 있는경우 오류를 알려주고, 애플리케이션 로딩 되지 않음.
@NamedQuery(name = "Member.findByUsername", query = "select m from Member m where m.username = :username")
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team")) // Member 조회 시 team은 무조건 조회하는 설정. 해당 name을 @EntityGraph 에서 호출하여 사용 가능
public class Member {

	@Id @GeneratedValue
	@Column(name = "member_id")
	private Long id;
	private String username;
	private int age;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;
	
	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}
	
	/**
	 * jpa 표준스팩에 entity를 만들 때 default 생성자가 있어야 한다.
	 * 또한 생성자의 접근제한자가 최소 protected는 되어야 한다.
	 * 그래야 JPA 구현체에서 프록시 기술이나, 리플랙션 기술을 사용할 수 있도록 지원해야 하기 때문에 private으로 선언하면 안된다.  
	 * 
	 * class 위에 @NoArgsConstructor(access = AccessLevel.PROTECTED) 선언해도 된다. 
	 * 
	 */
//	protected Member() {
//	}
	
	
	
	public Member(String username) {
		this.username = username;
	}

	public Member(String username, int age) {
		this.username = username;
		this.age = age;
	}
	
	public Member(String username, int age, Team team) {
		this.username = username;
		this.age = age;
		if(team != null)
			changeTeam(team);
	}

	
}
