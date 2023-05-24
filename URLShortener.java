import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class URLShortener {

    private static final String BASE_URL = "http://localhost:6269/";      /* The Base URL */
    private static final Map<String, String> longUrls = new HashMap<>();
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException {          
        System.out.println("Welcome to URL Shortener!");

        // Create the HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(6269), 0);
        server.createContext("/", new RedirectHandler());
        server.setExecutor(null); // Use the default executor
        server.start();

        // Get the long URL from the user.
        System.out.print("Please Enter the long URL: ");
        String longUrl = System.console().readLine();
        
        // Generate a short URL.
        String shortUrl = generateShortUrl();

        // Save the long URL to the map.
        longUrls.put(shortUrl, longUrl);
        System.out.println("URL Generate Sucessful--->");
        // Print the short URL to the console.
        System.out.println("Your short URL is: " + BASE_URL + shortUrl);
    }

    private static String generateShortUrl() {
        // Generate a random string of characters.
        String shortUrl = new String(new char[8]).replaceAll("[^a-zA-Z0-9]", "");

        // Check if the short URL already exists.
        while (longUrls.containsKey(shortUrl)) {
            shortUrl = generateShortUrl();
        }
        return shortUrl;           
    }

    static class RedirectHandler implements HttpHandler {                     
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String shortUrl = path.substring(1); // Remove the leading slash

            String longUrl = longUrls.get(shortUrl);
            if (longUrl != null) {
                exchange.getResponseHeaders().add("Location", longUrl);
                exchange.sendResponseHeaders(301, -1); // 301: Moved Permanently
            } else {
                exchange.sendResponseHeaders(404, -1); // 404: Not Found
            }

            OutputStream outputStream = exchange.getResponseBody();
            outputStream.close();
        }
    }
}
