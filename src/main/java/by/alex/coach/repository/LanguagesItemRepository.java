package by.alex.coach.repository;

import by.alex.coach.models.LanguagesItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguagesItemRepository extends JpaRepository<LanguagesItem, Long> {
    List<LanguagesItem> findByLanguageAndType(String language, String type);
}
