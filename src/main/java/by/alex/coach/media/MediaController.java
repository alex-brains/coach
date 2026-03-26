package by.alex.coach.media;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    /**
     * Загрузить файл.
     *
     * POST /api/media/upload
     * multipart/form-data:
     *   file     — файл
     *   itemType — QUESTION | LANGUAGE_ITEM (опционально)
     *   itemId   — id элемента (опционально)
     *
     * Ответ: { "id": 1, "url": "/api/media/1", "filename": "abc.png" }
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "itemType", required = false) String itemType,
            @RequestParam(value = "itemId", required = false) Long itemId
    ) {
        MediaFile saved = mediaService.save(file, itemType, itemId);

        return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "url", "/api/media/" + saved.getId(),
                "filename", saved.getOriginalName(),
                "contentType", saved.getContentType(),
                "size", saved.getSize()
        ));
    }

    /**
     * Отдать файл по id.
     * GET /api/media/{id}
     *
     * Браузер откроет картинку напрямую (inline).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable Long id) {
        MediaFile mediaFile = mediaService.getById(id);
        Path filePath = mediaService.getFilePath(id);

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new MediaNotFoundException("Файл не найден: " + id);
        }

        if (!resource.exists() || !resource.isReadable()) {
            throw new MediaNotFoundException("Файл не доступен: " + id);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mediaFile.getContentType()))
                // inline = браузер показывает картинку, attachment = скачивает
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + mediaFile.getOriginalName() + "\"")
                .body(resource);
    }

    /**
     * Все файлы прикреплённые к элементу.
     * GET /api/media?itemType=QUESTION&itemId=5
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getByItem(
            @RequestParam String itemType,
            @RequestParam Long itemId
    ) {
        List<Map<String, Object>> result = mediaService.getByItem(itemType, itemId)
                .stream()
                .map(f -> Map.<String, Object>of(
                        "id", f.getId(),
                        "url", "/api/media/" + f.getId(),
                        "filename", f.getOriginalName(),
                        "contentType", f.getContentType(),
                        "size", f.getSize()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    /**
     * Удалить файл.
     * DELETE /api/media/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mediaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
