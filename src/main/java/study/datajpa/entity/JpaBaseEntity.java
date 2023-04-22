package study.datajpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@Getter
@MappedSuperclass
public class JpaBaseEntity {

	@Column(updatable = false)
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	
	/**
	 * JPA 주요 이벤트 어노테이션
		@PrePersist, @PostPersist
		@PreUpdate, @PostUpdate
	 */
	@PrePersist
	public void prePersist() {
		createDate = LocalDateTime.now();
	}
	
	@PreUpdate
	public void preUpdate() {
		updateDate = LocalDateTime.now();
	}
}
