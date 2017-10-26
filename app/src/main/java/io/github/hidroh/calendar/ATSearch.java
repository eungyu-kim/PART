package io.github.hidroh.calendar;

/**
 * Created by User on 2017-10-26.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
/**
 * Created by User on 2017-10-16.
 */

public class ATSearch  extends  AsyncTask<Void, Integer, ArrayList<TMapPOIItem>> {

    private final String f_input;
    private final TMapView f_tmapView;
    private final RelativeLayout f_rlCover;

    private final AppCompatAutoCompleteTextView f_acactv;

    private boolean m_isRunning = true;

    public ATSearch(final String input, final TMapView tmapView, final RelativeLayout rlCover, final AppCompatAutoCompleteTextView acactv) {
        f_input     = input;
        f_rlCover   = rlCover;
        f_tmapView  = tmapView;
        f_acactv = acactv; // 최종 검색 단어
    }

    @Override
    protected void onPreExecute() { // 검색단어 돋보기그림으로 검색할때, 로딩시 나오는 text
        ((TextView) f_rlCover.findViewById(R.id.activity_main_tv_cover_command)).setText("Search : " + f_input);
        ((RelativeLayout) f_rlCover.findViewById(R.id.activity_main_rl_cover_duration)).setVisibility(View.GONE);
        // 커버 켜줌
        f_rlCover.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<TMapPOIItem> doInBackground(Void... voids) {
        final ArrayList<TMapPOIItem> retArrayList = new ArrayList<TMapPOIItem>();
        /*
            findTitlePOI 를 통해 입력된 키워드관련 을 검색합니다
            다른 AysncTask들과 마찬가지로 콜백으로 받습니다
         */
        TMapData tmapData = new TMapData();
        tmapData.findTitlePOI(f_input, new TMapData.FindTitlePOIListenerCallback() {
            @Override
            public void onFindTitlePOI(ArrayList<TMapPOIItem> arrayList) {
                retArrayList.addAll(arrayList);
                m_isRunning = false;
            }
        });

        while(m_isRunning) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retArrayList;
    }

    // 자동검색한 관련된 검색어 리스트 목록으로 보여준다.
    @Override
    protected void onPostExecute(final ArrayList<TMapPOIItem> tmapPoiArrayList) {
        f_rlCover.setVisibility(View.GONE);
        ((RelativeLayout) f_rlCover.findViewById(R.id.activity_main_rl_cover_duration)).setVisibility(View.VISIBLE);

        final ArrayList<String> tmapPoiNameArrayList = new ArrayList<String>();
        for(final TMapPOIItem tmapPoi : tmapPoiArrayList) {
            tmapPoiNameArrayList.add(tmapPoi.getPOIName());
        }
        /*키워드를 통해 받은결과는 0개 이상입니다*/
        if(tmapPoiNameArrayList.size() == 0) {
            // 0개면 toast 메시지 출력하구 그냥 끄기
            Toast.makeText(f_tmapView.getContext(), "검색 결과가 없습니다", Toast.LENGTH_LONG).show();
        } else {
            // 0 개 이상이면
            // 결과목록을 다이얼로그로 보여준 후
            final AlertDialog.Builder builder = new AlertDialog.Builder(f_tmapView.getContext());
            builder.setTitle("")
                    .setItems(tmapPoiNameArrayList.toArray(new String[tmapPoiNameArrayList.size()]), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            /*
                                선택된 결과로
                                0. 지도의 위치를 현재위치로 설정해줌
                                1. 주소를 창에 적어준다
                                2. 마커를 생성, 업데이트 해준다
                            */
                            final TMapPOIItem tmapPoiItem = tmapPoiArrayList.get(which); // tmapPoiItem 변수는 관련검색어 list 받아온거 쓰기
                            f_acactv.setText(tmapPoiItem.getPOIName());
                            f_tmapView.setLocationPoint(tmapPoiItem.getPOIPoint().getLongitude(), tmapPoiItem.getPOIPoint().getLatitude());
                            f_tmapView.setCenterPoint(tmapPoiItem.getPOIPoint().getLongitude(), tmapPoiItem.getPOIPoint().getLatitude(), true);

                            TMapMarkerItem tmapMarkerItem = new TMapMarkerItem();
                            tmapMarkerItem.setTMapPoint(tmapPoiItem.getPOIPoint());
                            tmapMarkerItem.setVisible(TMapMarkerItem.VISIBLE);

                            tmapMarkerItem.setCanShowCallout(true);
                            tmapMarkerItem.setCalloutSubTitle(tmapPoiItem.getPOIName());
                            tmapMarkerItem.setAutoCalloutVisible(true);

                            switch (f_acactv.getId()) {
                                case R.id.activity_main_acactv_begin: tmapMarkerItem.setCalloutTitle("시작지점");  f_tmapView.addMarkerItem("begin", tmapMarkerItem);  break;
                                case R.id.activity_main_acactv_end:    tmapMarkerItem.setCalloutTitle("종료지점");  f_tmapView.addMarkerItem("end", tmapMarkerItem);    break;
                            }
                        }
                    });
            builder.show();
        }
    }
}
