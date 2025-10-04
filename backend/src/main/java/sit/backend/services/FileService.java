package sit.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sit.backend.configurations.FileStorageProperties;
import sit.backend.dtos.SaleItemImageDto;
import sit.backend.entities.SaleItem;
import sit.backend.entities.SaleItemImage;
import sit.backend.repositories.SaleItemImageRepository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    private final Path fileStorageLocation;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SaleItemImageService saleItemImageService;
    @Autowired
    private SaleItemImageRepository saleItemImageRepository;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            if (!Files.exists(this.fileStorageLocation)) {
                Files.createDirectories(this.fileStorageLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create directory storage location: ", e);

        }
    }

    public String saveFile(MultipartFile image, SaleItem product) {
        String fileName = StringUtils.cleanPath(image.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Cannot upload file outside current directory: " + fileName);
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            SaleItemImageDto saleItemImageDto = new SaleItemImageDto();
            saleItemImageDto.setFileName(fileName);
            String[] parts = fileName.split("\\.");

            if (parts.length >= 2) {
                try {
                    int orderNo = Integer.parseInt(parts[parts.length - 2]);
                    saleItemImageDto.setImageViewOrder(orderNo);
                } catch (NumberFormatException e) {
                    saleItemImageDto.setImageViewOrder(null);
                }
            } else {
                saleItemImageDto.setImageViewOrder(null);
            }
            saleItemImageDto.setSaleItem(product);
            saleItemImageRepository.save(modelMapper.map(saleItemImageDto, SaleItemImage.class));
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Unable to save file: " + fileName, e);
        }

    }

    public List<String> saveFile(List<MultipartFile> images, SaleItem product) {
        List<String> fileNames = new ArrayList<>(images.size());
        images.forEach(image -> fileNames.add(saveFile(image, product)));
        return fileNames;
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File operation error: " + fileName, ex);
        }
    }

    public Resource loadFileAsResourceCard(String fileName) {
        try {
            Path filePath = Paths.get("uploads/cards/").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File operation error: " + fileName, ex);
        }
    }

    public void removeFile(SaleItem saleItem, String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                saleItemImageRepository.deleteByFileNameAndSaleItemId(fileName, saleItem.getId());
            } else {
                throw new ResourceNotFoundException("File not found " + fileName);
            }
        } catch (IOException ex) {
            throw new RuntimeException("File operation (DELETE) error: " + fileName, ex);
        }
    }


    public void removeFiles(SaleItem saleItem, List<String> fileNames) {
        List<String> failedFiles = new ArrayList<>();

        for (String fileName : fileNames) {
            try {
                removeFile(saleItem, fileName);
            } catch (Exception ex) {
                failedFiles.add(fileName);
            }
        }

        if (!failedFiles.isEmpty()) {
            throw new RuntimeException("Failed to delete files: " + String.join(", ", failedFiles));
        }
    }

    public void removeFileNormal(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (IOException ex) {
            throw new RuntimeException("File operation error: " + fileName, ex);
        }
    }


    public void renameFile(String oldFileName, String newFileName) {
        try {
            Path oldFilePath = this.fileStorageLocation.resolve(oldFileName).normalize();
            Path newFilePath = this.fileStorageLocation.resolve(newFileName).normalize();
            if (Files.exists(oldFilePath)) {
                Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new RuntimeException("File not found " + oldFileName);
            }
        } catch (IOException ex) {
            throw new RuntimeException("File operation (RENAME) error: " + oldFileName + " to " + newFileName, ex);
        }
    }

    public boolean fileExists(SaleItem saleItem, String fileName) {
        // สมมติว่าไฟล์อยู่ในโฟลเดอร์ picture/{product.id}/
        File file = new java.io.File("picture/" + saleItem.getId() + "/" + fileName);
        return file.exists();
    }

    public void deleteFile(String fileName) {
    }
}
