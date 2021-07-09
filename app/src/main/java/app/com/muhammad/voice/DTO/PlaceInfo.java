package app.com.muhammad.voice.DTO;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceInfo
{
    // IsLocal
    // IsLocalUpvote
    // IsTouristUpvote
    // TotalCheckins - stored in the DB
    // Save comment, revealOneSelf, up votes also

    private String name;
    private String address;
    private String phoneNumber;
    private String id;
    private Uri websiteUri;
    private LatLng latlng;
    private double rating;
    private String attributions;
    private String comment;

    private List<CheckinInfo> checkinInfos;

    // Delete the bottom ones
    //private boolean IsIdentifiedCheckIn;
    //private boolean IsHearted;
    //private boolean IsLocalCheckIn;

    public PlaceInfo(String name, String address,
                     String phoneNumber, String id,
                     Uri websiteUri, LatLng latlng,
                     float rating, String attributions,
                     String comment, boolean IsIdentifiedCheckIn,
                     boolean IsHearted, boolean IsLocalCheckIn) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.websiteUri = websiteUri;
        this.latlng = latlng;
        this.rating = rating;
        this.attributions = attributions;
        this.comment = comment;
        //this.IsIdentifiedCheckIn = IsIdentifiedCheckIn;
        //this.IsHearted = IsHearted;
        //this.IsLocalCheckIn = IsLocalCheckIn;
    }

    public PlaceInfo() {
        this.checkinInfos = new ArrayList<>();
    }

    public void addCheckInInfo(CheckinInfo checkinInfo){
        this.checkinInfos.add(checkinInfo);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public List<CheckinInfo> getCheckinInfos()
    {
        return checkinInfos;
    }

    public void setCheckinInfos(List<CheckinInfo> checkinInfos)
    {
        this.checkinInfos = checkinInfos;
    }

    public void copy(Place place) {
        this.address = place.getAddress();
        this.latlng = place.getLatLng();
    }

    @Override
    public boolean equals(Object object){
        boolean isEqual = false;

        if (object != null && object instanceof PlaceInfo){
            isEqual = (this.latlng.equals(((PlaceInfo) object).latlng));
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(String.valueOf(this.latlng));
    }

    /*public boolean isIdentifiedCheckIn()
    {
        return IsIdentifiedCheckIn;
    }

    public void setIdentifiedCheckIn(boolean anonymousCheckIn) { IsIdentifiedCheckIn = anonymousCheckIn; }

    public boolean isHearted()
    {
        return IsHearted;
    }

    public void setIsHearted(boolean upvote)
    {
        IsHearted = upvote;
    }

    public boolean isLocalCheckIn() { return IsLocalCheckIn; }

    public void setLocalCheckIn(boolean localCheckIn) { IsLocalCheckIn = localCheckIn; }*/

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", websiteUri=" + websiteUri +
                ", latlng=" + latlng +
                ", rating=" + rating +
                ", attributions='" + attributions + '\'' +
                '}';
    }
}
