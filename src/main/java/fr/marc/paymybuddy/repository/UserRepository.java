package fr.marc.paymybuddy.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fr.marc.paymybuddy.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

}
