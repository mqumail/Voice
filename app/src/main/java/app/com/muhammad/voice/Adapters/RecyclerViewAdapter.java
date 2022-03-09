package app.com.muhammad.voice.Adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

import app.com.muhammad.voice.DTO.PlaceInfo;
import app.com.muhammad.voice.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
{
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // each data item is just a string in this case
        public TextView poiName;
        public TextView poiAddress;
        public ItemClickListener itemClickListener;

        public RecyclerViewHolder(View v, ItemClickListener itemClickListener)
        {
            super(v);
            poiName = v.findViewById(R.id.poi_name);
            poiAddress = v.findViewById(R.id.poi_address);
            this.itemClickListener = itemClickListener;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(view, getBindingAdapterPosition());
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private List<PlaceInfo> mPlaces;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(Context context, List<PlaceInfo> places, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mClickListener = itemClickListener;
        mPlaces = places;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.poi_detail, parent, false);
        return new RecyclerViewHolder(view, mClickListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.poiName.setText(mPlaces.get(position).getName());
        holder.poiAddress.setText(mPlaces.get(position).getAddress());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPlaces.size();
    }
}