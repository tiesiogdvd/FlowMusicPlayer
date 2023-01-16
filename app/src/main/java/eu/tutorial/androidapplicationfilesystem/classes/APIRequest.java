package eu.tutorial.androidapplicationfilesystem.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class APIRequest {
    private static final String API_KEY = "";
    private static final String API_BASE_URL = "https://ws.audioscrobbler.com/2.0/";

// ...

    public void searchByFileName(String fileName) {
        try {
            // Encode the file name in case it contains special characters
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8");

            // Construct the API URL
            String apiUrl = API_BASE_URL + "?method=track.search&track=" + encodedFileName + "&api_key=" + API_KEY + "&format=json";

            // Create a new URL object
            URL url = new URL(apiUrl);
            System.out.println(url);
            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the input stream from the connection
            InputStream inputStream = connection.getInputStream();
            System.out.println("GETTING INPUT STREAM");
            // Read the response from the input stream
            String response = readStream(inputStream);
            System.out.println("READING STREAM");
            // Parse the response and extract the metadata
            parseResponse(response);
            System.out.println("PARSING RESPONSE");
        } catch (IOException e) {
            // Handle exceptions
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        // Read the response from the input stream and return it as a string
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private void parseResponse(String response) {
        try {
            // Parse the JSON response
            JSONObject json = new JSONObject(response);

            // Get the "trackmatches" object
            JSONObject trackMatches = json.getJSONObject("trackmatches");

            // Get the "track" array
            JSONArray tracks = trackMatches.getJSONArray("track");

            // Get the first track in the array
            JSONObject track = tracks.getJSONObject(0);

            // Extract the metadata
            String name = track.getString("name");
            String artist = track.getString("artist");
            String url = track.getString("url");

            // Get the image array
            JSONArray images = track.getJSONArray("image");

            // Get the URL of the image with size "extralarge"
            String imageUrl = null;
            for (int i = 0; i < images.length(); i++) {
                JSONObject image = images.getJSONObject(i);
                if (image.getString("size").equals("extralarge")) {
                    imageUrl = image.getString("#text");
                    break;
                }
            }

            // Print the metadata to the console for debugging purposes
            System.out.println("Name: " + name);
            System.out.println("Artist: " + artist);
            System.out.println("URL: " + url);
            System.out.println("Image URL: " + imageUrl);
        } catch (JSONException e) {
            // Handle exceptions
        }
    }

    private void displayMetadata(String artist, String title, String album) {
    }

}
