package com.example.a3dmodel;

import java.io.*;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Client {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final MediaType IMAGE = MediaType.get("image/png");

    private static void initializationHTTPPOSTRequest(OkHttpClient client, String url, String token, long numberOfFiles) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", token);
        jsonObject.put("count", numberOfFiles); // size of dir or number of selected
        try {
            FileWriter file = new FileWriter("test/ini.json");
            file.write(jsonObject.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(data, JSON);
        Request requestIni = new Request.Builder()
                .url(HttpUrl.get(url + token + ".json"))
                .post(body)
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(requestIni).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Send initialization: Ok");
            }
        }

    }

    private static void sendFileHTTPPOSTRequest(OkHttpClient client, String url, String token, String filePath) throws IOException {
        RequestBody body = RequestBody.create(data, IMAGE);
        Request requestIni = new Request.Builder()
                .url(HttpUrl.get(url + token + "/images/" + Paths.get(filePath).getFileName().toString()))
                .post(body)
                .header("Content-Type", "image/png")
                .build();
        try (Response response = client.newCall(requestIni).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Send initialization: Ok");
            }
        }
    }

    private static void getFileHTTPGETRequest(OkHttpClient client, String url, String token, File result) throws IOException, InterruptedException {
        Request requestGet = new Request.Builder()
                .url(HttpUrl.get(url + "resources/" + token + "/reconstruction_sequential/PMVS/models/pmvs_options.txt.ply"))
                .build();
        try (Response response = client.newCall(requestGet).execute()) {
            if (response.isSuccessful()) {
                FileOutputStream stream = new FileOutputStream(result);
                try {
                    stream.write(response.body().byteStream().read());
                } finally {
                    stream.close();
                }
            }
        }
    }

    public static void HttpClientRequest(List<String> files) throws IOException, InterruptedException, JSONException {
        String token = UUID.randomUUID().toString();
        String url = "http://localhost:8000/";
        System.out.println(token);
        OkHttpClient client = new OkHttpClient.Builder().build();
        initializationHTTPPOSTRequest(client, url, token, files.size());
        for (String file : files) {
            sendFileHTTPPOSTRequest(client, url, token, file);
        }

        getFileHTTPGETRequest(client, url, token, "test/pmvs_options.txt.ply");
    }
}
