package app.com.muhammad.voice;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.com.muhammad.voice.Adapters.RecyclerViewAdapter;
import app.com.muhammad.voice.Adapters.RecyclerViewReviewsAdapter;
import app.com.muhammad.voice.DTO.CheckinInfo;
import app.com.muhammad.voice.DTO.PlaceInfo;

class DisplayUserCheckIns
{
    private GoogleMap mMap;
    private CollectionReference mCollectionReference;
    private HomeScreenActivity mHomeScreenActivity;

    private static final String TAG = "DisplayUserCheckIns";

    private PopupWindow placeDetailPopupWindow, revealedUserPopupWindow, reviewsPopupWindow;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerViewReviews;
    private RecyclerView.Adapter mAdapterReviews;
    private RecyclerView.LayoutManager mLayoutManagerReviews;
    private List<String> revealedUserNameDataSet;
    private List<CheckinInfo> reviewsDataSet;
    private List<String> emptyUserNameDataSetMessage, emptyReviewsDataSetMessage;

    public DisplayUserCheckIns(HomeScreenActivity homeScreenActivity, GoogleMap map, CollectionReference collectionReference)
    {
        mMap = map;
        mCollectionReference = collectionReference;
        mHomeScreenActivity = homeScreenActivity;
    }

    public void LoadUserCheckIns()
    {
        // load the user check ins
        mCollectionReference
                .addSnapshotListener(new EventListener<QuerySnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e)
                    {
                        if (e != null)
                        {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<PlaceInfo> userCheckIns = AddMarkersOnMap(value);

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                        {
                            private TextView name, address,
                                    localNumber, localUpvotes,
                                    touristNumber, touristUpvotes,
                                    commentsNumber, revealedUserName,
                                    revealedUserAddress, placeReviewTitle;

                            private Button placeDetailPopupWindowCloseButton,
                                    placeDetailPopupWindowRevealedUserButton,
                                    revealedUserPopupWindowCloseButton,
                                    reviewsButton, reviewsCloseButton;


                            private int numberOfLocalVisitors, numberOfTouristVisitors,
                                    numberOfLocalHearts, numberOfTouristHearts,
                                    numberOfComments;

                            @Override
                            public boolean onMarkerClick(final Marker marker)
                            {
                                if(placeDetailPopupWindow != null)
                                {
                                    if (placeDetailPopupWindow.isShowing())
                                    {
                                        placeDetailPopupWindow.dismiss();
                                    }
                                }

                                LayoutInflater inflater = (LayoutInflater) mHomeScreenActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View customViewPlaceDetail = inflater.inflate(R.layout.place_details_popup_window, null);
                                final View customViewRevealedUser = inflater.inflate(R.layout.place_reveal_list_popup_window, null);
                                final View customViewCommentList = inflater.inflate(R.layout.comments_list_popup_window, null);

                                // Reset the variables on each click so accurate number is shown
                                numberOfLocalVisitors = 0;
                                numberOfTouristVisitors = 0;
                                numberOfLocalHearts = 0;
                                numberOfTouristHearts = 0;
                                numberOfComments = 0;

                                // Hide the default Info Window
                                marker.hideInfoWindow();

                                PlaceDetailsPopupWindow(customViewPlaceDetail, marker, customViewRevealedUser);

                                GuestBookPopupWindow(marker, customViewRevealedUser);

                                ReviewsPopupWindow(marker, customViewPlaceDetail, customViewCommentList);

                                // Position camera near the marker
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                                // Return true so the default infoWindow is not shown
                                return true;
                            }

                            private void PlaceDetailsPopupWindow(View customViewPlaceDetail, Marker marker, final View customViewRevealedUser)
                            {
                                placeDetailPopupWindow = new PopupWindow(
                                        customViewPlaceDetail,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);

                                View parent = mHomeScreenActivity.findViewById(android.R.id.content);

                                placeDetailPopupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    placeDetailPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                                    placeDetailPopupWindow.setElevation(20);
                                }

                                placeDetailPopupWindowCloseButton = customViewPlaceDetail.findViewById(R.id.closePopupButton);
                                placeDetailPopupWindowCloseButton.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        // Dismiss the popup window
                                        placeDetailPopupWindow.dismiss();
                                    }
                                });

                                placeDetailPopupWindowRevealedUserButton = customViewPlaceDetail.findViewById(R.id.revealedUsersButton);
                                placeDetailPopupWindowRevealedUserButton.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        // Set an elevation value for popup window
                                        // Call requires API level 21
                                        if(Build.VERSION.SDK_INT>=21){
                                            revealedUserPopupWindow.setElevation(5.0f);
                                        }

                                        mRecyclerView = customViewRevealedUser.findViewById(R.id.users_revealed_list_recycler_view);

                                        // use this setting to improve performance if you know that changes
                                        // in content do not change the layout size of the RecyclerView
                                        mRecyclerView.setHasFixedSize(true);

                                        // use a linear layout manager
                                        mLayoutManager = new LinearLayoutManager(mHomeScreenActivity);
                                        mRecyclerView.setLayoutManager(mLayoutManager);

                                        if (revealedUserNameDataSet != null)
                                        {
                                            if(revealedUserNameDataSet.size() > 0)
                                            {
                                                // specify an adapter (see also next example)
                                                mAdapter = new RecyclerViewAdapter(revealedUserNameDataSet);
                                                mRecyclerView.setAdapter(mAdapter);
                                            }
                                            else
                                            {
                                                emptyUserNameDataSetMessage = new ArrayList<>();
                                                emptyUserNameDataSetMessage.add("No revealed users have checked in yet!");
                                                mAdapter = new RecyclerViewAdapter(emptyUserNameDataSetMessage);
                                                mRecyclerView.setAdapter(mAdapter);
                                            }
                                        }

                                        revealedUserPopupWindow.showAtLocation(mHomeScreenActivity.mDrawerLayout, Gravity.CENTER,0,0);
                                    }
                                });

                                // Set an elevation value for popup window
                                // Call requires API level 21
                                if(Build.VERSION.SDK_INT>=21){
                                    placeDetailPopupWindow.setElevation(5.0f);
                                }

                                // Connect to DB and get the information needed to display on the popup
                                // Name and address of the place
                                // # of local visitors + # of hearts, # of tourist visitors + hearts, # of comments
                                name = customViewPlaceDetail.findViewById(R.id.namePlaceDetail);
                                address = customViewPlaceDetail.findViewById(R.id.addressPlaceDetail);
                                localNumber = customViewPlaceDetail.findViewById(R.id.localNumber);
                                localUpvotes = customViewPlaceDetail.findViewById(R.id.localUpvotes);
                                touristNumber = customViewPlaceDetail.findViewById(R.id.touristNumber);
                                touristUpvotes = customViewPlaceDetail.findViewById(R.id.touristUpvotes);
                                commentsNumber = customViewPlaceDetail.findViewById(R.id.commentNumber);

                                name.setText(marker.getTitle());
                                address.setText(marker.getSnippet());

                                mCollectionReference
                                        .whereEqualTo("name", marker.getTitle())
                                        .whereEqualTo("address", marker.getSnippet())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                                            {
                                                revealedUserNameDataSet = new ArrayList<>();
                                                reviewsDataSet = new ArrayList<>();

                                                for (DocumentSnapshot document : task.getResult())
                                                {
                                                    document.getReference()
                                                            .collection("CheckInsCollection")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                                            {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                                                {
                                                                    for (DocumentSnapshot document : task.getResult().getDocuments())
                                                                    {
                                                                        if (document.contains("Review") && !document.get("Review").equals(""))
                                                                        {
                                                                            CheckinInfo reviewData = new CheckinInfo();

                                                                            if ((boolean)document.get("IsIdentifiedCheckin"))
                                                                            {
                                                                                revealedUserNameDataSet.add((String)document.get("UserName"));
                                                                            }

                                                                            if ((boolean)document.get("IsIdentifiedCheckin"))
                                                                            {
                                                                                reviewData.setReview((String)document.get("Review"));
                                                                                reviewData.setUserName((String)document.get("UserName"));
                                                                                reviewData.setCheckInTime(new Timestamp((Date) document.get("CheckInTime")));
                                                                            }
                                                                            else
                                                                            {
                                                                                reviewData.setReview((String)document.get("Review"));
                                                                                reviewData.setUserName("Anonymous");
                                                                                reviewData.setCheckInTime(new Timestamp((Date) document.get("CheckInTime")));
                                                                            }

                                                                            reviewsDataSet.add(reviewData);
                                                                        }






                                                                        if ((boolean)document.get("IsLocal"))
                                                                        {
                                                                            numberOfLocalVisitors++;
                                                                            if ((boolean)document.get("IsHearted"))
                                                                            {
                                                                                numberOfLocalHearts++;
                                                                            }
                                                                        }
                                                                        else
                                                                        {
                                                                            numberOfTouristVisitors++;
                                                                            if ((boolean)document.get("IsHearted"))
                                                                            {
                                                                                numberOfTouristHearts++;
                                                                            }
                                                                        }
                                                                        if (document.get("Review") != null)
                                                                        {
                                                                            numberOfComments++;
                                                                        }
                                                                    }

                                                                    localNumber.setText(String.valueOf(numberOfLocalVisitors));
                                                                    localUpvotes.setText(String.valueOf(numberOfLocalHearts));
                                                                    touristNumber.setText(String.valueOf(numberOfTouristVisitors));
                                                                    touristUpvotes.setText(String.valueOf(numberOfTouristHearts));
                                                                    commentsNumber.setText(String.valueOf(numberOfComments));

                                                                    placeDetailPopupWindow.showAtLocation(mHomeScreenActivity.mDrawerLayout, Gravity.CENTER,0,0);
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }

                            private void ReviewsPopupWindow(final Marker marker, View customViewPlaceDetail, final View customViewCommentList)
                            {
                                reviewsPopupWindow = new PopupWindow(
                                        customViewCommentList,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT);

                                reviewsButton = customViewPlaceDetail.findViewById(R.id.reviews_button);
                                reviewsButton.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        // Set an elevation value for popup window
                                        // Call requires API level 21
                                        if(Build.VERSION.SDK_INT>=21){
                                            reviewsPopupWindow.setElevation(5.0f);
                                        }

                                        // set the title
                                        placeReviewTitle = customViewCommentList.findViewById(R.id.name_comments_list);
                                        placeReviewTitle.setText(marker.getTitle());

                                        mRecyclerViewReviews = customViewCommentList.findViewById(R.id.users_comments_list);

                                        // use this setting to improve performance if you know that changes
                                        // in content do not change the layout size of the RecyclerView
                                        mRecyclerViewReviews.setHasFixedSize(true);

                                        // use a linear layout manager
                                        mLayoutManagerReviews = new LinearLayoutManager(mHomeScreenActivity);
                                        mRecyclerViewReviews.setLayoutManager(mLayoutManagerReviews);

                                        if (reviewsDataSet != null)
                                        {
                                            if(reviewsDataSet.size() > 0)
                                            {
                                                // specify an adapter (see also next example)
                                                mAdapterReviews = new RecyclerViewReviewsAdapter(reviewsDataSet);
                                                mRecyclerViewReviews.setAdapter(mAdapterReviews);
                                            }
                                            else
                                            {
                                                emptyReviewsDataSetMessage = new ArrayList<>();
                                                emptyReviewsDataSetMessage.add("No reviews yet!");
                                                mAdapterReviews = new RecyclerViewAdapter(emptyReviewsDataSetMessage);
                                                mRecyclerViewReviews.setAdapter(mAdapterReviews);
                                            }
                                        }

                                        reviewsPopupWindow.showAtLocation(mHomeScreenActivity.mDrawerLayout, Gravity.CENTER,0,0);
                                    }
                                });

                                reviewsCloseButton = customViewCommentList.findViewById(R.id.close_button);
                                reviewsCloseButton.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        // Dismiss the popup window
                                        reviewsPopupWindow.dismiss();
                                    }
                                });
                            }

                            private void GuestBookPopupWindow(Marker marker, View customViewRevealedUser)
                            {
                                revealedUserPopupWindow = new PopupWindow(
                                        customViewRevealedUser,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT);

                                revealedUserPopupWindowCloseButton = customViewRevealedUser.findViewById(R.id.close_button);
                                revealedUserPopupWindowCloseButton.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        // Dismiss the popup window
                                        revealedUserPopupWindow.dismiss();
                                    }
                                });

                                revealedUserName = customViewRevealedUser.findViewById(R.id.revealedUserNameTextView);
                                revealedUserAddress = customViewRevealedUser.findViewById(R.id.revealedUserAddressTextView);

                                revealedUserName.setText(marker.getTitle());
                                revealedUserAddress.setText(R.string.guest_book_title);
                            }
                        });

                        Log.d(TAG, "Current check ins: " + userCheckIns);
                    }
                });
    }

    @NonNull
    private List<PlaceInfo> AddMarkersOnMap(@Nullable QuerySnapshot value)
    {
        List<PlaceInfo> userCheckIns = new ArrayList<>();
        for (QueryDocumentSnapshot doc : value)
        {
            // Create a new placeInfo object for each check in user has made
            PlaceInfo userCheckIn = new PlaceInfo();

            if (doc.get("name") != null)
            {
                userCheckIn.setName(doc.getString("name"));
            }
            if (doc.get("address") != null)
            {
                userCheckIn.setAddress(doc.getString("address"));
            }
            if (doc.get("id") != null)
            {
                userCheckIn.setId(doc.getString("id"));
            }
            if (doc.get("phone") != null)
            {
                userCheckIn.setPhoneNumber(doc.getString("phone"));
            }
            if (doc.get("website") != null)
            {
                userCheckIn.setWebsiteUri(Uri.parse(doc.getString("website")));
            }

            userCheckIn.setLatlng(new LatLng(doc.getGeoPoint("LatLng").getLatitude(),
                    doc.getGeoPoint("LatLng").getLongitude()));

            try
            {
                userCheckIn.setRating(doc.getDouble("rating"));
            }
            catch (NullPointerException ex)
            {
                Log.e(TAG, "Null exception");
            }

            // Add the individual check in to the collection
            userCheckIns.add(userCheckIn);
        }

        mMap.clear();

        // Add the markers, including the new ones on the map
        for (PlaceInfo checkIn: userCheckIns)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(checkIn.getLatlng())
                    .title(checkIn.getName())
                    .snippet(checkIn.getAddress())
                    // use Voice Icon here. TODO: the current icons are too large. Ask Luis to make it smaller
                    .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions);
        }
        return userCheckIns;
    }
}
