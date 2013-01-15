import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ArtistVis {

    /**
     * @param args
     */
    public static void main(String[] args) {

        generateArtistList();

    }

    private static void generateArtistList(){

        File artistList = new File("artists.txt");
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(artistList));

            ArrayList<String> topArtists = getTopArtists("dalbz");
            HashSet<String> allArtists = new HashSet<String>();

            allArtists.addAll(topArtists);

            int i = 1;
            for (String artist : topArtists) {
                allArtists.addAll(getSimilarArtists(artist));
                System.out.println(i);
                i++;
            }

            Gson gson = new Gson();

            writer.write(gson.toJson(allArtists));

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static String getHttpRequest(String urlText) {

        pause();

        String output = "";

        try {
            URL url = new URL(urlText);
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            String input;
            while ((input = in.readLine()) != null) {
                output += input;
            }
            in.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return output;

    }

    private static ArrayList<String> getSimilarArtists(String artistName) {

        artistName = artistName.replace(" ", "+");

        ArrayList<String> similarArtists = new ArrayList<String>();

        String response = getHttpRequest("http://ws.audioscrobbler.com/2.0/?method=artist.getsimilar&artist="
                + artistName
                + "&limit=100&api_key=56b26f4bbdd972b4460ca2ab0b53e63a&format=json");

        JsonParser parser = new JsonParser();

        JsonObject root = parser.parse(response).getAsJsonObject();

        JsonArray artists = root.getAsJsonObject("similarartists")
                .getAsJsonArray("artist");

        for (int i = 0; i < artists.size(); i++) {
            JsonObject artist = artists.get(i).getAsJsonObject();
            similarArtists.add(artist.get("name").getAsString());
        }

        return similarArtists;

    }

    private static ArrayList<String> getTopArtists(String userName) {

        ArrayList<String> topArtists = new ArrayList<String>();

        String response = getHttpRequest("http://ws.audioscrobbler.com/2.0/?method=user.gettopartists&user="
                + userName
                + "&limit=50&period=12month&api_key=56b26f4bbdd972b4460ca2ab0b53e63a&format=json");

        JsonParser parser = new JsonParser();

        JsonObject root = parser.parse(response).getAsJsonObject();

        JsonArray artists = root.getAsJsonObject("topartists")
                .getAsJsonArray("artist");

        for (int i = 0; i < artists.size(); i++) {
            JsonObject artist = artists.get(i).getAsJsonObject();
            topArtists.add(artist.get("name").getAsString());
        }

        return topArtists;

    }

    private static ArrayList<String> getTags(String artistName) {

        artistName = artistName.replace(" ", "+");

        ArrayList<String> tagList = new ArrayList<String>();

        String response = getHttpRequest("http://ws.audioscrobbler.com/2.0/?method=artist.gettoptags&artist="
                + artistName
                + "&api_key=56b26f4bbdd972b4460ca2ab0b53e63a&format=json");

        JsonParser parser = new JsonParser();

        JsonObject root = parser.parse(response).getAsJsonObject();

        JsonArray tags = root.getAsJsonObject("toptags").getAsJsonArray("tag");

        if (tags == null) {
            return tagList;
        }

        for (int i = 0; i < tags.size(); i++) {
            JsonObject tag = tags.get(i).getAsJsonObject();
            tagList.add(tag.get("name").getAsString().toLowerCase());
        }

        return tagList;

    }

    private static void pause() {
        try {
            Thread.sleep(210);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
