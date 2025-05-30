package Google.testing.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeocodingTestApp implements CommandLineRunner {

    @Autowired
    private GoogleMapsService googleMapsService;

    public static void main(String[] args) {
        SpringApplication.run(GeocodingTestApp.class, args);
    }

    @Override
    public void run(String... args) {
        String direccion = "11 de Septiembre 2796";
        boolean valida = googleMapsService.validarDireccionEnMarDelPlata(direccion);
        System.out.println("¿Es válida en Mar del Plata? " + valida);
    }

    // En la version final el controller se encarga de enviar la direccion
}
