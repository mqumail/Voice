package app.com.muhammad.voice.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.com.muhammad.voice.DTO.CheckIn;
import app.com.muhammad.voice.R;

public class RecyclerViewReviewsAdapter extends RecyclerView.Adapter<RecyclerViewReviewsAdapter.RecyclerViewHolder>
{
    private List<CheckIn> mData;
    private LayoutInflater mInflater;


    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewReviewsAdapter(Context context, List<CheckIn> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView  userName;
        TextView  review;
        TextView checkInDate;

        public RecyclerViewHolder(View v)
        {
            super(v);
            userName = v.findViewById(R.id.userNameTextView);
            review = v.findViewById(R.id.reviewTextView);
            checkInDate = v.findViewById(R.id.reviewDateTextView);
        }
    }

    // parent activity will implement this method to respond to click events
    public interface OnCommentsListener {
        void onCommentClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewReviewsAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = mInflater.inflate(R.layout.reviews_custom_view, parent, false);
        return new RecyclerViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position)
    {
        CheckIn checkIn = mData.get(position);
        TextView userNameTextView = holder.userName;
        TextView reviewTextView = holder.review;
        TextView checkInDateTextView = holder.checkInDate;
        userNameTextView.setText(checkIn.getUserName());
        reviewTextView.setText(checkIn.getReview());
        checkInDateTextView.setText(checkIn.getCheckInTime());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }
}
