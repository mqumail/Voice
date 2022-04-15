package app.com.muhammad.voice.DTO;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;

public class CheckIn
{

    public CheckIn() {
    }

    public CheckIn(@NonNull String id, boolean local, boolean hearted, boolean identifiedCheckIn, String userName, String review, String checkInTime) {
        this.id = id;
        this.local = local;
        this.hearted = hearted;
        this.identifiedCheckIn = identifiedCheckIn;
        this.userName = userName;
        this.review = review;
        this.checkInTime = checkInTime;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isHearted() {
        return hearted;
    }

    public void setHearted(boolean hearted) {
        this.hearted = hearted;
    }

    public boolean isIdentifiedCheckIn() {
        return identifiedCheckIn;
    }

    public void setIdentifiedCheckIn(boolean identifiedCheckIn) {
        this.identifiedCheckIn = identifiedCheckIn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    @Override
    public String toString() {
        return "CheckIn{" +
                "id='" + id + '\'' +
                ", local=" + local +
                ", hearted=" + hearted +
                ", identifiedCheckIn=" + identifiedCheckIn +
                ", userName='" + userName + '\'' +
                ", review='" + review + '\'' +
                ", checkInTime='" + checkInTime + '\'' +
                '}';
    }

    @NonNull
    private  String id;
    private boolean local;
    private boolean hearted;
    private boolean identifiedCheckIn;
    private String userName;
    private String review;
    private String checkInTime;


}
