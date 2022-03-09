//package app.com.muhammad.voice;
//
//import android.content.Intent;
//import android.location.Address;
//import android.location.Geocoder;
//import android.os.Build;
//import android.support.annotation.NonNull;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.PopupWindow;
//import android.widget.Switch;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//
//import com.google.android.gms.common.api.PendingResult;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.PlaceBuffer;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.ui.PlacePicker;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.GeoPoint;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import app.com.muhammad.voice.DTO.CheckinInfo;
//import app.com.muhammad.voice.DTO.PlaceInfo;
//import app.com.muhammad.voice.utils.MyCallBack;
//import app.com.muhammad.voice.utils.SharedPreferencesManagement;
//
//import static android.content.Context.LAYOUT_INFLATER_SERVICE;
//
//class UserCheckIns
//{
//    private CollectionReference mCollectionReference;
//    private HomeScreenActivity mHomeScreenActivity;
//    private GoogleMap mMap;
//
//    private static final String TAG = "UserCheckIns";
//    private static final float DEFAULT_ZOOM = 15f;
//
//    private PopupWindow commentAndVotesPopup;
//    private PlaceInfo mPlace;
//    private PendingResult<PlaceBuffer> placeResult;
//
//    UserCheckIns(HomeScreenActivity homeScreenActivity, GoogleMap map, CollectionReference collectionReference)
//    {
//        mMap = map;
//        mCollectionReference = collectionReference;
//        mHomeScreenActivity = homeScreenActivity;
//    }
//
//    void PlacePicker(Intent data)
//    {
//        Place place = PlacePicker.getPlace(mHomeScreenActivity, data);
//
//        placeResult = Places.GeoDataApi
//                .getPlaceById(mHomeScreenActivity.mGoogleApiClient, place.getId());
//
//        ///////////////////////////////////////////////////////
//        mPlace = new PlaceInfo();
//        mPlace.setCheckinInfo(new CheckinInfo());
//
//        // show a popup which will allow the user to pass a comment and upvotes
//        LayoutInflater inflater = (LayoutInflater) mHomeScreenActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
//        final View customView = inflater.inflate(R.layout.comments_and_votes_popup_window, null);
//
//        commentAndVotesPopup = new PopupWindow(
//                customView,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//
//        // Set an elevation value for popup window
//        // Call requires API level 21
//        if(Build.VERSION.SDK_INT>=21){
//            commentAndVotesPopup.setElevation(5.0f);
//        }
//
//        TextView checkinTitle = customView.findViewById(R.id.checkinTitle);
//        TextView checkinAddress = customView.findViewById(R.id.checkinAdress);
//        checkinTitle.setText(place.getName());
//        checkinAddress.setText(place.getAddress());
//        Button sendButton = customView.findViewById(R.id.commentSendButton);
//        sendButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                // Dismiss the popup window
//                commentAndVotesPopup.dismiss();
//
//                // Store the user comment, checkin as anonymous and upvote to PlaceInfo
//                EditText commentEditText = customView.findViewById(R.id.commentEditText);
//                Switch switchButton = customView.findViewById(R.id.userVisibilitySwitchButton);
//                ToggleButton upvoteToggleButton = customView.findViewById(R.id.upVoteToggleButton);
//
//                mPlace.getCheckinInfo().setReview(commentEditText.getText().toString());
//                mPlace.getCheckinInfo().setIdentifiedCheckIn(switchButton.isChecked());
//                mPlace.getCheckinInfo().setHearted(upvoteToggleButton.isChecked());
//                mPlace.getCheckinInfo().setCheckInTime(new Timestamp(new Date()));
//
//                // Place this call in the onCLick of popup:
//                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//            }
//        });
//
//        // Allow user to enter comments
//        commentAndVotesPopup.setFocusable(true);
//
//        // Finally, show the popup window at the center location of root relative layout
//        commentAndVotesPopup.showAtLocation(mHomeScreenActivity.mDrawerLayout, Gravity.CENTER,0,0);
//
//        String toastMsg = String.format("Place: %s", place.getName());
//        Toast.makeText(mHomeScreenActivity, toastMsg, Toast.LENGTH_LONG).show();
//    }
//
//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>()
//    {
//        @Override
//        public void onResult(@NonNull PlaceBuffer places) {
//            if(!places.getStatus().isSuccess()){
//                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            final Place place = places.get(0);
//
//            try{
//                mPlace.setName(place.getName().toString());
//                mPlace.setAddress(place.getAddress().toString());
////                mPlace.setAttributions(place.getAttributions().toString());
////                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
//                mPlace.setId(place.getId());
//                mPlace.setLatlng(place.getLatLng());
//                mPlace.setRating(place.getRating());
//                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
//                mPlace.setWebsiteUri(place.getWebsiteUri());
//
//                MyCallBack myCallBackIfLocalCheckIn = new MyCallBack()
//                {
//                    @Override
//                    public void onCallback(boolean localCheckIn)
//                    {
//                        mPlace.getCheckinInfo().setLocal(localCheckIn);
//                    }
//                };
//
//                CheckIfLocalCheckIn(place, myCallBackIfLocalCheckIn);
//
//            }catch (NullPointerException e){
//                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
//            }
//
//            // check if the place is already exist in the firebase, if it does,
//            // just increment the check in counter, otherwise create a new entry
//            final String id = place.getId();
//            final CharSequence name = place.getName();
//
//            MyCallBack myCallBack = new MyCallBack()
//            {
//                @Override
//                public void onCallback(boolean alreadyExists)
//                {
//                    if (alreadyExists)
//                    {
//                        UpdatePlaceInfo(id, name);
//                    }
//                    else
//                    {
//                        SavePlaceInfo();
//                    }
//                }
//            };
//
//            PlaceExistsInDB(myCallBack, place.getId(), place.getName());
//
//            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
//                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);
//
//            places.release();
//        }
//    };
//
//    private void CheckIfLocalCheckIn(Place place, final MyCallBack myCallBackIfLocalCheckIn)
//    {
//        // TODO: Instead of just checking the names of the cities, also check the cities ID
//        // for example, there is Weimar in Germany and Also in Texas USA
//
//        String savedCities = mHomeScreenActivity.localCities.getCitiesString();
//        final LatLng placeLatLng = place.getLatLng();
//
//        final List<String> cityNames = new ArrayList<>();
//
//        if (!savedCities.equals("empty"))
//        {
//            try {
//                String[] citiesArray = savedCities.split("/");
//                String[] cityInfo;
//
//                for (String aCitiesArray : citiesArray) {
//                    cityInfo = aCitiesArray.split("-");
//                    cityNames.add(cityInfo[1]);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        Places.GeoDataApi.getPlaceById(mHomeScreenActivity.mGoogleApiClient, place.getId())
//                .setResultCallback(new ResultCallback<PlaceBuffer>()
//                {
//                    @Override
//                    public void onResult(@NonNull PlaceBuffer places)
//                    {
//                        if (!places.getStatus().isSuccess())
//                        {
//                            // Request did not complete successfully
//                            return;
//                        }
//
//                        // Setup Geocoder
//                        Geocoder geocoder = new Geocoder(mHomeScreenActivity, Locale.getDefault());
//                        List<Address> addresses;
//
//                        try
//                        {
//                            addresses = geocoder.getFromLocation(
//                                    placeLatLng.latitude,
//                                    placeLatLng.longitude,
//                                    1);
//
//                            if (addresses.size() > 0)
//                            {
//                                // Here are some results you can geocode
//                                String ZIP;
//                                String city;
//                                String state;
//                                String country;
//
//                                if (addresses.get(0).getPostalCode() != null) {
//                                    ZIP = addresses.get(0).getPostalCode();
//                                    Log.d("ZIP", ZIP);
//                                }
//
//                                if (addresses.get(0).getLocality() != null) {
//                                    city = addresses.get(0).getLocality();
//                                    Log.d("city", city);
//
//                                    if (cityNames.contains(city))
//                                    {
//                                        myCallBackIfLocalCheckIn.onCallback(true);
//                                    }
//                                    else
//                                    {
//                                        myCallBackIfLocalCheckIn.onCallback(false);
//                                    }
//                                }
//
//                                if (addresses.get(0).getAdminArea() != null) {
//                                    state = addresses.get(0).getAdminArea();
//                                    Log.d("state", state);
//                                }
//
//                                if (addresses.get(0).getCountryName() != null) {
//                                    country = addresses.get(0).getCountryName();
//                                    Log.d("country", country);
//                                }
//                            }
//                        }
//                        catch (IOException e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    private void PlaceExistsInDB(final MyCallBack myCallBack, String id, CharSequence name)
//    {
//        mCollectionReference
//                .whereEqualTo("id", id)
//                .whereEqualTo("name", name)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
//                {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task)
//                    {
//                        if (task.isSuccessful())
//                        {
//                            for (QueryDocumentSnapshot document : task.getResult())
//                            {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//
//                            if (task.getResult().isEmpty())
//                            {
//                                // return false
//                                myCallBack.onCallback(false);
//                            }
//                            else
//                            {
//                                // return true
//                                myCallBack.onCallback(true);
//                            }
//                        }
//                        else
//                        {
//                            // Log that the call to DB failed.
//                            Log.e(TAG, "Call failed to DB");
//                        }
//                    }
//                });
//    }
//
//    private void UpdatePlaceInfo(String id, CharSequence name)
//    {
//        mCollectionReference
//                .whereEqualTo("id", id)
//                .whereEqualTo("name", name)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
//                {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task)
//                    {
//                        for (DocumentSnapshot document : task.getResult().getDocuments())
//                        {
//                            DocumentReference docReference = document.getReference();
//                            final Map<String, Object> CheckInsCollection = new HashMap<>();
//
//                            CheckInsCollection.put("IsLocal", mPlace.getCheckinInfo().isLocal());
//                            CheckInsCollection.put("IsHearted", mPlace.getCheckinInfo().isHearted());
//                            CheckInsCollection.put("IsIdentifiedCheckin", mPlace.getCheckinInfo().isIdentifiedCheckIn());
//                            CheckInsCollection.put("CheckInTime", mPlace.getCheckinInfo().getCheckInTime());
//
//                            if (!mPlace.getCheckinInfo().getReview().equals(""))
//                            {
//                                CheckInsCollection.put("Review", mPlace.getCheckinInfo().getReview());
//                            }
//
//
//                            if (mPlace.getCheckinInfo().isIdentifiedCheckIn())
//                            {
//                                // Get the username from the SP
//                                SharedPreferencesManagement spUserInfo = mHomeScreenActivity.userInformation;
//
//                                String mUserInfo = spUserInfo.loadSPInfo();
//
//                                if(mUserInfo != "empty")
//                                {
//                                    String[] userArray = mUserInfo.split("/");
//
//                                    if(userArray.length > 2)
//                                    {
//                                        // Append the username to the old name
//                                        CheckInsCollection.put("UserName", userArray[1]);
//                                    }
//                                }
//                            }
//
//                            docReference
//                                    .collection("CheckInsCollection")
//                                    .add(CheckInsCollection)
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
//                                    {
//                                        @Override
//                                        public void onSuccess(DocumentReference documentReference)
//                                        {
//                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener()
//                                    {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e)
//                                        {
//                                            Log.w(TAG, "Error adding document", e);
//                                        }
//                                    });
//                        }
//                    }
//                });
//    }
//
//    // Store the place info in the FireStore
//    private void SavePlaceInfo()
//    {
//        Map<String, Object> placeCollection = new HashMap<>();
//        if (mPlace.getName() != null)
//        {
//            placeCollection.put("name", mPlace.getName());
//        }
//        if (mPlace.getAddress() != null)
//        {
//            placeCollection.put("address", mPlace.getAddress());
//        }
//        if (mPlace.getId() != null)
//        {
//            placeCollection.put("id", mPlace.getId());
//        }
//        if (mPlace.getPhoneNumber() != null)
//        {
//            placeCollection.put("phone", mPlace.getPhoneNumber());
//        }
//        if (mPlace.getWebsiteUri() != null)
//        {
//            placeCollection.put("website", mPlace.getWebsiteUri().toString());
//        }
//        if (mPlace.getComment() != null)
//        {
//            placeCollection.put("review", mPlace.getCheckinInfo().getReview());
//        }
//
//        placeCollection.put("LatLng", new GeoPoint(mPlace.getLatlng().latitude, mPlace.getLatlng().longitude));
//        placeCollection.put("Rating", mPlace.getRating());
//
//        final Map<String, Object> checkInsCollection = new HashMap<>();
//
//        checkInsCollection.put("IsLocal", mPlace.getCheckinInfo().isLocal());
//        checkInsCollection.put("IsHearted", mPlace.getCheckinInfo().isHearted());
//        checkInsCollection.put("IsIdentifiedCheckin", mPlace.getCheckinInfo().isIdentifiedCheckIn());
//        checkInsCollection.put("CheckInTime", mPlace.getCheckinInfo().getCheckInTime());
//
//        if (!mPlace.getCheckinInfo().getReview().equals(""))
//        {
//            checkInsCollection.put("Review", mPlace.getCheckinInfo().getReview());
//        }
//
//        if (mPlace.getCheckinInfo().isIdentifiedCheckIn())
//        {
//            SharedPreferencesManagement spUserInfo = mHomeScreenActivity.userInformation;
//
//            String mUserInfo = spUserInfo.loadSPInfo();
//            if(mUserInfo != "empty")
//            {
//                String[] userArray = mUserInfo.split("/");
//
//                if(userArray.length > 2)
//                {
//                    checkInsCollection.put("UserName", userArray[1]);
//                }
//            }
//        }
//
//        // Add a new document with a generated ID
//        mCollectionReference
//                .add(placeCollection)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//
//                        mCollectionReference
//                                .document(documentReference.getId())
//                                .collection("CheckInsCollection")
//                                .add(checkInsCollection).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
//                        {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference)
//                            {
//                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                            }
//                        }).addOnFailureListener(new OnFailureListener()
//                        {
//                            @Override
//                            public void onFailure(@NonNull Exception e)
//                            {
//                                Log.w(TAG, "Error adding document", e);
//                            }
//                        });
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//    }
//
//    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo){
//        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//
//        //mMap.setInfoWindowAdapter(new CustomInfoWindowGoogleMap(HomeScreenActivity.this));
//
//        if(placeInfo != null){
//            try{
//                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
//                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
//                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
//                        "Price Rating: " + placeInfo.getRating() + "\n";
//
//            }catch (NullPointerException e){
//                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage() );
//            }
//        }else{
//            mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_img_marker_man))
//                    .position(latLng));
//        }
//
//        hideSoftKeyboard();
//    }
//
//    private void hideSoftKeyboard(){
//        mHomeScreenActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//    }
//
//}
