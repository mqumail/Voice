package app.com.muhammad.voice;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import app.com.muhammad.voice.DTO.PlaceInfo;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter
{
    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.map_custom_info_window, null);

        TextView nameTextView = view.findViewById(R.id.name);
        TextView addressTextView = view.findViewById(R.id.address);
        /*TextView phoneNumberTextView = view.findViewById(R.id.phone_number);
        TextView websiteUrlTextView = view.findViewById(R.id.website_url);
        TextView ratingTextView = view.findViewById(R.id.rating);*/

        nameTextView.setText(marker.getTitle());
        //addressTextView.setText(marker.getSnippet());

        PlaceInfo infoWindowData = (PlaceInfo) marker.getTag();

        //int imageId = context.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),
        //        "drawable", context.getPackageName());
        //img.setImageResource(imageId);

        addressTextView.setText(infoWindowData.getAddress() == null ? "" : infoWindowData.getAddress());
        /*phoneNumberTextView.setText(infoWindowData.getPhoneNumber() == null ? "" : infoWindowData.getPhoneNumber());
        websiteUrlTextView.setText(infoWindowData.getWebsiteUri() == null ? "" : infoWindowData.getWebsiteUri().toString());
        if (infoWindowData.getRating() >= 0)
        {
            ratingTextView.setText(("" + MathmeticalOperations.round(infoWindowData.getRating(), 1)));
        }*/

        return view;
    }
}
