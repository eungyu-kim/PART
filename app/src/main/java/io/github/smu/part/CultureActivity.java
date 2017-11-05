package io.github.smu.part;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
 * Created by kug00 on 2017-11-04.
 */

public class CultureActivity extends AppCompatActivity {

    TextView culture_title, culture_addr, culture_intro, TextView_culture_Infocenter, TextView_culture_parking, TextView_culture_Restdateleports,
            TextView_culture_Usetimeleports;

    //숙박 정보 소개 가져오기
    String culture_Intro_Data, culture_Data;
    //숙박 정보를 담기위한 해쉬 리스트 선언
    ArrayList<HashMap<String, String>> culture_S_ListHash;
    //받는 정보 분류
    int contentTypeId;
    //JSON에서 목록을 만들기위한 배열
    private static final String Detail_SID="contentid", Detail_Title = "title", Detail_address = "addr1", Detail_Image = "firstimage"
            ,Overview = "overview",Infocenter = "infocenterculture", Parking = "parkingculture",Restdateleports = "restdateculture"
            ,Usetimeleports = "usetimeculture";

    String culture_contentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.culture_detail);

        //아이디 찾기
        culture_title = (TextView) findViewById(R.id.culture_title);
        culture_addr = (TextView) findViewById(R.id.culture_addr);
        culture_intro = (TextView) findViewById(R.id.culture_intro);
        TextView_culture_Infocenter = (TextView) findViewById(R.id.TextView_culture_Infocenter);
        TextView_culture_parking = (TextView) findViewById(R.id.TextView_culture_parking);
        TextView_culture_Restdateleports = (TextView) findViewById(R.id.TextView_culture_Restdateleports);
        TextView_culture_Usetimeleports = (TextView) findViewById(R.id.TextView_culture_Usetimeleports);
        ImageButton culture_back = (ImageButton)findViewById(R.id.culture_detail_back);

        //지역정보를 저장하기 위한
        culture_S_ListHash = new ArrayList<HashMap<String, String>>();

        //정보 전달 받기
        Intent it = getIntent();

        //컨텐츠 ID 받기
        contentTypeId = it.getIntExtra("It_ContentTypeId", contentTypeId);
        //Log.d("ListView","contentTypeId:"+contentTypeId);


        //해쉬맵 가져오기
        HashMap<String, String> DetailHash;
        DetailHash = (HashMap<String, String>) it.getSerializableExtra("DetailHash");

        //Id 얻기
        culture_contentId = DetailHash.get(Detail_SID);
        //Log.d("ListView","Food_contentId:"+Food_contentId);

        //타이틀 얻기
        String Food_title = DetailHash.get(Detail_Title);
        //Log.d("ListView","Food_title:"+Food_title);
        culture_title.setText(Food_title);

        //주소 얻기
        String Food_addr = DetailHash.get(Detail_address);
        //Log.d("ListView","address:"+Food_addr);
        culture_addr.setText(Food_addr);

        //이미지 얻기
        String detail_img = DetailHash.get(Detail_Image);
        //Log.d("ListView","address:"+detail_img);
        // 아이템 내 각 위젯에 데이터 반영, 피카소 이용해 url 주소 이미지 넣기
        Picasso.with(this)
                .load(detail_img)
                .into((ImageView) findViewById(R.id.culture_img));

        getData(LeisueIntroURL());
        IntroData(IntroURL());

        culture_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getData(String url) {
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
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (IOException e) {
                    return "다운로드 실패";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                culture_Intro_Data = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void showList() {
        try {
            //전체 데이터 출력 Detaildata json으로 파싱 받은 데이터가 String 형식으로 되있다.
            //Log.d("Result","Detaildata 전체데이터출력 : "+Food_Intro_Data);

            //Json data -> JsonOject 변환
            JSONObject jsonObj = new JSONObject(culture_Intro_Data);
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
            if (jsonvalue.has(Overview))
                OverviewValue = jsonvalue.getString(Overview);
            //Log.d("Result","Overview 결과"+OverviewValue);
            culture_intro.setText(Html.fromHtml(OverviewValue));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected String LeisueIntroURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=" + servicekey;
        String mid = "&contentTypeId=" + contentTypeId + "&contentId=" + culture_contentId;
        String last = "&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y&_type=json";
        String data = first + mid + last;
        return data;
    }

    //문의 및 소개 정보 가져오는 URL 생성
    protected String IntroURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey=" + servicekey;
        String mid = "&contentTypeId=" + contentTypeId + "&contentId=" + culture_contentId;
        String last = "&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&introYN=Y&_type=json";
        String data = first + mid + last;
        return data;
    }

    //문의 및 안내 정보 가져오기
    public void IntroData(String url) {
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
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (IOException e) {
                    return "다운로드 실패";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                culture_Data = result;
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
            JSONObject jsonObj = new JSONObject(culture_Data);
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
            Log.d("Result", "Infocenter 결과" + InfocenterValue);
            if (!InfocenterValue.equals(""))
                TextView_culture_Infocenter.setText(Html.fromHtml(InfocenterValue));

            //주차 시설 정보
            String ParkingValue = "주차시설 정보가 없습니다.";
            if (jsonvalue.has(Parking))
                ParkingValue = jsonvalue.getString(Parking);
            Log.d("Result", "Parking 결과" + ParkingValue);
            if (!ParkingValue.equals(""))
                TextView_culture_parking.setText(Html.fromHtml(ParkingValue));

            //휴일 정보
            String RestdateleportsValue = "휴일 정보가 없습니다.";
            if (jsonvalue.has(Restdateleports))
                RestdateleportsValue = jsonvalue.getString(Restdateleports);
            Log.d("Result", "Parking 결과" + RestdateleportsValue);
            if (!RestdateleportsValue.equals(""))
                TextView_culture_Restdateleports.setText(Html.fromHtml(RestdateleportsValue));

            //이용 시간 정보
            String UsetimeleportsValue = "이용 시간 정보가 없습니다.";
            if (jsonvalue.has(Usetimeleports))
                UsetimeleportsValue = jsonvalue.getString(Usetimeleports);
            Log.d("Result", "Parking 결과" + UsetimeleportsValue);
            if (!UsetimeleportsValue.equals(""))
                TextView_culture_Usetimeleports.setText(Html.fromHtml(UsetimeleportsValue));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}