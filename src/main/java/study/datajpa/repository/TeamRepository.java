package study.datajpa.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TeamRepository {

	@PersistenceContext
	private EntityManager em;
	
	
}
