package io.github.smu.part;

/**
 * Created by User on 2017-10-26.
 */
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RelativeLayout;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

/**
 * Created by User on 2017-10-16.
 */

public class ATFindPath extends AsyncTask<TMapPoint, Integer, Void> {

    private final TMapData.TMapPathType f_pathType;
    private final TMapView f_tmapView;
    private final RelativeLayout f_rlCover;

    private TMapPolyLine m_tmapPolyLine = null;

    private boolean m_isRunning = true;

    public ATFindPath(final TMapData.TMapPathType pathType, final TMapView tmapView, final RelativeLayout rlCover) {
        f_rlCover = rlCover; // 반투명 검은색 보호 화면
        f_tmapView = tmapView; // 지도
        f_pathType = pathType;
    }

    // 커버 켜줘서 다른 작업 추가 수행 막음
    @Override
    protected void onPreExecute() {
        f_rlCover.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(TMapPoint... tmapPoints) {

        TMapData tmapData = new TMapData();

        // findPathDataWithType 호출하여 경로를 콜백으로 받아옴
        tmapData.findPathDataWithType(f_pathType, tmapPoints[0], tmapPoints[1], new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) { // pdf 87페이지.. 출발,목적지 값으로 경로탐색 요청하기
                m_tmapPolyLine = tMapPolyLine;
                m_isRunning = false;
            }
        });

        // m_isRunning이 false가 될때까지, 즉 위의 콜백이 불릴때까지 asyncTask를 켜둠
        while(m_isRunning) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void vvoid) {
        // findPathDataWithType로 받아온 TMapPolyLine을 맵에 그려줌
        if(m_tmapPolyLine != null) {
            m_tmapPolyLine.setLineColor(Color.BLUE);
            m_tmapPolyLine.setLineWidth(2);
            f_tmapView.addTMapPolyLine("path", m_tmapPolyLine); // 지도에 경로라인 blue색으로 표시
        }

        // 커버 꺼서 다른 작업 추가 수행 막았던거 풀어줌
        f_rlCover.setVisibility(View.GONE);
    }
}
