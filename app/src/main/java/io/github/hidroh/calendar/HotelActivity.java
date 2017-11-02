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

public class HotelActivity extends AppCompatActivity {

    TextView hotel_title, hotel_addr, hotel_intro, TextView_Hotel_Infocenter, TextView_Hotel_Parking, TextView_Hotel_Roomtype, TextView_checkintime, TextView_checkouttime;

    //숙박 정보 소개 가져오기
    String Hotel_Intro_Data, Hotel_Data;
    //숙박 정보를 담기위한 해쉬 리스트 선언
    ArrayList<HashMap<String, String>> Hotel_S_ListHash;
    //받는 정보 분류
    int contentTypeId;
    //JSON에서 목록을 만들기위한 배열
    private static final String Detail_SID="contentid", Detail_Title = "title", Detail_address = "addr1", Detail_Image = "firstimage"
            ,Overview = "overview",Infocenter = "infocenterlodging", Parking = "parkinglodging", Roomtype = "roomtype", Checkintime ="checkintime",
            Checkouttime = "checkouttime";

    String Hotel_contentId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        //아이디 찾기
        hotel_title = (TextView)findViewById(R.id.hotel_title);
        hotel_addr = (TextView)findViewById(R.id.hotel_addr);
        hotel_intro = (TextView)findViewById(R.id.hotel_intro);
        TextView_Hotel_Infocenter = (TextView)findViewById(R.id.TextView_Hotel_Infocenter);
        TextView_Hotel_Parking = (TextView)findViewById(R.id.TextView_Hotel_Parking);
        TextView_Hotel_Roomtype = (TextView)findViewById(R.id.TextView_Hotel_Roomtype);
        TextView_checkintime = (TextView)findViewById(R.id.TextView_checkintime);
        TextView_checkouttime = (TextView)findViewById(R.id.TextView_checkouttime);




        //지역정보를 저장하기 위한
        Hotel_S_ListHash = new ArrayList<HashMap<String, String>>();

        //정보 전달 받기
        Intent it = getIntent();

        //컨텐츠 ID 받기
        contentTypeId = it.getIntExtra("It_ContentTypeId",contentTypeId);
        //Log.d("ListView","contentTypeId:"+contentTypeId);


        //해쉬맵 가져오기
        HashMap<String,String> DetailHash;
        DetailHash = (HashMap<String,String>)it.getSerializableExtra("DetailHash");

        //Id 얻기
        Hotel_contentId = DetailHash.get(Detail_SID);
        //Log.d("ListView","Food_contentId:"+Food_contentId);

        //타이틀 얻기
        String Food_title = DetailHash.get(Detail_Title);
        //Log.d("ListView","Food_title:"+Food_title);
        hotel_title.setText(Food_title);

        //주소 얻기
        String Food_addr = DetailHash.get(Detail_address);
        //Log.d("ListView","address:"+Food_addr);
        hotel_addr.setText(Food_addr);

        //이미지 얻기
        String detail_img = DetailHash.get(Detail_Image);
        //Log.d("ListView","address:"+detail_img);
        // 아이템 내 각 위젯에 데이터 반영, 피카소 이용해 url 주소 이미지 넣기
        Picasso.with(this)
                .load(detail_img)
                .into((ImageView)findViewById(R.id.hotel_img));

        getData(FoodIntroURL());
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
                Hotel_Intro_Data=result;
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
            JSONObject jsonObj = new JSONObject(Hotel_Intro_Data);
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
            hotel_intro.setText(Html.fromHtml(OverviewValue));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    protected String FoodIntroURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=" + servicekey;
        String mid="&contentTypeId="+contentTypeId+"&contentId=" + Hotel_contentId;
        String last="&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y&_type=json";
        String data  = first  + mid  + last;
        return data;
    }

    //문의 및 소개 정보 가져오는 URL 생성
    protected String IntroURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey=" + servicekey;
        String mid="&contentTypeId="+contentTypeId+"&contentId=" + Hotel_contentId;
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
                Hotel_Data=result;
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
            JSONObject jsonObj = new JSONObject(Hotel_Data);
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
            if (!InfocenterValue.equals(""))
                TextView_Hotel_Infocenter.setText(Html.fromHtml(InfocenterValue));

            //주차 시설 정보
            String ParkingValue = "주차시설 정보가 없습니다.";
            if(jsonvalue.has(Parking))
                ParkingValue = jsonvalue.getString(Parking);
            //Log.d("Result","Parking 결과"+ParkingValue);
            if (!ParkingValue.equals(""))
                TextView_Hotel_Parking.setText(Html.fromHtml(ParkingValue));

            //객실 유형 정보
            String RoomtypeValue = "객실유형 정보가 없습니다.";
            if(jsonvalue.has(Roomtype))
                RoomtypeValue = jsonvalue.getString(Roomtype);
            Log.d("Result","Roomtype 결과"+RoomtypeValue);
            if (!RoomtypeValue.equals(""))
                TextView_Hotel_Roomtype.setText(Html.fromHtml(RoomtypeValue));

            //입실 유형 정보
            String CheckintimeValue = "객실유형 정보가 없습니다.";
            if(jsonvalue.has(Checkintime))
                CheckintimeValue = jsonvalue.getString(Checkintime);
            Log.d("Result","Checkintime 결과"+CheckintimeValue);
            if (!CheckintimeValue.equals(""))
                TextView_checkintime.setText(Html.fromHtml(CheckintimeValue));

            //퇴실 유형 정보
            String CheckoutimeValue = "객실유형 정보가 없습니다.";
            if(jsonvalue.has(Checkouttime))
                CheckoutimeValue = jsonvalue.getString(Checkouttime);
            Log.d("Result","Checkouttime 결과"+CheckoutimeValue);
            if (!CheckoutimeValue.equals(""))
                TextView_checkouttime.setText(Html.fromHtml(CheckoutimeValue));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
