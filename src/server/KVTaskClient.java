package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String apiToken;
    private final URI url;
    private final HttpClient client;

    public KVTaskClient(String url) {
        client = HttpClient.newHttpClient();
        this.url = URI.create(url);
        this.apiToken = generateApiToken(url);
    }

    private String generateApiToken(String url) {
        try {
            URI requestUri = URI.create(url + "register/");
            HttpRequest request =  HttpRequest.newBuilder().uri(requestUri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время получения API ключа произошла ошибка.");
        }
        return "";
    }

    public void put(String key, String json) {
        try {
            URI requestUri = URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken);

            HttpRequest request =  HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(requestUri)
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время запроса произошла ошибка. ");
            e.printStackTrace();
        }
    }

    public String load(String key) {
        try {
            URI requestUri = URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken);

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(requestUri)
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        } catch (IOException | InterruptedException e) {
            return "";
        }
    }
}
