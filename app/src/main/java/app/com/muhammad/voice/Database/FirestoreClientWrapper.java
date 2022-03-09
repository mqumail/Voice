/*
 * Copyright 2018 The StartupOS Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.com.muhammad.voice.Database;

import android.os.Build;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

//import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import android.content.Context;

import androidx.annotation.RequiresApi;

/** A proto wrapper for Firestore's client, that uses protos' binary format. */
public class FirestoreClientWrapper {
    private static final String PROTO_FIELD = "proto";
    private static final String TAG = "FirestorePlacesClient";

    private FirebaseFirestore mFirestore;

    FirebaseFirestore database;

//    public FirestorePlacesClient(String serviceAccountJson) {
//        try {
//            InputStream serviceAccount = new FileInputStream(serviceAccountJson);
//            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
//            FirebaseOptions.Builder builder = FirebaseOptions.builder();
//            FirebaseOptions options = builder.setCredentials(credentials).build();
//            try {
//                FirebaseApp.initializeApp(options);
//            } catch (IllegalStateException e) {
//                if (e.getMessage().contains("already exists")) {
//                    // Firestore is probably already initialized - do nothing
//                } else {
//                    throw e;
//                }
//            }
//            client = FirestoreClient.getFirestore();
//            storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public FirestoreClientWrapper(Context context, String project) {
        // TODO
        // Initializing Firebase programmatically here does not work with FirebaseRemoteConfig.
        // If you comment out this block & comment in google_app_id in strings.xml to let Firebase
        // initialize automatically, FirebaseRemoteConfig *does* work. But I need to be able to
        // initialize Firebase programmatically so I can specify the values below at runtime.

        //    "project_number": "496533077048",
        //    "firebase_url": "https://voice-dc16d.firebaseio.com",
        //    "project_id": "voice-dc16d",
        //    "storage_bucket": "voice-dc16d.appspot.com"

        //       applicationId = options.applicationId;
        //      apiKey = options.apiKey;
        //      databaseUrl = options.databaseUrl;
        //      gaTrackingId = options.gaTrackingId;
        //      gcmSenderId = options.gcmSenderId;
        //      storageBucket = options.storageBucket;
        //      projectId = options.projectId;



        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:496533077048:android:e175e5a4333c833297facd")
                .setProjectId("voice-dc16d")
                .setStorageBucket("voice-dc16d.appspot.com")
                .setApiKey("AIzaSyA6EQMxZXkCmy1R3gTl4g9s3X-lAn0SsME")
                .setDatabaseUrl("https://voice-dc16d.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(context, options);

        mFirestore = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getClient() {
        return mFirestore;
    }

    private String joinPath(String collection, String documentId) {
        if (collection.endsWith("/")) {
            return collection + documentId;
        }
        return collection + "/" + documentId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Message parseProto(DocumentSnapshot document, Message.Builder builder) throws InvalidProtocolBufferException {
        return builder
                .build()
                .getParserForType()
                .parseFrom(Base64.getDecoder().decode(document.getString(PROTO_FIELD)));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ImmutableMap<String, String> encodeProto(Message proto) throws InvalidProtocolBufferException {
        byte[] protoBytes = proto.toByteArray();
        String base64BinaryString = Base64.getEncoder().encodeToString(protoBytes);
        return ImmutableMap.of(PROTO_FIELD, base64BinaryString);
    }



    private CollectionReference getCollectionReference(String[] parts, int length) {
        DocumentReference docRef;
        CollectionReference collectionRef = mFirestore.collection(parts[0]);
        for (int i = 1; i < length; i += 2) {
            docRef = collectionRef.document(parts[i]);
            collectionRef = docRef.collection(parts[i + 1]);
        }
        return collectionRef;
    }

    public CollectionReference getCollectionReference(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] parts = path.split("/");
        if (parts.length % 2 != 1) {
            throw new IllegalArgumentException("Path length should be odd but is " + parts.length);
        }
        return getCollectionReference(parts, parts.length);
    }

    public DocumentReference getDocumentReference(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] parts = path.split("/");
        if (parts.length % 2 != 0) {
            throw new IllegalArgumentException("Path length should be even but is " + parts.length);
        }
        return getCollectionReference(parts, parts.length - 1).document(parts[parts.length - 1]);
    }

    public DocumentReference getDocumentReference(String collection, String documentId) {
        return getDocumentReference(joinPath(collection, documentId));
    }



//    public ApiFuture<DocumentSnapshot> getDocumentAsync(String path) {
//        return getDocumentReference(path).get();
//    }

//    public ApiFuture<DocumentSnapshot> getDocumentAsync(String collection, String documentId) {
//        return getDocumentAsync(joinPath(collection, documentId));
//    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public Message getProtoDocument(String path, Message.Builder builder) {
//        try {
//            return parseProto(getDocument(path), builder);
//        } catch (IOException e) {
//            throw new IllegalStateException(e);
//        }
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public Message getProtoDocument(String collection, String documentId, Message.Builder builder) {
//        return getProtoDocument(joinPath(collection, documentId), builder);
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public ApiFuture<WriteResult> setProtoDocumentAsync(String path, Message proto) {
//        try {
//            return setDocumentAsync(path, encodeProto(proto));
//        } catch (InvalidProtocolBufferException e) {
//            throw new IllegalStateException(e);
//        }
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public ApiFuture<WriteResult> setProtoDocumentAsync(String collection, String documentId, Message proto) {
//        return setProtoDocumentAsync(joinPath(collection, documentId), proto);
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public WriteResult setProtoDocument(String path, Message proto) {
//        try {
//            return setProtoDocumentAsync(path, proto).get();
//        } catch (ExecutionException | InterruptedException e) {
//            throw new IllegalStateException(e);
//        }
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public WriteResult setProtoDocument(String collection, String documentId, Message proto) {
//        return setProtoDocument(joinPath(collection, documentId), proto);
//    }



//    public ApiFuture<WriteResult> setDocumentAsync(String path, Map map) {
//        return getDocumentReference(path).set(map);
//    }
//
//    public ApiFuture<WriteResult> setDocumentAsync(String collection, String documentId, Map<String, ?> map) {
//        return setDocumentAsync(joinPath(collection, documentId), map);
//    }


//    public ApiFuture<QuerySnapshot> getDocumentsAsync(String path) {
//        return getCollectionReference(path).get();
//    }
//
////    @RequiresApi(api = Build.VERSION_CODES.O)
////    public List<Message> getProtoDocuments(String path, Message.Builder builder) {
////        ImmutableList.Builder<Message> result = ImmutableList.builder();
////        try {
////            Message proto = builder.build();
////            QuerySnapshot querySnapshot = getDocumentsAsync(path).get();
////            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
////                result.add(parseProto(document, builder));
////            }
////            return result.build();
////        } catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e) {
////            throw new IllegalStateException(e);
////        }
////    }


//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public ApiFuture<DocumentReference> addProtoDocumentToCollectionAsync(String path, Message proto) {
//        try {
//            return getCollectionReference(path).add(encodeProto(proto));
//        } catch (InvalidProtocolBufferException e) {
//            throw new IllegalStateException(e);
//        }
//    }





//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public MessageWithId getDocumentFromCollection(String path, Message.Builder builder, boolean shouldRemove) {
//        try {
//            QuerySnapshot querySnapshot = getCollectionReference(path).limit(1).get().get();
//            if (querySnapshot.isEmpty()) {
//                return null;
//            }
//            QueryDocumentSnapshot queryDocumentSnapshot = querySnapshot.getDocuments().get(0);
//            MessageWithId result =
//                    MessageWithId.create(
//                            queryDocumentSnapshot.getId(), parseProto(queryDocumentSnapshot, builder));
//            if (shouldRemove) {
//                deleteDocument(path + "/" + queryDocumentSnapshot.getId());
//            }
//            return result;
//        } catch (ExecutionException | InterruptedException | InvalidProtocolBufferException e) {
//            throw new IllegalStateException(e);
//        }
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public MessageWithId getDocumentFromCollection(String path, Message.Builder proto) {
//        return getDocumentFromCollection(path, proto, false);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public MessageWithId popDocument(String path, Message.Builder proto) {
//        return getDocumentFromCollection(path, proto, true);
//    }



//    public ApiFuture<WriteResult> deleteDocumentAsync(String path) {
//        return getDocumentReference(path).delete();
//    }

//    public ApiFuture<WriteResult> deleteDocumentAsync(String collection, String documentId) {
//        return deleteDocumentAsync(joinPath(collection, documentId));
//    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public String uploadTo(String bucketName, String filePath, String fileName) throws IOException {
//
//        BlobInfo blobInfo =
//                storage.create(
//                        BlobInfo.newBuilder(bucketName, fileName)
//                                .setAcl(ImmutableList.of(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
//                                .build(),
//                        Files.toByteArray(Paths.get(filePath).toFile()));
//        return blobInfo.getMediaLink();
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public String downloadFrom(String bucketName, String fileName) throws IOException {
//        String[] parts = fileName.split("[.]");
//        String name = parts[0];
//        String extension = ".tmp";
//        if (parts.length > 1) {
//            extension = "." + parts[parts.length - 1];
//        }
//        File tempFile = File.createTempFile(name, extension);
//        storage.get(BlobId.of(bucketName, fileName)).downloadTo(Paths.get(tempFile.getAbsolutePath()));
//        return tempFile.getAbsolutePath();
//    }

//    public void addCollectionListener(String path, Message.Builder builder, ProtoEventListener listener) {
//        getCollectionReference(path)
//            .addSnapshotListener(
//                    (querySnapshot, e) -> {
//                        if (e != null) {
//                            listener.onEvent(null, e);
//                            return;
//                        }
//                        try {
//                            listener.onEvent(new ProtoQuerySnapshot(querySnapshot, builder), null);
//                        } catch (InvalidProtocolBufferException e2) {
//                            listener.onEvent(null, new IllegalArgumentException(e2));
//                        }
//                    });
//    }

    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("service-account.json"))
                .createScoped(Arrays.asList(
                        "https://www.googleapis.com/auth/firebase.database",
                        "https://www.googleapis.com/auth/userinfo.email"
                ));
        googleCredentials.refreshAccessToken();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}

