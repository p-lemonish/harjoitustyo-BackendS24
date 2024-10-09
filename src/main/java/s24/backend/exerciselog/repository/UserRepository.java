package s24.backend.exerciselog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import s24.backend.exerciselog.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
