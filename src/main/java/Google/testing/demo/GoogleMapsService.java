package Google.testing.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleMapsService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Valida si una dirección o punto de interés se encuentra dentro de Mar del Plata.
     *
     * @param direccion Texto de dirección o nombre de lugar.
     * @return true si está dentro de Mar del Plata con condiciones válidas.
     */
    public boolean validarDireccionEnMarDelPlata(String direccion) {
        boolean tieneNumero = contieneNumero(direccion);

        // Construir URL base para la consulta
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address={address},+Mar+del+Plata&key={key}";

        // Si NO contiene número, limitamos a resultados tipo "street_address" (evita resultados vagos)
        if (!tieneNumero) {
            url += "&result_type=street_address";
        }

        // Parámetros para la llamada
        Map<String, String> params = new HashMap<>();
        params.put("address", direccion);
        params.put("key", apiKey);

        // Llamada al servicio de Google
        Map<String, Object> response = restTemplate.getForObject(url, Map.class, params);
        if (response == null || !"OK".equals(response.get("status"))) return false;

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        if (results == null || results.isEmpty()) return false;

        Map<String, Object> result = results.get(0);

        // ❌ Rechazar coincidencias parciales (por ejemplo, solo un barrio)
        if (Boolean.TRUE.equals(result.get("partial_match"))) {
            return false;
        }

        // ❌ Rechazar si no está explícitamente en Mar del Plata
        List<Map<String, Object>> addressComponents = (List<Map<String, Object>>) result.get("address_components");
        boolean esMarDelPlata = addressComponents.stream().anyMatch(comp -> {
            List<String> types = (List<String>) comp.get("types");
            String longName = (String) comp.get("long_name");
            return types.contains("locality") && "Mar del Plata".equalsIgnoreCase(longName);
        });

        if (!esMarDelPlata) return false;

        // ❌ Rechazar ubicación si se requiere alta precisión (por tener altura) y no la tiene
        Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
        String locationType = (String) geometry.get("location_type");

        if (tieneNumero && (
                locationType == null ||
                        locationType.equals("APPROXIMATE") ||
                        locationType.equals("GEOMETRIC_CENTER"))) {
            return false;
        }

        // ✅ Verificar que las coordenadas estén dentro de Mar del Plata
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");
        double lat = ((Number) location.get("lat")).doubleValue();
        double lng = ((Number) location.get("lng")).doubleValue();

        // Rango geográfico aproximado de Mar del Plata
        double latMin = -38.100;
        double latMax = -37.850;
        double lngMin = -57.700;
        double lngMax = -57.450;

        return lat >= latMin && lat <= latMax && lng >= lngMin && lng <= lngMax;
    }

    /**
     * Devuelve true si el texto contiene al menos un número.
     */
    private boolean contieneNumero(String texto) {
        return texto.matches(".*\\d+.*");
    }
}


