package app.com.muhammad.voice;

import java.util.Date;

public class Marker
{
    private int localUserVisits;
    private int regularUserVisits;
    private int localUpVotes;
    private int regularUpVotes;
    private String userName;
    private String userComment;
    private Date commentDate;
    private int markerID;

    public int getLocalUserVisits()
    {
        return localUserVisits;
    }

    public void setLocalUserVisits(int localUserVisits)
    {
        this.localUserVisits = localUserVisits;
    }

    public int getRegularUserVisits()
    {
        return regularUserVisits;
    }

    public void setRegularUserVisits(int regularUserVisits)
    {
        this.regularUserVisits = regularUserVisits;
    }

    public int getLocalUpvotes()
    {
        return localUpVotes;
    }

    public void setLocalUpvotes(int localUpvotes)
    {
        this.localUpVotes = localUpvotes;
    }

    public int getRegularUpVotes()
    {
        return regularUpVotes;
    }

    public void setRegularUpVotes(int regularUpvotes)
    {
        this.regularUpVotes = regularUpvotes;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserComment()
    {
        return userComment;
    }

    public void setUserComment(String userComment)
    {
        this.userComment = userComment;
    }

    public Date getCommentDate()
    {
        return commentDate;
    }

    public void setCommentDate(Date commentDate)
    {
        this.commentDate = commentDate;
    }

    public int getMarkerID()
    {
        return markerID;
    }

    public void setMarkerID(int markerID)
    {
        this.markerID = markerID;
    }
}
