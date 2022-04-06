package app.com.muhammad.voice.Database;

import static app.com.muhammad.voice.util.Constants.CHECK_INS_COLLECTION_PATH;
import static app.com.muhammad.voice.util.Constants.PLACES_COLLECTION_PATH;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.com.muhammad.voice.DTO.CheckIn;
import app.com.muhammad.voice.DTO.PlaceInfo;
import app.com.muhammad.voice.Database.callbacks.FirebaseCheckInsCallback;
import app.com.muhammad.voice.Database.callbacks.FirebasePlacesCallback;


public class FirestoreFirebaseClient {
    private static final String TAG = "TK_FirestoreFirebaseClient";

    private FirebaseFirestore db;
    private static FirestoreFirebaseClient instance;
    private FirebaseApp voiceApp;

    public FirestoreFirebaseClient(Context context) {

        db = FirebaseFirestore.getInstance();

        // Init: App & DB
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://voice-dc16d.firebaseio.com")
                .setApiKey("AIzaSyA6EQMxZXkCmy1R3gTl4g9s3X-lAn0SsME")
                .setApplicationId("voice-dc16d")
                .build();

        boolean hasBeenInitialized = false;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(context);
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)){
                hasBeenInitialized = true;
                voiceApp = app;
            }
        }

        if(!hasBeenInitialized) {
            voiceApp = FirebaseApp.initializeApp(context, options);
        }
        db = FirebaseFirestore.getInstance(voiceApp);


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public static FirestoreFirebaseClient getInstance(Context context)
    {
        if(instance == null)
            instance = new FirestoreFirebaseClient(context);
        return instance;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void getPlacesInfo(FirebasePlacesCallback callback) {
        CollectionReference collectionRef = getDb().collection(PLACES_COLLECTION_PATH);
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "TKKKKKKK_onComplete: inside getPlaceInfo onComplete - 2nd");
                if (task.isSuccessful()) {
                    ArrayList<PlaceInfo> placesInfo = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        PlaceInfo taskItem = document.toObject(PlaceInfo.class);
                        String placeId = document.getId();
                        taskItem.setId(placeId);
                        placesInfo.add(taskItem);
                    }
                    Log.d(TAG, placesInfo.toString());
                    callback.getPlacesCallBack(placesInfo);
                } else {
                    Log.w(TAG, "onComplete: No documents returned.");
                }
            }
        });
    }

    public void getCheckInForPlace(String id, FirebaseCheckInsCallback handler)
    {
        Log.d(TAG, "getCheckInForPlace: hello");
        CollectionReference ref = getDb().collection(PLACES_COLLECTION_PATH).document(id).collection(CHECK_INS_COLLECTION_PATH);

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<CheckIn> checkIns = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    CheckIn taskItem = document.toObject(CheckIn.class);
                    checkIns.add(taskItem);
                }
                Log.d(TAG, "getCheckInForPlace: " + checkIns.size());
                handler.getCheckInsCallBack(checkIns);
            }
        });
    }

    //save
    public void savePlace(Map<String, Object> placeInfoMap, Map<String, Object> checkInMap) {
        getDb().collection(PLACES_COLLECTION_PATH)
                .add(placeInfoMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "TK_DocumentSnapshot added with ID: " + documentReference.getId());
                    getDb().collection(PLACES_COLLECTION_PATH)
                            .document(documentReference.getId())
                            .collection("CheckInsCollection")
                            .add(checkInMap)
                            .addOnSuccessListener(documentReference1 ->
                                    Log.d(TAG, "TK_DocumentSnapshot added with ID: " + documentReference1.getId()))
                            .addOnFailureListener(e ->
                                    Log.w(TAG, "TK_Error adding document", e));

                })
                .addOnFailureListener(e -> Log.w(TAG, "TK_Error adding document", e));
    }

    //update
    public void updateCheckInsDocument(Map<String, Object> checkInCollection, PlaceInfo placeInfo) {
        getDb().collection(PLACES_COLLECTION_PATH)
                .document(placeInfo.getId())
                .collection(CHECK_INS_COLLECTION_PATH)
                .add(checkInCollection)
                .addOnSuccessListener(documentReference1
                        -> Log.d(TAG, "TK_updateDocument: Following check in document was updated: " + documentReference1.toString()));
    }

    //delete
    public void deleteDocument(String document) {

    }
}

