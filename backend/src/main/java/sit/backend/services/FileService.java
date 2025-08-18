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
}