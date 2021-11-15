package app.com.muhammad.voice;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.com.muhammad.voice.DTO.CheckinInfo;
import app.com.muhammad.voice.DTO.PlaceInfo;
import app.com.muhammad.voice.utils.MyCallBack;
import app.com.muhammad.voice.utils.SharedPreferencesManagement;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class UserCheckIns
{
//    private CollectionReference mCollectionReference;
//    private HomeScreenActivity mHomeScreenActivity;
//    private GoogleMap mMap;

    private static final String TAG = "UserCheckIns";
    private static final float DEFAULT_ZOOM = 15f;

    private PopupWindow commentAndVotesPopup;
    private PlaceInfo mPlace;
//    private PendingResult<PlaceBuffer> placeResult;
//
//    UserCheckIns(HomeScreenActivity homeScreenActivity, GoogleMap map, CollectionReference collectionReference)
//    {
//        mMap = map;
//        mCollectionReference = collectionReference;
//        mHomeScreenActivity = homeScreenActivity;
//    }

    void PlacePicker(Intent data)
    {
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

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            commentAndVotesPopup.setElevation(5.0f);
        }

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

        // Allow user to enter comments
        commentAndVotesPopup.setFocusable(true);

        // Finally, show the popup window at the center location of root relative layout
        //commentAndVotesPopup.showAtLocation(mHomeScreenActivity.mDrawerLayout, Gravity.CENTER,0,0);
    }



    private void CheckIfLocalCheckIn(final MyCallBack myCallBackIfLocalCheckIn)
    {
        // TODO: Instead of just checking the names of the cities, also check the cities ID
        // for example, there is Weimar in Germany and Also in Texas USA



        final List<String> cityNames = new ArrayList<>();



    }

    private void PlaceExistsInDB(final MyCallBack myCallBack, String id, CharSequence name){
    }

    private void UpdatePlaceInfo(String id, CharSequence name)
    {

    }

    // Store the place info in the FireStore
    private void SavePlaceInfo()
    {
    }

    private void moveCamera(float zoom, PlaceInfo placeInfo){
    }

    private void hideSoftKeyboard(){

    }

}
