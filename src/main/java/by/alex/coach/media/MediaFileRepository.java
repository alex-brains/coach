package by.alex.coach.media;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    // Все медиафайлы прикреплённые к конкретному элементу
    // Например: все картинки к вопросу с id=5
    List<MediaFile> findByItemTypeAndItemId(String itemType, Long itemId);

    // Удалить все медиафайлы элемента (при удалении вопроса)
    void deleteByItemTypeAndItemId(String itemType, Long itemId);
}
