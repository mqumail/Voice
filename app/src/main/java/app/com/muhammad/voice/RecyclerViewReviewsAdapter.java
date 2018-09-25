package app.com.muhammad.voice;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.com.muhammad.voice.DTO.CheckinInfo;

public class RecyclerViewReviewsAdapter extends
        RecyclerView.Adapter<RecyclerViewReviewsAdapter.RecyclerViewHolder>
{
    private List<CheckinInfo> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewReviewsAdapter(List<CheckinInfo> myDataset) {
        mDataset = myDataset;
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

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewReviewsAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent,
                                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reviews_custom_view, parent, false);

        RecyclerViewHolder vh = new RecyclerViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position)
    {
        Date date = new Date();
        CheckinInfo checkinInfo = mDataset.get(position);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy");
        try {
            date = dateFormatter.parse(String.valueOf(checkinInfo.getCheckInTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        TextView userNameTextView = holder.userName;
        TextView reviewTextView = holder.review;
        TextView checkInDateTextView = holder.checkInDate;

        userNameTextView.setText(checkinInfo.getUserName());
        reviewTextView.setText(checkinInfo.getReview());
        checkInDateTextView.setText(dateFormatter.format(date));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
