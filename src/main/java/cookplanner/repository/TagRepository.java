package cookplanner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cookplanner.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findByName(String string);

}
