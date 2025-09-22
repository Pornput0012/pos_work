package sit.backend.controllers;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.backend.services.FileService;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/sale-items")
public class SaleItemImageController {
    private final FileService fileService;

    public SaleItemImageController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Resource resource = fileService.loadFileAsResource(fileName);

        String contentType;
        try {
            Path filePath = resource.getFile().toPath();
            contentType = Files.probeContentType(filePath); // ตรวจ MIME type
        } catch (Exception e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"") // inline = แสดงใน browser
                .body(resource);
    }
}
