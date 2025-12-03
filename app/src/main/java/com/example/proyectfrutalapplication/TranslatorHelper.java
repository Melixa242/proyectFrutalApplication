package com.example.proyectfrutalapplication;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

/**
 * Helper para traducir texto de inglés a español usando ML Kit
 * Descarga los modelos necesarios automáticamente
 */
public class TranslatorHelper {

    private static final String TAG = "TranslatorHelper";
    private Translator translator;
    private Context context;
    private boolean isModelDownloaded = false;

    public interface TranslationCallback {
        void onSuccess(String translatedText);
        void onFailure(String error);
        void onDownloading(String message);
    }

    public TranslatorHelper(Context context) {
        this.context = context;
        initializeTranslator();
    }

    /**
     * Inicializa el traductor de inglés a español
     */
    private void initializeTranslator() {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)  // Desde inglés
                .setTargetLanguage(TranslateLanguage.SPANISH)  // A español
                .build();

        translator = Translation.getClient(options);
    }

    /**
     * Traduce texto de inglés a español
     */
    public void translateText(String englishText, TranslationCallback callback) {
        if (englishText == null || englishText.trim().isEmpty()) {
            callback.onFailure("Texto vacío");
            return;
        }

        // Configurar condiciones de descarga (solo WiFi o datos móviles)
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()  // Cambiar a false si quieres permitir datos móviles
                .build();

        // Descargar modelo si no está disponible
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(aVoid -> {
                    isModelDownloaded = true;
                    Log.d(TAG, "Modelo descargado correctamente");

                    // Realizar traducción
                    translator.translate(englishText)
                            .addOnSuccessListener(translatedText -> {
                                Log.d(TAG, "Traducción exitosa: " + englishText + " -> " + translatedText);
                                callback.onSuccess(translatedText);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error en traducción: " + e.getMessage());
                                callback.onFailure("Error al traducir: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error descargando modelo: " + e.getMessage());
                    callback.onFailure("Error descargando modelo: " + e.getMessage());
                });
    }

    /**
     * Traduce múltiples textos en lote
     */
    public void translateBatch(String[] englishTexts, BatchTranslationCallback callback) {
        String[] translations = new String[englishTexts.length];
        final int[] completedCount = {0};

        for (int i = 0; i < englishTexts.length; i++) {
            final int index = i;
            translateText(englishTexts[i], new TranslationCallback() {
                @Override
                public void onSuccess(String translatedText) {
                    translations[index] = translatedText;
                    completedCount[0]++;

                    if (completedCount[0] == englishTexts.length) {
                        callback.onAllTranslated(translations);
                    }
                }

                @Override
                public void onFailure(String error) {
                    translations[index] = englishTexts[index]; // Mantener original si falla
                    completedCount[0]++;

                    if (completedCount[0] == englishTexts.length) {
                        callback.onAllTranslated(translations);
                    }
                }

                @Override
                public void onDownloading(String message) {
                    callback.onProgress("Descargando modelos...");
                }
            });
        }
    }

    public interface BatchTranslationCallback {
        void onAllTranslated(String[] translations);
        void onProgress(String message);
    }

    /**
     * Verifica si el modelo está descargado
     */
    public void checkModelDownloaded(ModelCheckCallback callback) {
        RemoteModelManager modelManager = RemoteModelManager.getInstance();
        TranslateRemoteModel englishModel = new TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build();
        TranslateRemoteModel spanishModel = new TranslateRemoteModel.Builder(TranslateLanguage.SPANISH).build();

        modelManager.getDownloadedModels(TranslateRemoteModel.class)
                .addOnSuccessListener(models -> {
                    boolean hasEnglish = false;
                    boolean hasSpanish = false;

                    for (TranslateRemoteModel model : models) {
                        if (model.getLanguage().equals(TranslateLanguage.ENGLISH)) {
                            hasEnglish = true;
                        }
                        if (model.getLanguage().equals(TranslateLanguage.SPANISH)) {
                            hasSpanish = true;
                        }
                    }

                    callback.onResult(hasEnglish && hasSpanish);
                })
                .addOnFailureListener(e -> callback.onResult(false));
    }

    public interface ModelCheckCallback {
        void onResult(boolean isDownloaded);
    }

    /**
     * Descarga manualmente los modelos
     */
    public void downloadModels(DownloadCallback callback) {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        callback.onDownloadStarted();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(aVoid -> {
                    isModelDownloaded = true;
                    callback.onDownloadSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onDownloadFailed(e.getMessage());
                });
    }

    public interface DownloadCallback {
        void onDownloadStarted();
        void onDownloadSuccess();
        void onDownloadFailed(String error);
    }

    /**
     * Elimina los modelos descargados para liberar espacio
     */
    public void deleteModels(DeleteCallback callback) {
        RemoteModelManager modelManager = RemoteModelManager.getInstance();
        TranslateRemoteModel englishModel = new TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build();
        TranslateRemoteModel spanishModel = new TranslateRemoteModel.Builder(TranslateLanguage.SPANISH).build();

        modelManager.deleteDownloadedModel(englishModel)
                .addOnSuccessListener(aVoid -> {
                    modelManager.deleteDownloadedModel(spanishModel)
                            .addOnSuccessListener(aVoid1 -> {
                                isModelDownloaded = false;
                                callback.onDeleteSuccess();
                            })
                            .addOnFailureListener(e -> callback.onDeleteFailed(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onDeleteFailed(e.getMessage()));
    }

    public interface DeleteCallback {
        void onDeleteSuccess();
        void onDeleteFailed(String error);
    }

    /**
     * Cierra el traductor y libera recursos
     */
    public void close() {
        if (translator != null) {
            translator.close();
        }
    }

    /**
     * Método auxiliar para traducir productos de la API
     */
    public void translateProduct(Product product, TranslationCallback callback) {
        translateText(product.getName(), new TranslationCallback() {
            @Override
            public void onSuccess(String translatedName) {
                product.setName(translatedName);

                // Si hay notas, también traducirlas
                if (product.getNotes() != null && !product.getNotes().isEmpty()) {
                    translateText(product.getNotes(), new TranslationCallback() {
                        @Override
                        public void onSuccess(String translatedNotes) {
                            product.setNotes(translatedNotes);
                            callback.onSuccess("Producto traducido");
                        }

                        @Override
                        public void onFailure(String error) {
                            callback.onSuccess("Nombre traducido, notas sin traducir");
                        }

                        @Override
                        public void onDownloading(String message) {
                            callback.onDownloading(message);
                        }
                    });
                } else {
                    callback.onSuccess("Producto traducido");
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }

            @Override
            public void onDownloading(String message) {
                callback.onDownloading(message);
            }
        });
    }
}