package Google.testing.demo;

public class ComoDeberiaQuedarReporteController {

    // Agregar googleMapsService al contructor
    @Autowired
    public ReporteController(ReporteService reporteService, com.reportalo.tpFinal.service.GoogleMapsService googleMapsService) {
        this.reporteService = reporteService;
        this.googleMapsService = googleMapsService;
    }

    // Agregar Get para valir direccion
    @GetMapping("/validar-direccion")
    public ResponseEntity<String> validarDireccionEnMarDelPlata(@RequestParam String direccion) {
        boolean esValida = googleMapsService.validarDireccionEnMarDelPlata(direccion);

        if (esValida) {
            return ResponseEntity.ok("La dirección está dentro de Mar del Plata.");
        } else {
            return ResponseEntity.badRequest().body("La dirección no pertenece a Mar del Plata.");
        }
    }

}
