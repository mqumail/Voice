package app.com.muhammad.voice;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;
import app.com.muhammad.voice.DTO.CheckinInfo;
import app.com.muhammad.voice.DTO.PlaceInfo;

class DisplayUserCheckIns
{
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

    public DisplayUserCheckIns(HomeScreenActivity homeScreenActivity)
    {
        mHomeScreenActivity = homeScreenActivity;
    }

    @NonNull
    private List<PlaceInfo> AddMarkersOnMap()
    {
        List<PlaceInfo> userCheckIns = new ArrayList<>();
        {
            // Create a new placeInfo object for each check in user has made
            PlaceInfo userCheckIn = new PlaceInfo();

            // Add the individual check in to the collection
            userCheckIns.add(userCheckIn);
        }

        // Add the markers, including the new ones on the map

        return userCheckIns;
    }
}
