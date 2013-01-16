import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class ArtistVis {

    /**
     * @param args
     */
    public static void main(String[] args) {

        // generateArtistList();

        // generateTagData(parseArtistList());

        HashMap<String, ArrayList<String>> artistToTagMap = parseArtistTagData();
        System.out.println(artistToTagMap);
        System.out.println(artistToTagMap.keySet().size());

    }

    private static HashMap<String, ArrayList<String>> parseArtistTagData() {

        HashMap<String, ArrayList<String>> output = new HashMap<String, ArrayList<String>>();

        File tagFolder = new File("tagData");
        File[] listOfFiles = tagFolder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {

            File file = listOfFiles[i];

            if (file.isFile()) {
                if (file.getName().endsWith(".txt")) {
                    String artistName = file.getName().replace(".txt", "");

                    try {

                        BufferedReader reader = new BufferedReader(
                                new FileReader(file));

                        String listText = reader.readLine();

                        Gson gson = new Gson();

                        Type collectionType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        ArrayList<String> taglist = gson.fromJson(listText,
                                collectionType);

                        ArrayList<String> truncatedList = new ArrayList<String>();

                        if (taglist != null) {
                            if (taglist.size() > 10) {
                                truncatedList = new ArrayList<String>(
                                        taglist.subList(0, 9));
                            } else {
                                truncatedList = taglist;
                            }

                            output.put(artistName, truncatedList);
                        }


                        reader.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        return output;
    }

    private HashMap<String, Integer> getTagDictionary(
            HashMap<String, ArrayList<String>> tagData) {

        HashMap<String, Integer> output = new HashMap<String, Integer>();

        return output;
    }

    private static void generateArtistList() {

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

    private static ArrayList<String> parseArtistList() {

        File artistList = new File("artists.txt");
        BufferedReader reader;
        ArrayList<String> output = new ArrayList<String>();

        try {

            reader = new BufferedReader(new FileReader(artistList));

            String listText = reader.readLine();

            Gson gson = new Gson();

            Type collectionType = new TypeToken<ArrayList<String>>() {
            }.getType();
            output = gson.fromJson(listText, collectionType);

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;

    }

    private static void generateTagData(ArrayList<String> artists) {
        for (String artist : artists) {
            saveTagData(artist);
        }
    }

    private static void saveTagData(String artistName) {

        File artistTags = new File("tagData/" + sanitizeAscii(artistName)
                + ".txt");

        if (!artistTags.exists()) {


            BufferedWriter writer;

            try {

                writer = new BufferedWriter(new FileWriter(artistTags));

                Gson gson = new Gson();

                writer.write(gson.toJson(getTags(artistName)));

                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
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

        JsonArray artists = root.getAsJsonObject("topartists").getAsJsonArray(
                "artist");

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

    private static String sanitizeAscii(String input) {

        for (int i = 0; i < input.length(); i++) {

            int c = input.charAt(i);
            if (c > 0x7F || (c >= 58 && c <= 64)) {
                input.replace(input.charAt(i), '_');
            }
        }

        return input;
    }

}
