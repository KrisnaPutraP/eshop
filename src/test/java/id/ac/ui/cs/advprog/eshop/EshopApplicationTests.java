package id.ac.ui.cs.advprog.eshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class EshopApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main() {
        try (var mockedSpringApplication = mockStatic(SpringApplication.class)) {
            String[] args = new String[]{};
            EshopApplication.main(args);

            mockedSpringApplication.verify(
                    () -> SpringApplication.run(
                            eq(EshopApplication.class),
                            any(String[].class)
                    )
            );
        }
    }
}