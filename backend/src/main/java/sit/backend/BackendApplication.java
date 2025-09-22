package sit.backend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import sit.backend.ListMapper;
import sit.backend.configurations.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)

public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }



    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

     @Bean
    public ListMapper listMapper() {
        return ListMapper.getInstance();
    }
}
