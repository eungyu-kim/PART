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
 * Created by kug00 on 2017-11-03.
 */

public class PartyDataActivity extends AppCompatActivity {
    //JSON에서 목록을 만들기위한 배열
    private static final String Detail_SID="contentid", Detail_Title = "title", Detail_address = "addr1", party_Image = "firstimage"
            ,Overview = "overview",EventStart = "eventstartdate", EventEnd="eventenddate",Eventplace = "eventplace";

    TextView Party_title, Party_addr, Party_intro, TextView_party_EventStart, TextView_party_EventEnd, TextView_party_eventplace;

    String Party_contentId;

    //숙박 정보 소개 가져오기
    String Hotel_Intro_Data, Hotel_Data;
    //숙박 정보를 담기위한 해쉬 리스트 선언
    ArrayList<HashMap<String, String>> Party_S_ListHash;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partydata);

        Party_title = (TextView)findViewById(R.id.party_title) ;
        Party_addr = (TextView)findViewById(R.id.partyl_addr);
        Party_intro = (TextView)findViewById(R.id.party_intro);
        TextView_party_EventStart = (TextView)findViewById(R.id.TextView_party_EventStart);
        TextView_party_EventEnd = (TextView)findViewById(R.id.TextView_party_EventEnd);
        TextView_party_eventplace = (TextView)findViewById(R.id.TextView_party_eventplace);

        //지역정보를 저장하기 위한
        Party_S_ListHash = new ArrayList<HashMap<String, String>>();

        //정보 전달 받기
        Intent it = getIntent();

        //해쉬맵 가져오기
        HashMap<String,String> DetailHash;
        DetailHash = (HashMap<String,String>)it.getSerializableExtra("DetailHash");

        //Id 얻기
        Party_contentId = DetailHash.get(Detail_SID);
        //Log.d("ListView","Food_contentId:"+Food_contentId);

        //타이틀 얻기
        String Food_title = DetailHash.get(Detail_Title);
        //Log.d("ListView","Food_title:"+Food_title);
        Party_title.setText(Food_title);

        //주소 얻기
        String Food_addr = DetailHash.get(Detail_address);
        //Log.d("ListView","address:"+Food_addr);
        Party_addr.setText(Food_addr);

        //이미지 얻기
        String detail_img = DetailHash.get(party_Image);
        //Log.d("ListView","address:"+detail_img);
        // 아이템 내 각 위젯에 데이터 반영, 피카소 이용해 url 주소 이미지 넣기
        Picasso.with(this)
                .load(detail_img)
                .into((ImageView)findViewById(R.id.party_img));

        //행사기간 얻기
        String EventStartValue = DetailHash.get(EventStart);
        //Log.d("ListView","address:"+Food_addr);
        TextView_party_EventStart.setText(EventStartValue);

        String EventEndValue = DetailHash.get(EventEnd);
        //Log.d("ListView","address:"+Food_addr);
        TextView_party_EventEnd.setText(EventEndValue);

        getData(ParyIntroURL());
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
            Party_intro.setText(Html.fromHtml(OverviewValue));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    protected String ParyIntroURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=" + servicekey;
        String mid="&contentTypeId=15&contentId=" + Party_contentId;
        String last="&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y&_type=json";
        String data  = first  + mid  + last;
        return data;
    }

    //문의 및 소개 정보 가져오는 URL 생성
    protected String IntroURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey=" + servicekey;
        String mid="&contentTypeId=15&contentId=" + Party_contentId;
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
            //이벤트 장소 정보
            String EventplaceValue = "이벤트 장소 정보가 없습니다.";
            if(jsonvalue.has(Eventplace))
                EventplaceValue = jsonvalue.getString(Eventplace);
            //Log.d("Result","Infocenter 결과"+EventplaceValue);
            if (!EventplaceValue.equals(""))
                TextView_party_eventplace.setText(Html.fromHtml(EventplaceValue));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
