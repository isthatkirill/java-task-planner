package tracker.server;

import tracker.exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    final private URI url;
    private String apiToken;
    HttpClient httpClient;

    public KVTaskClient(String path) {
        this.url = URI.create(path);
        httpClient = HttpClient.newHttpClient();
    }

    public void register() {
        URI uri = URI.create(this.url + "/register");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                apiToken = response.body();
                System.out.println(apiToken);
            } else {
                apiToken = null;
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Can't do register: ", e);
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(this.url + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response code: " + response.statusCode());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException(String.valueOf(response.statusCode()));
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Can't do save request" , e);
        }
    }

    public String load(String key) {
        URI uri = URI.create(this.url + "/load/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int sc = response.statusCode();
            if (response.statusCode() != 200 && response.statusCode() != 204) {
                throw new ManagerSaveException(String.valueOf(response.statusCode()));
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Can't do save request", e);
        }
    }
}
