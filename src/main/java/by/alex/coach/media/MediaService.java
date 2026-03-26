package by.alex.coach.media;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final MediaProperties properties;
    private Path uploadRoot;

    public MediaService(MediaFileRepository mediaFileRepository, MediaProperties properties) {
        this.mediaFileRepository = mediaFileRepository;
        this.properties = properties;
    }

    /**
     * Создаём папку uploads при старте приложения если её нет.
     */
    @PostConstruct
    public void init() {
        uploadRoot = Paths.get(properties.getUploadDir()).toAbsolutePath();
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось создать папку для загрузок: " + uploadRoot, e);
        }
    }

    /**
     * Сохранить файл на диск и создать запись в БД.
     *
     * @param file     загруженный файл
     * @param itemType к чему прикреплён: QUESTION | LANGUAGE_ITEM | null
     * @param itemId   id элемента | null
     */
    public MediaFile save(MultipartFile file, String itemType, Long itemId) {
        validateFile(file);

        String originalName = file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "file";

        String ext = extractExtension(originalName);
        String filename = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        Path destination = uploadRoot.resolve(filename);

        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка сохранения файла: " + filename, e);
        }

        MediaFile mediaFile = new MediaFile();
        mediaFile.setFilename(filename);
        mediaFile.setOriginalName(originalName);
        mediaFile.setContentType(file.getContentType());
        mediaFile.setSize(file.getSize());
        mediaFile.setItemType(itemType);
        mediaFile.setItemId(itemId);

        return mediaFileRepository.save(mediaFile);
    }

    /**
     * Получить физический путь к файлу (для отдачи через контроллер).
     */
    @Transactional(readOnly = true)
    public Path getFilePath(Long id) {
        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Файл не найден: " + id));
        return uploadRoot.resolve(mediaFile.getFilename());
    }

    /**
     * Получить запись о файле (метаданные).
     */
    @Transactional(readOnly = true)
    public MediaFile getById(Long id) {
        return mediaFileRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Файл не найден: " + id));
    }

    /**
     * Все файлы прикреплённые к элементу.
     */
    @Transactional(readOnly = true)
    public List<MediaFile> getByItem(String itemType, Long itemId) {
        return mediaFileRepository.findByItemTypeAndItemId(itemType, itemId);
    }

    /**
     * Удалить файл с диска и из БД.
     */
    public void delete(Long id) {
        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Файл не найден: " + id));

        Path filePath = uploadRoot.resolve(mediaFile.getFilename());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Логируем но не бросаем исключение — запись из БД всё равно удалим
            System.err.println("Не удалось удалить файл с диска: " + filePath);
        }

        mediaFileRepository.delete(mediaFile);
    }

    // ─── Вспомогательные методы ──────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        String contentType = file.getContentType();
        if (contentType == null || !properties.getAllowedTypes().contains(contentType)) {
            throw new IllegalArgumentException(
                    "Недопустимый тип файла: " + contentType +
                    ". Разрешены: " + properties.getAllowedTypes()
            );
        }

        // 10 MB максимум (дополнительная проверка на уровне сервиса)
        if (file.getSize() > 10L * 1024 * 1024) {
            throw new IllegalArgumentException("Файл слишком большой. Максимум 10MB");
        }
    }

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1).toLowerCase() : "";
    }
}
