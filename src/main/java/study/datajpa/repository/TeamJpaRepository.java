package study.datajpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import study.datajpa.entity.Team;

@Repository
public class TeamJpaRepository {

	@PersistenceContext
	private EntityManager em;
	
	public Team save(Team team) {
		em.persist(team);
		return team;
	}
	
	public void delete(Team team) {
		em.remove(team);
	}
	
	public List<Team> findAll(Long id) {
		 return em.createQuery("select t from Team t", Team.class)
				 .getResultList();
	}
	
	public Optional<Team> findById(Long id) {
		Team findTeam = em.find(Team.class, id);
		return Optional.ofNullable(findTeam);
	}
	
	public long count() {
		return em.createQuery("select count(t) from Team t", Long.class)
				.getSingleResult();
	}
}
