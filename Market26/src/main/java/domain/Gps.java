package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Gps {
	 @Id
	 @GeneratedValue 
	 private Integer id;
	private double lat;
	private double lon;
	
	private static final double KOSTEA_KM = 0.05;
	private static final double BIDALKETA_MINIMOA = 3.00;
	private static final double BIDALKETA_MAXIMOA = 100.00;
    
	
	
	static class Koordenada {
        double lat;
        double lon;

        Koordenada(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
	}
	
	public Gps (String Kalea, String kodePostala, String herrialdea)  throws Exception {
		Koordenada k1 = geocodificar( Kalea,  kodePostala,  herrialdea);
		lat=k1.lat;
		lon=k1.lon;
	}
	public void eguneratuGps(String Kalea, String kodePostala, String herrialdea)throws Exception{
		Koordenada k1=geocodificar( Kalea,  kodePostala,  herrialdea);
		lat=k1.lat;
		lon=k1.lon;
	}	
	public static Koordenada geocodificar(String Kalea, String kodePostala, String herrialdea) throws Exception {
	    
	    if (intentarBusqueda("", "", herrialdea) == null) {
	        throw new IllegalArgumentException("PerfilaGUI.ErrorPais");
	    }

	    if (intentarBusqueda("", kodePostala, herrialdea) == null) {
	        throw new IllegalArgumentException("PerfilaGUI.ErrorCP");
	    }

	    Koordenada k = intentarBusqueda(Kalea, kodePostala, herrialdea);
	    if (k != null) return k;


	    String calleLimpia = Kalea.replaceAll("(?i)\\s*kalea|\\s*calle|\\s*avenida", "").trim();
	    k = intentarBusqueda(calleLimpia, kodePostala, herrialdea);
	    if (k != null) return k;

	    throw new IllegalArgumentException("PerfilaGUI.ErrorCalle");
	}
	private static Koordenada intentarBusqueda(String calle, String codigoPostal, String pais) throws Exception {
	    String url = "https://nominatim.openstreetmap.org/search?"
	            + "street=" + URLEncoder.encode(calle, StandardCharsets.UTF_8)
	            + "&postalcode=" + URLEncoder.encode(codigoPostal, StandardCharsets.UTF_8)
	            + "&country=" + URLEncoder.encode(pais, StandardCharsets.UTF_8)
	            + "&format=json&limit=1";

	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("User-Agent", "MiAplicacionJava/1.0")
	            .GET()
	            .build();

	    HttpResponse<String> response =
	            client.send(request, HttpResponse.BodyHandlers.ofString());

	    String body = response.body();

	    if (body == null || body.trim().equals("[]")) {
	        return null;
	    }

	    if (!body.contains("\"lat\":\"") || !body.contains("\"lon\":\"")) {
	        return null;
	    }

	    try {
	        String latStr = body.split("\"lat\":\"")[1].split("\"")[0];
	        String lonStr = body.split("\"lon\":\"")[1].split("\"")[0];
	        return new Koordenada(Double.parseDouble(latStr), Double.parseDouble(lonStr));
	    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
	        return null;  
	    }
	}

	public double haversine(Gps bestea) {
	    final double R = 6371.0; // km

	    double lat1 = Math.toRadians(this.lat);
	    double lon1 = Math.toRadians(this.lon);
	    double lat2 = Math.toRadians(bestea.lat);
	    double lon2 = Math.toRadians(bestea.lon);

	    double dLat = lat2 - lat1;
	    double dLon = lon2 - lon1;

	    double a = Math.pow(Math.sin(dLat / 2), 2)
	            + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);

	    double c = 2 * Math.asin(Math.sqrt(a));

	    return R * c; 
	}

	public double bidalketaGastuakKalkulatu(Gps bestea) {
	    double distantzia = this.haversine(bestea);
	    double costeCalculado = distantzia * KOSTEA_KM;

	    double costeFinal = Math.min(BIDALKETA_MAXIMOA,
	            Math.max(BIDALKETA_MINIMOA, costeCalculado));

	    return BigDecimal.valueOf(costeFinal)
	            .setScale(2, RoundingMode.HALF_UP)
	            .doubleValue();
	}
}