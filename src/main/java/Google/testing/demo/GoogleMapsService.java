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

    public boolean validarDireccionEnMarDelPlata(String direccion) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address={address},+Mar+del+Plata&key={key}";

        Map<String, String> params = new HashMap<>();
        params.put("address", direccion);
        params.put("key", apiKey);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class, params);
        if (response == null || !"OK".equals(response.get("status"))) return false;

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        if (results == null || results.isEmpty()) return false;

        Map<String, Object> result = results.get(0);
        String formattedAddress = (String) result.get("formatted_address");

        // Normalizar para comparar sin espacios ni mayúsculas
        String direccionNormalizada = direccion.toLowerCase().replaceAll("\\s+", "");
        String formattedNormalizada = formattedAddress.toLowerCase().replaceAll("\\s+", "");

        // Verifica que aparezca la calle+altura exacta en la dirección devuelta
        if (!formattedNormalizada.contains(direccionNormalizada)) {
            return false;
        }

        Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");

        double lat = ((Number) location.get("lat")).doubleValue();
        double lng = ((Number) location.get("lng")).doubleValue();

        // Coordenadas aproximadas de Mar del Plata
        double latMin = -38.100;
        double latMax = -37.850;
        double lngMin = -57.700;
        double lngMax = -57.450;

        return lat >= latMin && lat <= latMax && lng >= lngMin && lng <= lngMax;
    }
}
