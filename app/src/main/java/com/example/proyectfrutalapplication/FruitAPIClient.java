package com.example.proyectfrutalapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FruitAPIClient {

    private static final String TAG = "FruitAPIClient";
    private static final String BASE_URL = "https://www.fruityvice.com/api/";

    // Interfaz para callbacks
    public interface OnFruitsLoadedListener {
        void onFruitsLoaded(List<FruitAPI> fruits);
        void onError(String error);
    }

    public interface OnFruitLoadedListener {
        void onFruitLoaded(FruitAPI fruit);
        void onError(String error);
    }

    // Método para obtener todas las frutas
    public static void getAllFruits(OnFruitsLoadedListener listener) {
        new GetAllFruitsTask(listener).execute(BASE_URL + "fruit/all");
    }

    // Método para obtener una fruta por nombre
    public static void getFruitByName(String name, OnFruitLoadedListener listener) {
        new GetFruitTask(listener).execute(BASE_URL + "fruit/" + name);
    }

    // Método para obtener una fruta por ID
    public static void getFruitById(int id, OnFruitLoadedListener listener) {
        new GetFruitTask(listener).execute(BASE_URL + "fruit/" + id);
    }

    // AsyncTask para obtener todas las frutas
    private static class GetAllFruitsTask extends AsyncTask<String, Void, String> {
        private OnFruitsLoadedListener listener;
        private String errorMessage;

        GetAllFruitsTask(OnFruitsLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    connection.disconnect();

                    return response.toString();
                } else {
                    errorMessage = "Error HTTP: " + responseCode;
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error en la petición: " + e.getMessage());
                errorMessage = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && listener != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    List<FruitAPI> fruits = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        FruitAPI fruit = new FruitAPI(jsonObject);
                        fruits.add(fruit);
                    }

                    listener.onFruitsLoaded(fruits);
                } catch (Exception e) {
                    Log.e(TAG, "Error al parsear JSON: " + e.getMessage());
                    listener.onError("Error al procesar datos: " + e.getMessage());
                }
            } else if (listener != null) {
                listener.onError(errorMessage != null ? errorMessage : "Error desconocido");
            }
        }
    }

    // AsyncTask para obtener una sola fruta
    private static class GetFruitTask extends AsyncTask<String, Void, String> {
        private OnFruitLoadedListener listener;
        private String errorMessage;

        GetFruitTask(OnFruitLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    connection.disconnect();

                    return response.toString();
                } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    errorMessage = "Fruta no encontrada";
                    return null;
                } else {
                    errorMessage = "Error HTTP: " + responseCode;
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error en la petición: " + e.getMessage());
                errorMessage = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && listener != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    FruitAPI fruit = new FruitAPI(jsonObject);
                    listener.onFruitLoaded(fruit);
                } catch (Exception e) {
                    Log.e(TAG, "Error al parsear JSON: " + e.getMessage());
                    listener.onError("Error al procesar datos: " + e.getMessage());
                }
            } else if (listener != null) {
                listener.onError(errorMessage != null ? errorMessage : "Error desconocido");
            }
        }
    }
}