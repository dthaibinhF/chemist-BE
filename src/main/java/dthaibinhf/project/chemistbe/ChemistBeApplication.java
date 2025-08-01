package dthaibinhf.project.chemistbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableScheduling
public class ChemistBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChemistBeApplication.class, args);
    }
}
