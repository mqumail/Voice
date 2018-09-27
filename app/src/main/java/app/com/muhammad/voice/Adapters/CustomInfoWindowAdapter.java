package app.com.muhammad.voice.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import app.com.muhammad.voice.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_window_info, null);
    }

    private void rendowWindowText(com.google.android.gms.maps.model.Marker marker, View view){

        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);

        if(!title.equals("")){
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.snippet);

        if(!snippet.equals("")){
            tvSnippet.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }
}
