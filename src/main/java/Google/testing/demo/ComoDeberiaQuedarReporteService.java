/*package Google.testing.demo;

public class ComoDeberiaQuedarReporteService {

    public Reporte addReporte(Reporte reporte){
        if(reporte == null){
            throw new IllegalArgumentException("El reporte no puede ser nulo");
        }

        if(reporte.getUsuario() == null){
            throw new IllegalArgumentException("El reporte debe tener un usuario asociado");
        }

        // Validar dirección con geocoding API
        if(reporte.getDireccion() == null || !googleMapsService.validarDireccionEnMarDelPlata(reporte.getDireccion())) {
            throw new IllegalArgumentException("La dirección ingresada no es válida o no se encuentra en Mar del Plata.");
        }

        try {
            // Validar que el usuario existe
            Usuario usuario = usuarioService.getUserById(reporte.getUsuario().getId());
            reporte.setUsuario(usuario); // Aseguramos que tenemos el usuario completo

            return reporteRepository.save(reporte);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Error al validar el usuario: " + e.getMessage());
        } catch (RuntimeException e){
            throw new RuntimeException("No se pudo agregar el reporte: " + e.getMessage());
        }
    }

}
*/