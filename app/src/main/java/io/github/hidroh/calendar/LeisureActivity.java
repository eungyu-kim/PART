package io.github.hidroh.calendar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kug00 on 2017-11-01.
 */

public class LeisureActivity extends AppCompatActivity {

    TextView leisure_title, leisure_addr, leisure_intro, TextView_leisure_Infocenter, TextView_leisure_parking, TextView_Restdateleports,
            TextView_checkintime, TextView_checkouttime, openperiod;

    //숙박 정보 소개 가져오기
    String leisure_Intro_Data, leisure_Data;
    //숙박 정보를 담기위한 해쉬 리스트 선언
    ArrayList<HashMap<String, String>> leisure_S_ListHash;
    //받는 정보 분류
    int contentTypeId;
    //JSON에서 목록을 만들기위한 배열
    private static final String Detail_SID="contentid", Detail_Title = "title", Detail_address = "addr1", Detail_Image = "firstimage"
            ,Overview = "overview",Infocenter = "infocenterleports", Parking = "parkingleports",Restdateleports = "restdateleports"
            ,Usetimeleports = "usetimeleports",Openperiod = "openperiod";

    String leisure_contentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leisure);

        //아이디 찾기
        leisure_title = (TextView)findViewById(R.id.leisure_title);
        leisure_addr = (TextView)findViewById(R.id.leisure_addr);
        leisure_intro = (TextView)findViewById(R.id.leisure_intro);
        TextView_leisure_Infocenter = (TextView)findViewById(R.id.TextView_leisure_Infocenter);
        TextView_leisure_parking = (TextView)findViewById(R.id.TextView_leisure_parking);
        TextView_Restdateleports = (TextView)findViewById(R.id.TextView_Restdateleports);
        TextView_checkintime = (TextView)findViewById(R.id.TextView_checkintime);
        TextView_checkouttime = (TextView)findViewById(R.id.TextView_checkouttime);
        openperiod = (TextView)findViewById(R.id.openperiod);

        //지역정보를 저장하기 위한
        leisure_S_ListHash = new ArrayList<HashMap<String, String>>();

        //정보 전달 받기
        Intent it = getIntent();

        //컨텐츠 ID 받기
        contentTypeId = it.getIntExtra("It_ContentTypeId",contentTypeId);
        //Log.d("ListView","contentTypeId:"+contentTypeId);


        //해쉬맵 가져오기
        HashMap<String,String> DetailHash;
        DetailHash = (HashMap<String,String>)it.getSerializableExtra("DetailHash");

        //Id 얻기
        leisure_contentId = DetailHash.get(Detail_SID);
        //Log.d("ListView","Food_contentId:"+Food_contentId);

        //타이틀 얻기
        String Food_title = DetailHash.get(Detail_Title);
        //Log.d("ListView","Food_title:"+Food_title);
        leisure_title.setText(Food_title);

        //주소 얻기
        String Food_addr = DetailHash.get(Detail_address);
        //Log.d("ListView","address:"+Food_addr);
        leisure_addr.setText(Food_addr);

        //이미지 얻기
        String detail_img = DetailHash.get(Detail_Image);
        //Log.d("ListView","address:"+detail_img);
        // 아이템 내 각 위젯에 데이터 반영, 피카소 이용해 url 주소 이미지 넣기
        Picasso.with(this)
                .load(detail_img)
                .into((ImageView)findViewById(R.id.leisure_img));

        getData(LeisueIntroURL());
        IntroData(IntroURL());
    }

    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while((json = bufferedReader.readLine())!= null) {
                        sb.append(json+"\n");
                    }
                    return sb.toString().trim();
                } catch(IOException e){
                    return "다운로드 실패";
                }
            }
            @Override
            protected void onPostExecute(String result) {
                leisure_Intro_Data=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void showList(){
        try {
            //전체 데이터 출력 Detaildata json으로 파싱 받은 데이터가 String 형식으로 되있다.
            //Log.d("Result","Detaildata 전체데이터출력 : "+Food_Intro_Data);

            //Json data -> JsonOject 변환
            JSONObject jsonObj = new JSONObject(leisure_Intro_Data);
            //JsonObject -> 하위 JsonObject Get
            String response = jsonObj.getString("response");
            //Log.d("Result","response 결과"+response);

            JSONObject Response = new JSONObject(response);
            String body = Response.getString("body");
            //Log.d("Result","body 결과"+body);

            JSONObject Body = new JSONObject(body);
            String items = Body.getString("items");
            //Log.d("Result","items 결과"+items);

            JSONObject Item = new JSONObject(items);
            String item = Item.getString("item");
            //Log.d("Result","item 결과"+item);

            JSONObject jsonvalue = new JSONObject(item);
            //존재하지 않는경우 저장하지 않는다.
            String OverviewValue = "소개가 없습니다.";
            if(jsonvalue.has(Overview))
                OverviewValue = jsonvalue.getString(Overview);
            //Log.d("Result","Overview 결과"+OverviewValue);
            leisure_intro.setText(Html.fromHtml(OverviewValue));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    protected String LeisueIntroURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=" + servicekey;
        String mid="&contentTypeId="+contentTypeId+"&contentId=" + leisure_contentId;
        String last="&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y&_type=json";
        String data  = first  + mid  + last;
        return data;
    }

    //문의 및 소개 정보 가져오는 URL 생성
    protected String IntroURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey=" + servicekey;
        String mid="&contentTypeId="+contentTypeId+"&contentId=" + leisure_contentId;
        String last="&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&introYN=Y&_type=json";
        String data  = first  + mid  + last;
        return data;
    }

    //문의 및 안내 정보 가져오기
    public void IntroData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while((json = bufferedReader.readLine())!= null) {
                        sb.append(json+"\n");
                    }
                    return sb.toString().trim();
                } catch(IOException e){
                    return "다운로드 실패";
                }
            }
            @Override
            protected void onPostExecute(String result) {
                leisure_Data=result;
                IntroShow();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void IntroShow() {
        try {
            //전체 데이터 출력 Detaildata json으로 파싱 받은 데이터가 String 형식으로 되있다.
            //Log.d("Result","Introdata 전체데이터출력 : "+Introdata);

            //Json data -> JsonOject 변환
            JSONObject jsonObj = new JSONObject(leisure_Data);
            //JsonObject -> 하위 JsonObject Get
            String response = jsonObj.getString("response");
            //Log.d("Result","response 결과"+response);

            JSONObject Response = new JSONObject(response);
            String body = Response.getString("body");
            //Log.d("Result","body 결과"+body);

            JSONObject Body = new JSONObject(body);
            String items = Body.getString("items");
            //Log.d("Result","items 결과"+items);

            JSONObject Item = new JSONObject(items);
            String item = Item.getString("item");
            //Log.d("Result","item 결과"+item);

            JSONObject jsonvalue = new JSONObject(item);
            //문의 안내 정보
            String InfocenterValue = "문의 및 안내 정보가 없습니다.";
            if (jsonvalue.has(Infocenter))
                InfocenterValue = jsonvalue.getString(Infocenter);
            Log.d("Result","Infocenter 결과"+InfocenterValue);
            if (!InfocenterValue.equals(""))
                TextView_leisure_Infocenter.setText(Html.fromHtml(InfocenterValue));

            //주차 시설 정보
            String ParkingValue = "주차시설 정보가 없습니다.";
            if (jsonvalue.has(Parking))
                ParkingValue = jsonvalue.getString(Parking);
            Log.d("Result","Parking 결과"+ParkingValue);
            if (!ParkingValue.equals(""))
                TextView_leisure_parking.setText(Html.fromHtml(ParkingValue));

            //휴일 정보
            String RestdateleportsValue = "휴일 정보가 없습니다.";
            if (jsonvalue.has(Restdateleports))
                RestdateleportsValue = jsonvalue.getString(Restdateleports);
            Log.d("Result","Parking 결과"+RestdateleportsValue);
            if (!RestdateleportsValue.equals(""))
                TextView_Restdateleports.setText(Html.fromHtml(RestdateleportsValue));

            //이용 시간 정보
            String UsetimeleportsValue = "이용 시간 정보가 없습니다.";
            if (jsonvalue.has(Usetimeleports))
                UsetimeleportsValue = jsonvalue.getString(Usetimeleports);
            Log.d("Result","Parking 결과"+RestdateleportsValue);
            if (!UsetimeleportsValue.equals(""))
                TextView_Restdateleports.setText(Html.fromHtml(UsetimeleportsValue));

            //개장 기간 정보
            String OpenperiodsValue = "개장 기간 정보가 없습니다.";
            if (jsonvalue.has(Openperiod))
                OpenperiodsValue = jsonvalue.getString(Openperiod);
            Log.d("Result","Parking 결과"+RestdateleportsValue);
            if (!OpenperiodsValue.equals(""))
                openperiod.setText(Html.fromHtml(OpenperiodsValue));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
