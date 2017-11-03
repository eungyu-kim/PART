package io.github.smu.part;

/**
 * Created by User on 2017-10-26.
 */
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

/**
 * Created by User on 2017-10-16.
 */

public class ATFindCurrentLocation extends AsyncTask<Void, Integer, AddressAndPoint> {
    private final TMapGpsManager    f_gps;
    private final TMapView          f_tmapView;
    private final RelativeLayout    f_rlCover;

    private final AppCompatAutoCompleteTextView f_acactv;

    public static int TIME_OUT = 30000;

    private boolean m_isRunning = true;

    public ATFindCurrentLocation(final TMapGpsManager gps, final TMapView tmapView, final RelativeLayout rlCover, final AppCompatAutoCompleteTextView acactv) {
        f_gps        = gps;
        f_tmapView  = tmapView;
        f_rlCover   = rlCover;
        f_acactv    = acactv;
    }

    @Override
    protected void onPreExecute() {
        /*
            GPS 열어줌,

            TMapGpsManager.NETWORK_PROVIDER는 데이터 즉 주변 기지국을 통해 위치를 파악하는거임
            TMapGpsManager.GPS_PROVIDER 는 GPS 즉 위성을 이용하여 위치 파악하는 거임

            집에서 작업해 실내에서는 GPS를 사용할수 없기에 NETWORK_PROVIDER로 해두었습니다

            그리고 현재 메서드 onPreExecute에서 Open해주고, onPostExecute에서 Close 해주는 이유는
            사용할때만 open-close 하지않고 계속 켜두면, 그만큼 배터리가 빨리 닳기 때문입니당
         */
        f_gps.setProvider(TMapGpsManager.NETWORK_PROVIDER);
        f_gps.OpenGps();

        /*
            진행 사항을 알아보기 위한 뷰임
            AysncTask를 실제 수행하는 doInbackground에서는 뷰를 업데이트 할수가 없음
            (그 이유는 뷰들은 MainThread에서 돌고 있기 때문에)

            하여 AsyncTask를 수행할떄는

            onPreExecute - doInBackground - onProgressUpdate - doInBackground - onPreExecute
            이 순서로 진행 돼는데
            onPreExecute, onPreExecute에서는 (뷰 작업을 포함한) 시작 준비 과정을 수행하고

            doInBackground에서는 실제 작업을
            doInBackground에서는 에서 뷰 작업이 필요하다면 publishProgress를 통해 onProgressUpdate를 호출하여 수행하여야 한다

         */
        ((TextView) f_rlCover.findViewById(R.id.activity_main_tv_cover_command)).setText("장소를 찾는 중입니다. 잠시만 기다려 주세요");
        ((TextView) f_rlCover.findViewById(R.id.activity_main_tv_cover_total)).setText((TIME_OUT / 1000) + " sec 로딩중..");
        f_rlCover.setVisibility(View.VISIBLE);
    }

    @Override
    protected AddressAndPoint doInBackground(Void... voids) {
        int elapased = 0;
        TMapPoint retval = null;

        // getLocation 을 통해 현재 위치를 받아옵니다
        // Non-Block이기 떄문에 처음에는 0, 0이 나올 경우가 많습니다
        // 한 몇초 지나야 제대로된 값이 나오므로 기다려 줍시다

        // 1. 요기서는 0.1초마다 getLocation 을 호출해주며
        // 2. elapased % 1000 == 0, 즉 1초이면 프로그레스를 업데이트 해주고
        // 3. TIME_OUT(30000) 30초동안 계속 getLocation 하다가
        // 정상결과가 나오면 그놈을 사용하고, 계속 0이나오면 실패 메시지를 출력합니다
        while((retval = f_gps.getLocation()).getLatitude() == 0) {
            try {
                if(elapased % 1000 == 0) {
                    publishProgress(elapased);
                }
                elapased += 100;
                Thread.sleep(100);  // msecond

                if(elapased >= TIME_OUT) {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*
            getLocation 은 위도, 경도 값만 줍니다

            그래서 이 위도 경도를 가지고 주소를 찾아야 합니다
         */
        final AddressAndPoint aap = new AddressAndPoint(retval.getLatitude(), retval.getLongitude());
        TMapData tmapdata = new TMapData();

        // convertGpsToAddress를 통해 getLocation으로 가져온 위도,경도를 주소로 변경 해줍니다
        tmapdata.convertGpsToAddress(retval.getLatitude(), retval.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
            @Override
            public void onConvertToGPSToAddress(String address) {
                aap.setAddress(address);
                m_isRunning = false;
            }
        });

        // convertGpsToAddress의 콜백이 호출될때까지 대기
        while(m_isRunning) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return aap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
//        super.onProgressUpdate(values);

        ((TextView) f_rlCover.findViewById(R.id.activity_main_tv_cover_current)).setText((values[0] / 1000) + " sec");
    }

    @Override
    protected void onPostExecute(AddressAndPoint aap) {
        if(aap != null) {

            /*
                정상적으로 위도, 경도 -> 주소까지 받아왔다면 해야할일이

                0. 지도의 위치를 현재위치로 설정해줌
                1. 주소를 입력창에 적어준다
                2. 마커를 생성, 업데이트 해준다
             */
            f_acactv.setText(aap.getAddress());

            f_tmapView.setLocationPoint(aap.getLongitude(), aap.getLatitude());
            f_tmapView.setCenterPoint(aap.getLongitude(), aap.getLatitude(), true);

            TMapMarkerItem tmapMarkerItem = new TMapMarkerItem();
            tmapMarkerItem.setTMapPoint(new TMapPoint(aap.getLatitude(), aap.getLongitude()));
            tmapMarkerItem.setVisible(TMapMarkerItem.VISIBLE);

            tmapMarkerItem.setCanShowCallout(true);
            tmapMarkerItem.setCalloutSubTitle(aap.getAddress());
            tmapMarkerItem.setAutoCalloutVisible(true);

            switch (f_acactv.getId()) {
                case R.id.activity_main_acactv_begin:
                    tmapMarkerItem.setCalloutTitle("시작지점");
                    f_tmapView.addMarkerItem("begin", tmapMarkerItem);
                    break;
                case R.id.activity_main_acactv_end:
                    tmapMarkerItem.setCalloutTitle("종료지점");
                    f_tmapView.addMarkerItem("end", tmapMarkerItem);
                    break;
            }
        } else {
            Toast.makeText(f_tmapView.getContext(), "현재위치를 획득에 실패하였습니다", Toast.LENGTH_LONG).show();
        }
        f_rlCover.setVisibility(View.GONE);
        f_gps.CloseGps();
    }
}

