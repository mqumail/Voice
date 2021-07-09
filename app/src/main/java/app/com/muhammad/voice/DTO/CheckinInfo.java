package app.com.muhammad.voice.DTO;

import com.google.firebase.Timestamp;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckinInfo
{
    public CheckinInfo(boolean isLocal, boolean isHearted, boolean isIdentifiedCheckIn, String userName, String review, Timestamp checkInTime) {
        this.isLocal = isLocal;
        this.isHearted = isHearted;
        this.isIdentifiedCheckIn = isIdentifiedCheckIn;
        this.userName = userName;
        this.review = review;
        this.checkInTime = checkInTime;
    }

    private boolean isLocal;
    private boolean isHearted;
    private boolean isIdentifiedCheckIn;
    private String userName;
    private String review;
    private Timestamp checkInTime;

    public CheckinInfo()
    {

    }

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

    public Timestamp getCheckInTime()
    {
        return checkInTime;
    }

    public void setCheckInTime(Timestamp checkInTime)
    {
        this.checkInTime = checkInTime;
    }
}
