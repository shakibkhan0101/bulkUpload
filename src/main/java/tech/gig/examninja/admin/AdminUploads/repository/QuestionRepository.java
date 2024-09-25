package tech.gig.examninja.admin.AdminUploads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.gig.examninja.admin.AdminUploads.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
