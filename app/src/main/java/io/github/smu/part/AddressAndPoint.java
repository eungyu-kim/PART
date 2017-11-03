package io.github.smu.part;

/**
 * Created by User on 2017-10-26.
 */
import com.skp.Tmap.TMapPoint;
/**
 * Created by User on 2017-10-16.
 */


public class AddressAndPoint {
    private String address = "";
    private final double latitude;
    private final double longitude;

    public AddressAndPoint(final double lat, final double lon) {
        latitude = lat;
        longitude = lon;
    }

    public void setAddress(String addr) {
        address = addr;
    }

    public final String getAddress() {
        return address;
    }

    public final double getLatitude() {
        return latitude;
    }

    public final double getLongitude() {
        return longitude;
    }

    public final TMapPoint getTMapPoint() {
        return new TMapPoint(getLatitude(), getLongitude());
    }

}