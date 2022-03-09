package app.com.muhammad.voice.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import app.com.muhammad.voice.R;

public class CustomInfoWindowAdapter
{
    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_window_info, null);
    }

    private void rendowWindowText(){
    }


}
