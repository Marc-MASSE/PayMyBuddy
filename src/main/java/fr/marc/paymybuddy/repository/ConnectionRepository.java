package fr.marc.paymybuddy.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.User;

@Repository
public interface ConnectionRepository extends CrudRepository<Connection, Integer> {
	
	public List<Connection> findAllByUser(User user);
	
	public Connection findByUserIdAndBuddyId(Integer userId, Integer buddyId);
	
	public void deleteByUserIdAndBuddyId(Integer userId, Integer buddyId);

}
