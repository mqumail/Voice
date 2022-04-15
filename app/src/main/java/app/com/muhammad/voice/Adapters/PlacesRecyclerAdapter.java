package app.com.muhammad.voice.Adapters;

import static app.com.muhammad.voice.util.LocationHelper.distanceToFromCurrentLocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.com.muhammad.voice.DTO.PlaceInfo;
import app.com.muhammad.voice.R;

public class PlacesRecyclerAdapter extends RecyclerView.Adapter<PlacesRecyclerAdapter.RecyclerViewHolder>
{
    private List<PlaceInfo> mPlaces = new ArrayList<>();
    private LayoutInflater mInflater;
    private OnPlaceListener onPlaceListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlacesRecyclerAdapter(Context context, List<PlaceInfo> places, OnPlaceListener onPlaceListener) {
        this.mInflater = LayoutInflater.from(context);
        this.onPlaceListener = onPlaceListener;
        mPlaces = places;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // each data item is just a string in this case
        public TextView poiName;
        public TextView poiAddress;
        public TextView poiDistance;
        public OnPlaceListener onPlaceListener;

        public RecyclerViewHolder(View v, OnPlaceListener onPlaceListener)
        {
            super(v);
            poiName = v.findViewById(R.id.poi_name);
            poiAddress = v.findViewById(R.id.poi_address);
            poiDistance = v.findViewById(R.id.poi_distance);
            this.onPlaceListener = onPlaceListener;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onPlaceListener.onPlaceClick(view, getBindingAdapterPosition());
        }
    }

    // parent activity will implement this method to respond to click events
    public interface OnPlaceListener {
        void onPlaceClick(View view, int position);
    }



    // Create new views (invoked by the layout manager)
    @Override
    public PlacesRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.poi_detail, parent, false);
        return new RecyclerViewHolder(view, onPlaceListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.poiName.setText(mPlaces.get(position).getName());
        holder.poiAddress.setText(mPlaces.get(position).getAddress());
        double distance = distanceToFromCurrentLocation(mPlaces.get(position).getLatLng(), mInflater.getContext());
        double distanceInKM = 0.001 * distance;

        String formattedDistance = new DecimalFormat("#.##").format(distanceInKM);

        holder.poiDistance.setText(formattedDistance + " KM away");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPlaces.size();
    }
}