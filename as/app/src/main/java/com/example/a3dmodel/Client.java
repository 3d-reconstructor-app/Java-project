package com.example.a3dmodel;

import java.io.*;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Client {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final MediaType IMAGE = MediaType.get("image/png");

    public static void HttpClientRequest(List<File> files, File result) throws AppException {
        String token = UUID.randomUUID().toString();
        String url = "http://localhost:8000/";
        System.out.println(token);
        OkHttpClient client = new OkHttpClient.Builder().build();
        initializationHTTPPOSTRequest(client, url, token, files.size());
        for (File file : files) {
            sendFileHTTPPOSTRequest(client, url, token, file);
        }

        getFileHTTPGETRequest(client, url, token, result);
    }

    private static void initializationHTTPPOSTRequest(OkHttpClient client, String url, String token, long numberOfFiles) throws AppException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", token);
            jsonObject.put("count", numberOfFiles);
        } catch (JSONException e) {
            throw new AppException("Can't make json to initialize reconstruction", e);
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request requestIni = new Request.Builder()
                .url(HttpUrl.get(url + token + ".json"))
                .post(body)
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(requestIni).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Send initialization: Ok");
            }
        } catch (IOException e) {
            throw new AppException("Can't make initialization request", e);
        }

    }

    private static void sendFileHTTPPOSTRequest(OkHttpClient client, String url, String token, File file) throws AppException {
        RequestBody body = RequestBody.create(file, IMAGE);
        Request requestIni = new Request.Builder()
                .url(HttpUrl.get(url + token + "/images/" + file.getName()))
                .post(body)
                .header("Content-Type", "image/png")
                .build();
        try (Response response = client.newCall(requestIni).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Send file " + file.getName() + " : Ok");
            }
        } catch (IOException e) {
            throw new AppException("Can't end file " + file.getName(), e);
        }
    }

    private static void getFileHTTPGETRequest(OkHttpClient client, String url, String token, File result) throws AppException {
        Request requestGet = new Request.Builder()
                .url(HttpUrl.get(url + "resources/" + token + "/reconstruction_sequential/PMVS/models/pmvs_options.txt.ply"))
                .build();
        try (Response response = client.newCall(requestGet).execute()) {
            if (response.isSuccessful()) {
                try (FileOutputStream stream = new FileOutputStream(result)) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        stream.write(body.byteStream().read());
                    }
                }
            }
        } catch (IOException e) {
            throw new AppException("Can't get result", e);
        }
    }
}
