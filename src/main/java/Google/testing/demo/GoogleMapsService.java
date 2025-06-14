package Google.testing.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleMapsService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean validarDireccionEnMarDelPlata(String direccion) {
        // Usamos la dirección "cruda", sin agregar ", Mar del Plata"
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address={address}&region=ar&key={key}";

        Map<String, String> params = new HashMap<>();
        params.put("address", direccion);
        params.put("key", apiKey);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class, params);
        if (response == null || !"OK".equals(response.get("status"))) return false;

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        if (results == null || results.isEmpty()) return false;

        Map<String, Object> result = results.get(0);
        String formattedAddress = (String) result.get("formatted_address");

        // Normalizar ambas direcciones para hacer comparación sin tildes ni espacios
        String direccionNormalizada = normalizarTexto(direccion);
        String formattedNormalizada = normalizarTexto(formattedAddress);

        if (!formattedNormalizada.contains(direccionNormalizada)) {
            return false;
        }

        Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");

        double lat = ((Number) location.get("lat")).doubleValue();
        double lng = ((Number) location.get("lng")).doubleValue();

        // Rango aproximado de coordenadas de Mar del Plata
        double latMin = -38.100;
        double latMax = -37.850;
        double lngMin = -57.700;
        double lngMax = -57.450;

        return lat >= latMin && lat <= latMax && lng >= lngMin && lng <= lngMax;
    }

    private String normalizarTexto(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")  // quita tildes
                .toLowerCase()
                .replaceAll("\\s+", "");          // elimina espacios
    }
}
