package study.datajpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 *  @EnableJpaRepositories 
 	· JPA Repository들을 활성화하기 위한 애노테이션입니다.
 *  · basePackages 프로퍼티를 통해 패키지 경로를 지정하면 해당 패키지를 스캔하여 JpaRepository 상속받은 인터페이스(MemberRepository)를 찾아 빈에 등록해 줍니다.
 *  · MemberRepository 를 @Autowired 한 곳에서는 스프링 데이터 JPA가 MemberRepository에다가 JpaRepository 의 구현체를 주입해 줍니다.
 *  · 따라서 우리가 보는 소스코드상에는 MemberRepository의 구현체가 보이지 않아도 사용할 수 있는 이유입니다.
 *  
 *  · springboot 에서는 해당 어노테이션을 하지 않아도 내부적으로 @EnableJpaRepositories 가 자동 설정 됩니다.
 *  · 또한 @SpringBootApplication 선언된 패키지 하위의 모든 패키지를 알아서 스캔합니다.
 */
//@EnableJpaRepositories(basePackages = "패키지경로")
@EnableJpaAuditing
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	/**
	 * @CreatedBy @LastModifiedBy 를 선언한 필드에 아래 메소드의 리턴값을 넣어준다.
	 * 메소드명은 아무거나 적어도 됨 
	 */
	@Bean
	public AuditorAware<String> auditorProvider(){
		return () -> Optional.of(UUID.randomUUID().toString());
	}
	
}
