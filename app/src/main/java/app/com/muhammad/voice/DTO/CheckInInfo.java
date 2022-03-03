package app.com.muhammad.voice.DTO;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;

public class CheckInInfo
{
    @NonNull
    private  String id;

    private boolean isLocal;
    private boolean isHearted;
    private boolean isIdentifiedCheckIn;
    private String userName;
    private String review;

    public boolean isLocal()
    {
        return isLocal;
    }

    public void setLocal(boolean local)
    {
        isLocal = local;
    }

    public boolean isHearted()
    {
        return isHearted;
    }

    public void setHearted(boolean hearted)
    {
        isHearted = hearted;
    }

    public boolean isIdentifiedCheckIn()
    {
        return isIdentifiedCheckIn;
    }

    public void setIdentifiedCheckIn(boolean identifiedCheckIn)
    {
        isIdentifiedCheckIn = identifiedCheckIn;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getReview()
    {
        return review;
    }

    public void setReview(String review)
    {
        this.review = review;
    }


    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }
}
