import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ApiClient {

    private HttpClient client;
    private String last;

    public ApiClient() {
        client = HttpClient.newHttpClient();
    }

    public int get(String url, String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        last = response.body();
        return response.statusCode();
    }

    public int uploadSkin(String token, String skinPath, String variant) throws Exception {
        byte[] fileBytes = Files.readAllBytes(Path.of(skinPath));
        String boundary = UUID.randomUUID().toString();

        String partVariant = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"variant\"\r\n\r\n"
                + variant + "\r\n";

        String partFileHeader = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"skin.png\"\r\n"
                + "Content-Type: image/png\r\n\r\n";

        String ending = "\r\n--" + boundary + "--\r\n";

        byte[] a = partVariant.getBytes();
        byte[] b = partFileHeader.getBytes();
        byte[] c = ending.getBytes();
        byte[] body = new byte[a.length + b.length + fileBytes.length + c.length];
        int pos = 0;
        System.arraycopy(a, 0, body, pos, a.length); pos += a.length;
        System.arraycopy(b, 0, body, pos, b.length); pos += b.length;
        System.arraycopy(fileBytes, 0, body, pos, fileBytes.length); pos += fileBytes.length;
        System.arraycopy(c, 0, body, pos, c.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.minecraftservices.com/minecraft/profile/skins"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        last = response.body();
        return response.statusCode();
    }

    public void printLast() {
        System.out.println(last);
    }
}
