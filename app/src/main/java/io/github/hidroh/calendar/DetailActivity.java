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
 * Created by kug00 on 2017-10-29.
 */

public class DetailActivity extends AppCompatActivity {
    //JSON 문자열을 저장할 문자열
    String Detaildata;
    String Introdata;
    //JSON에서 목록을 만들기위한 배열
    private static final String Detail_SID="contentid", Detail_Title = "title", Detail_address = "addr1", Detail_Image = "firstimage"
            ,Overview = "overview",Infocenter = "infocenter", Parking = "parking",Restdate = "restdate";

    //지역 정보를 담기위한 해쉬 리스트 선언
    ArrayList<HashMap<String, String>> Locla_S_ListHash;
    //받는 정보 분류
    int contentTypeId;
    String detail_contentId;
    TextView TextView_intro, TextView_Infocenter, TextView_parking, TextView_Restdate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_detail);

        //아이디 찾기
        TextView TextView_Title = (TextView)findViewById(R.id.detail_title);
        TextView TextView_addr = (TextView)findViewById(R.id.detail_addr);
        TextView_intro = (TextView)findViewById(R.id.detail_intro);
        TextView_Infocenter = (TextView)findViewById(R.id.TextView_Infocenter);
        TextView_parking = (TextView)findViewById(R.id.TextView_parking);
        TextView_Restdate = (TextView)findViewById(R.id.TextView_Restdate) ;

        //지역정보를 저장하기 위한
        Locla_S_ListHash = new ArrayList<HashMap<String, String>>();

        //정보 전달 받기
        Intent it = getIntent();

        //컨텐츠 ID 받기
        contentTypeId = it.getIntExtra("It_ContentTypeId",contentTypeId);
        //Log.d("ListView","contentTypeId:"+contentTypeId);


        //해쉬맵 가져오기
        HashMap<String,String> DetailHash;
        DetailHash = (HashMap<String,String>)it.getSerializableExtra("DetailHash");

        //Id 얻기
        detail_contentId = DetailHash.get(Detail_SID);
        //Log.d("ListView","contentId:"+detail_contentId);

        //타이틀 얻기
        String detail_title = DetailHash.get(Detail_Title);
        //Log.d("ListView","title:"+detail_title);
        TextView_Title.setText(detail_title);

        //주소 얻기
        String detail_addr = DetailHash.get(Detail_address);
        //Log.d("ListView","address:"+detail_addr);
        TextView_addr.setText(detail_addr);

        //이미지 얻기
        String detail_img = DetailHash.get(Detail_Image);
        //Log.d("ListView","address:"+detail_img);
        // 아이템 내 각 위젯에 데이터 반영, 피카소 이용해 url 주소 이미지 넣기
        Picasso.with(this)
                .load(detail_img)
                .into((ImageView)findViewById(R.id.detail_img));

        getData(CreateURL());
        //문의 및 안내 정보
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
                Detaildata=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void showList(){
        try {
            //전체 데이터 출력 Detaildata json으로 파싱 받은 데이터가 String 형식으로 되있다.
            //Log.d("Result","Detaildata 전체데이터출력 : "+Detaildata);

            //Json data -> JsonOject 변환
            JSONObject jsonObj = new JSONObject(Detaildata);
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
            TextView_intro.setText(Html.fromHtml(OverviewValue));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    protected String CreateURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=" + servicekey;
        String mid="&contentTypeId="+contentTypeId+"&contentId=" + detail_contentId;
        String last="&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y&_type=json";
        String data  = first  + mid  + last;
        return data;
    }

    //문의 및 소개 정보 가져오는 URL 생성
    protected String IntroURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey=" + servicekey;
        String mid="&contentTypeId="+contentTypeId+"&contentId=" + detail_contentId;
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
                Introdata=result;
                IntroShow();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void IntroShow(){
        try {
            //전체 데이터 출력 Detaildata json으로 파싱 받은 데이터가 String 형식으로 되있다.
            //Log.d("Result","Introdata 전체데이터출력 : "+Introdata);

            //Json data -> JsonOject 변환
            JSONObject jsonObj = new JSONObject(Introdata);
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
            if(jsonvalue.has(Infocenter))
                InfocenterValue = jsonvalue.getString(Infocenter);
            //Log.d("Result","Infocenter 결과"+InfocenterValue);
            TextView_Infocenter.setText(Html.fromHtml(InfocenterValue));

            //주차 시설 정보
            String ParkingValue = "주차시설 정보가 없습니다.";
            if(jsonvalue.has(Parking))
                ParkingValue = jsonvalue.getString(Parking);
            //Log.d("Result","Parking 결과"+ParkingValue);
            TextView_parking.setText(Html.fromHtml(ParkingValue));

            //휴일
            String RestdateValue = "휴일 정보가 없습니다.";
            if(jsonvalue.has(Restdate))
                RestdateValue = jsonvalue.getString(Restdate);
            //Log.d("Result","Restdate 결과"+RestdateValue);
            TextView_Restdate.setText(Html.fromHtml(RestdateValue));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

