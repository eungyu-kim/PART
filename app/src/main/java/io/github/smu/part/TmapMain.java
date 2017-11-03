package io.github.smu.part;

/**
 * Created by User on 2017-10-26.
 */
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2017-10-16.
 */

public class TmapMain extends AppCompatActivity implements View.OnClickListener {
    TMapPoint firstmapcount; // 대중교통이용시 출발정류장 좌표
    TMapPoint listmapcount; // 대중교통이용시 마지막정류장 좌표
    Double startlat,startlng; // 경로조회시 출발 지점
    Double endlat,endlng; // 경로조회시 도착지점
    int subwayType = 0;
    JSONObject jsonObject; // json 파싱 객체생성
    String routedetail = ""; // 세부적인 경로 나올 데이터
    public static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 255; // 첫 출발지는 서울역

    private TMapView m_tmapView = null;

    private AppCompatAutoCompleteTextView m_acactvBegin = null;
    private ImageButton m_ibtnBeginCurrent = null; // 현재위치를 시작점으로
    private ImageButton m_ibtnBeginSearch = null; // 검색한 위치를 시작점으로

    private AppCompatAutoCompleteTextView m_acactvEnd = null;
    private ImageButton m_ibtnEndCurrent = null; // 현재위치를 종료점으로
    private ImageButton m_ibtnEndSearch = null; // 검색한 위치를 종료점으로

    private Button m_btnPathSearch = null; // 경로검색 버튼
    private RelativeLayout m_rlCover = null;
    private TMapGpsManager m_gps = null;
    ListView listView;
    ArrayList<String> list; // listView에 연결할 모델 객체
    ArrayAdapter<String>adapter;


    // dropdownlist사용해서 관련검색어 handler만들기
    // f_autoComleteHandler변수에는 message가 들어가는데, msg에는 what이 시작위치, obj에는 검색어 관련단어들이 배열로 저장되어있다.
    private final Handler f_autoCompleteHandler = new Handler() {  // 관련 검색어 자동완성해주기 기능변수 f_autoCompleteHandler
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) { // 어디에 검색을 하느냐에 따라서 달라진다. switch로 구분하기
                case R.id.activity_main_acactv_begin: // 출발지 검색에 message가 있을 시
                    // setAdapter(dropline, 관련검색어를저장한 배열변수 obj)
                    m_acactvBegin.setAdapter(new ArrayAdapter<String>(TmapMain.this, android.R.layout.simple_dropdown_item_1line, (List<String>) msg.obj));
                    break;

                case R.id.activity_main_acactv_end:  // 종료지 검색에 message가 있을 시
                    m_acactvEnd.setAdapter(new ArrayAdapter<String>(TmapMain.this, android.R.layout.simple_dropdown_item_1line, (List<String>) msg.obj));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmapxml);

        ImageButton Tmap_Back = (ImageButton)findViewById(R.id.tmap_back);

        Tmap_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TmapMain.this, MainActivity.class);
                startActivity(it);
                finish();
            }
        });

        // ACCESS_FINE_LOCATION 퍼미션이 있는지 확인한다
        // 롤리팝 이후로는 ACCESS_FINE_LOCATION은 코드상으로 확인 해야함
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // 권한 없으면 권한사용할지 말지 물어봄
            // 수락 거부에 대한 결과는 onRequestPermissionsResult에서 확인함


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ACCESS_FINE_LOCATION);
        } else {

            // 권한 있으면 postOnCreate 수행함
            postOnCreate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 사용자가 ACCESS_FINE_LOCATION 사용 수락하면 그냥 postOnCreate 수행함
                    postOnCreate();
                } else {
                    // 사용자가 ACCESS_FINE_LOCATION 사용 거부하면 그냥 끔
                    finish();
                }
                break;
        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void postOnCreate() {
        /*
            필요한 뷰들 로드
         */
        m_tmapView = (TMapView) findViewById(R.id.activity_main_tmv); // mapview 화면

        m_rlCover = (RelativeLayout) findViewById(R.id.activity_main_rl_cover); // 까만 화면

        m_acactvBegin = (AppCompatAutoCompleteTextView) findViewById(R.id.activity_main_acactv_begin); // 출발지 검색창
        m_ibtnBeginCurrent = (ImageButton) findViewById(R.id.activity_main_ibtn_begin_current); // 출발지 현재위치로
        m_ibtnBeginSearch = (ImageButton) findViewById(R.id.activity_main_ibtn_begin_search); // 출발지 검색하기

        m_acactvEnd = (AppCompatAutoCompleteTextView) findViewById(R.id.activity_main_acactv_end);  // 도착지 검색창
        m_ibtnEndCurrent = (ImageButton) findViewById(R.id.activity_main_ibtn_end_current); // 도착지 현재위치로
        m_ibtnEndSearch = (ImageButton) findViewById(R.id.activity_main_ibtn_end_search); // 도착지 검색하기

        m_btnPathSearch = (Button) findViewById(R.id.activity_main_btn_path_search); // 경로검색하기 버튼

        /*
            리스너들 등록
         */
        m_ibtnBeginCurrent.setOnClickListener(this);
        m_ibtnBeginSearch.setOnClickListener(this);
        m_ibtnEndCurrent.setOnClickListener(this);
        m_ibtnEndSearch.setOnClickListener(this);
        m_btnPathSearch.setOnClickListener(this);

        /*
            TMapView 초기화 (pdf파일 참조)
         */
        m_tmapView.setSKPMapApiKey("9e1b7eb3-04ac-38ad-8101-fe40b5d34884"); //apikey 받기
        m_tmapView.setCompassMode(true);
        m_tmapView.setIconVisibility(true);
        m_tmapView.setZoomLevel(15);
        m_tmapView.setMapType(TMapView.MAPTYPE_STANDARD);
        m_tmapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        m_tmapView.setTrackingMode(true);
        m_tmapView.setSightVisible(true);

        /*
            GPS 초기화
         */
        m_gps = new TMapGpsManager(this);
        m_gps.setMinTime(1000);
        m_gps.setMinDistance(5);

        /*
            Auto Complete를 위한 TextWatcher 추가
         */
        m_acactvBegin.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable editable) {}
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence != null) {

                    final String input = charSequence.toString().trim();
                    if (!input.equals("")) { // 검색어를 쓰면
                        // autoComplete를 통해 키워드 관련 목록을 받아온후 (검색어 관련어들 자동생성)
                        TMapData tmapData = new TMapData();
                        tmapData.autoComplete(input, new TMapData.AutoCompleteListenerCallback() {
                            @Override
                            public void onAutoComplete(ArrayList<String> arrayList) { // arraylist에 관련검색어들 저장하기
                                // 콜백으로 결과가 받아졌으며, 하나 이상 관련검색어가 있으면
                                if(arrayList.size() > 0) {
                                    // 핸들러를 통해 AutoCompleteTextView 업데이트
                                    final Message message = f_autoCompleteHandler.obtainMessage(); // f_autoCompleteHandler변수에 자동완성 기능 넘기기
                                    message.what = m_acactvBegin.getId(); // 시작위치에서 선택한 단어의 id를 얻어서 what에 저장.
                                    message.obj = arrayList; // 관련검색어가 들어있는 배열변수 obj
                                    f_autoCompleteHandler.sendMessage(message);
                                }
                            }
                        });
                    }
                }
            }
        });

        // 위의 m_acactvBegin과 동일
        m_acactvEnd.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable editable) {}
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence != null) {
                    final String input = charSequence.toString().trim();
                    if (!input.equals("")) {
                        TMapData tmapData = new TMapData();
                        tmapData.autoComplete(input, new TMapData.AutoCompleteListenerCallback() {
                            @Override
                            public void onAutoComplete(ArrayList<String> arrayList) {
                                if(arrayList.size() > 0) {
                                    final Message message = f_autoCompleteHandler.obtainMessage();
                                    message.what = m_acactvEnd.getId();
                                    message.obj = arrayList;
                                    f_autoCompleteHandler.sendMessage(message);
                                }
                            }
                        });
                    }
                }
            }
        });

        // 앱 첫 시작에 현재위치를 받아와, 시작위치로 설정(m_acactvBegin) 해준다
        new ATFindCurrentLocation(m_gps, m_tmapView, m_rlCover, m_acactvBegin).execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 현재위치 버튼을 누르면 ATFindCurrentLocation AsyncTask 수행
            case R.id.activity_main_ibtn_begin_current:
                new ATFindCurrentLocation(m_gps, m_tmapView, m_rlCover, m_acactvBegin).execute();   break;
            case R.id.activity_main_ibtn_end_current:
                new ATFindCurrentLocation(m_gps, m_tmapView, m_rlCover, m_acactvEnd).execute();      break;

            // 검색 버튼(돋보기 모양) 누르면 ATSearch AsyncTask 수행
            case R.id.activity_main_ibtn_begin_search:
            case R.id.activity_main_ibtn_end_search:
                // 현재 AppCompatAutoCompleteTextView 구분 후
                final AppCompatAutoCompleteTextView acactv;
                switch (view.getId()) {
                    case R.id.activity_main_ibtn_begin_search: // 시작위치 검색
                        acactv = m_acactvBegin;  // 검색하는 곳에서 최종 선택한 검색어를 acactv변수에 저장
                        break;
                    case R.id.activity_main_ibtn_end_search: // 종료위치 검색
                        acactv = m_acactvEnd;
                        break;
                    default:
                        return;
                }

                // ATSearch AsyncTask 수행
                final String input = acactv.getText().toString().trim(); // 최종검색선택한 단어 input변수 string형으로 저장하기
                if(!input.equals("")) { // 빈칸이 아니고 단어가 검색되어있으면 실행하기
                    new ATSearch(input, m_tmapView, m_rlCover, acactv).execute();  // 최종 검색한 단어로 ATSearch.java 들어가기
                    // 키보드는 닫아줌
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(acactv.getWindowToken(), 0);
                }
                break;


            case R.id.activity_main_btn_path_search: // 경로검색
                try {
                    // 맵에서 마커를 가져와서
                    final TMapMarkerItem beginMarker = m_tmapView.getMarkerItemFromID("begin");
                    final TMapMarkerItem endMarker = m_tmapView.getMarkerItemFromID("end");

                    // 시작, 도착 두 마커 다 있으면
                    if((beginMarker != null) && (endMarker != null)) {

                        // 다이얼로그를 띄워 자동차 경로, 보행자경로를 선택하게 해줌
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("")
                                .setItems(new CharSequence[] {"자동차 경로", "보행자 경로",  "버스 경로", "지하철 경로"}, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

// 선택된 경로에 따라 ATFindPath를 수행함
                                        switch (which) {
                                            case 0: new ATFindPath(TMapData.TMapPathType.CAR_PATH, m_tmapView, m_rlCover).execute(beginMarker.getTMapPoint(), endMarker.getTMapPoint()); break;
                                            case 1: new ATFindPath(TMapData.TMapPathType.PEDESTRIAN_PATH, m_tmapView, m_rlCover).execute(beginMarker.getTMapPoint(), endMarker.getTMapPoint()); break;

// <!_-------경로 추가지점 ----------!>
                                            case 2: // 버스
// 출발좌표, 도착좌표,교통타입
                                                startlat = beginMarker.getTMapPoint().getLatitude();
                                                startlng = beginMarker.getTMapPoint().getLongitude();
                                                endlat = endMarker.getTMapPoint().getLatitude();
                                                endlng = endMarker.getTMapPoint().getLongitude();
                                                OdsayAPi(startlat,startlng,endlat,endlng,2);
                                                break;
                                            case 3 : // 지하철
                                                startlat = beginMarker.getTMapPoint().getLatitude();
                                                startlng = beginMarker.getTMapPoint().getLongitude();
                                                endlat = endMarker.getTMapPoint().getLatitude();
                                                endlng = endMarker.getTMapPoint().getLongitude();
                                                OdsayAPi(startlat,startlng,endlat,endlng,1);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                });
                        builder.show();
                    } else {
                        Toast.makeText(this, "먼저 시작 / 종료 지점을 선택해주세요", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    // 이동방법 검색
    private void OdsayAPi(Double startlat, Double startlng, Double endlat, Double endlng, int i) {
        subwayType = i; // 이동방법 1 지하철/ /
        ODsayService odsayService;
        odsayService = ODsayService.init(getApplicationContext(), "5Vq83w/lRS6BYBFCS/QM77UFYnTlqRyHkJGe87dbalw");
        odsayService.setReadTimeout(3000);
        odsayService.setConnectionTimeout(3000);
// 서버 통신
        odsayService.requestSearchPubTransPath(Double.toString(startlng), Double.toString(startlat), Double.toString(endlng),Double.toString(endlat), "0", "0", "0", onResultCallbackListener);
    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        // 호출 성공시 데이터 들어옴옴
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            jsonObject = oDsayData.getJson();
            transportation(jsonObject);
        }
        // 에러 표출시 데이터
        @Override
        public void onError(int i, String errorMessage, API api) {
            Log.i("SearchAPi",errorMessage);
        }
    };

    // 이동방법 파싱
    private void transportation(JSONObject jsonObject) {
        try{
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray pathArray = result.getJSONArray("path");
// pathArray 안의 경로 갯수
            int pathArraycount = pathArray.length();
            for(int a = 0; a<pathArraycount; a++) {
                JSONObject pathArrayDetailOBJ = pathArray.getJSONObject(a);
// 경로 타입 1 지하철 2 버스 3도보
                int pathType = pathArrayDetailOBJ.getInt("pathType");
                if( pathType == subwayType){
                    JSONObject infoOBJ = pathArrayDetailOBJ.getJSONObject("info");
                    int totalWalk = infoOBJ.getInt("totalWalk"); // 총 도보 이동거리
                    int payment = infoOBJ.getInt("payment"); // 요금
                    int totalTime = infoOBJ.getInt("totalTime"); // 소요시간
                    String mapObj = infoOBJ.getString("mapObj"); // 경로 디테일 조회 아이디
                    String firstStartStation = infoOBJ.getString("firstStartStation"); // 출발 정거장
                    String lastEndStation = infoOBJ.getString("lastEndStation"); // 도착 정거장

// 세부경로 디테일
                    JSONArray subPathArray = pathArrayDetailOBJ.getJSONArray("subPath");
                    int subPathArraycount = subPathArray.length();
// 반환 데이터 스트링으로

                    routedetail=""; // 초기화
                    for(int b = 0; b<subPathArraycount; b++){
                        JSONObject subPathOBJ = subPathArray.getJSONObject(b);
                        int Type = subPathOBJ.getInt("trafficType"); // 이동방법
                        switch (Type){
                            case 1:
                                routedetail += "\n지하철\n-> ";
                                break;
                            case 2:
                                routedetail += "\n버스\n-> ";
                                break;
                            default:
                                routedetail += "\n도보\n-> ";
                                break;
                        }
// 버스 또는 지하철 이동시에만
                        if(Type == 1 || Type ==2){
                            String startName = subPathOBJ.getString("startName"); // 승차정류장
                            routedetail += startName+" 에서 ";
                            String endName = subPathOBJ.getString("endName"); // 하차정류장
                            routedetail += endName+" ";
// 버스및 지하철 정보 가져옴 (정보가 많으므로 array로 가져오기)
                            JSONArray laneObj = subPathOBJ.getJSONArray("lane");
                            if(Type == 1 ){ // 지하철
                                String subwayName = laneObj.getJSONObject(0).getString("name"); // 지하철 정보(몇호선)
                               // String subwaycode = laneObj.getJSONObject(0).getString("subwayCode"); // 지하철 노선번호
                                routedetail += subwayName + " 지하철 탑승 ";
                            }
                            if(Type == 2 ) { // 버스..
                                String busNo = laneObj.getJSONObject(0).getString("busNo"); // 버스번호정보
                                String busroute = " ["+busNo+ "] 번 버스 탑승 ";
                                routedetail += busroute;
                            }
                        }
                        int distance = subPathOBJ.getInt("distance"); // 이동길이
                        routedetail += Integer.toString(distance)+"m 이동 (";
                        int sectionTime = subPathOBJ.getInt("sectionTime"); // 이동시간
                        routedetail += Integer.toString(sectionTime)+"분 소요)\n";
                        totalTime += sectionTime ;
////////////////////////////////////////////////////////////addlist 넣기!!! 한줄마다 listview설정하기
                    } // 세부경로 종료

                    routedetail += "\n\n총 " + Integer.toString(totalTime) + "분 소요됩니다.  감사합니다^^\n---------------------------------------------------------------- \n\n" ;
// api 경로 좌표 요청
                    Dialogview();
                    OdsayAPiroute(mapObj);
// 화면에 버스 및 지하철 경로 출력

                    break;
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //세부경로 출력
    private void Dialogview() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //닫기
            }
        });
        alert.setMessage(routedetail);
        alert.show();
    }

    // 경로 디테일
    private void OdsayAPiroute(String mapObj) {
        ODsayService odsayService;
        odsayService = ODsayService.init(getApplicationContext(), "5Vq83w/lRS6BYBFCS/QM77UFYnTlqRyHkJGe87dbalw");
        odsayService.setReadTimeout(3000);
        odsayService.setConnectionTimeout(3000);
// 서버 통신
        odsayService.requestLoadLane("0:0@"+mapObj, onResultCallbackListener1);
    }

    private OnResultCallbackListener onResultCallbackListener1 = new OnResultCallbackListener() {
        // 호출 성공시 데이터 들어옴옴
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            jsonObject = oDsayData.getJson();
            transportationroute(jsonObject);
        }
        // 에러 표출시 데이터
        @Override
        public void onError(int i, String errorMessage, API api) {
            Log.i("SearchAPi",errorMessage);
        }
    };
    // 경로 파싱
    private void transportationroute(JSONObject jsonObject) {
        TMapPolyLine map = new TMapPolyLine();
        try{
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray laneArray = result.getJSONArray("lane");
            JSONArray sectionArray = laneArray.getJSONObject(0).getJSONArray("section");
            JSONArray graphPosArray = sectionArray.getJSONObject(0).getJSONArray("graphPos");
            int graphPoscount = graphPosArray.length();
            for(int a=0; a<graphPoscount; a++){
                Double lat = graphPosArray.getJSONObject(a).getDouble("y"); // 37.456633193982924
                Double lng = graphPosArray.getJSONObject(a).getDouble("x"); // 126.70482242067823
                if(a==0){
                    firstmapcount = new TMapPoint(lat, lng); // 출발 정류장 좌표
                }
                if(a==graphPoscount-1) {
                    listmapcount = new TMapPoint(lat, lng); // 도착 정류장 좌표
                }
                TMapPoint mapcount = new TMapPoint(lat, lng);
                map.addLinePoint(mapcount);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        map.setLineColor(Color.BLUE);
        map.setLineWidth(2);
        m_tmapView.addTMapPolyLine("path",map);
    }
}