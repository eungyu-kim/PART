package io.github.hidroh.calendar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kug00 on 2017-10-17.
 */

public class LocalSearchActivity extends AppCompatActivity {
    //JSON 문자열을 저장할 문자열
    String localsearchdata;
    //JSON에서 목록을 만들기위한 배열
    private static final String Locla_SID="contentid", Title = "title", address = "addr1", Image = "firstimage";

    //지역 정보를 담기위한 해쉬 리스트 선언
    ArrayList<HashMap<String, String>> Locla_S_ListHash;
    //지역정보를 저장하기 위한 표현하는 리스트 뷰 선언
    ListView Locla_S_List;
    //여행 리스트뷰 어댑터 선언
    LocalListViewAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localsearch);

        //아이디 찾기
        ImageButton Search_back = (ImageButton)findViewById(R.id.search_back);
        Locla_S_List = (ListView)findViewById(R.id.search_list);

        //클래스 생성
        //지역정보를 저장하기 위한
        Locla_S_ListHash = new ArrayList<HashMap<String, String>>();
        //지역 정보를 커스텀  listView와 연결하기 위한 어뎁터
        adapter = new LocalListViewAdapter();

        //맨 상당 뒤로가기 버튼
        Search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LocalSearchActivity.this,MainActivity.class);
                finish();
            }
        });


        getData(CreateURL());
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
                localsearchdata=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void showList(){
        try {
            //전체 데이터 출력 localsearchdata json으로 파싱 받은 데이터가 String 형식으로 되있다.
            Log.d("Result","localsearchdata 전체데이터출력 : "+localsearchdata);

            //Json data -> JsonOject 변환
            JSONObject jsonObj = new JSONObject(localsearchdata);
            //JsonObject -> 하위 JsonObject Get
            String response = jsonObj.getString("response");
            Log.d("Result","response 결과"+response);

            JSONObject Response = new JSONObject(response);
            String body = Response.getString("body");
            Log.d("Result","body 결과"+body);

            JSONObject Body = new JSONObject(body);
            String items = Body.getString("items");
            Log.d("Result","items 결과"+items);

            JSONObject Item = new JSONObject(items);
            String item = Item.getString("item");
            Log.d("Result","item 결과"+item);

            //JsonObject -> JSONArray 값 추출
            JSONArray ItemArray = Item.getJSONArray("item");

            //JSONArray 길이만큼 반복
            for(int i=0;i<ItemArray.length();i++){
                JSONObject c =ItemArray.getJSONObject(i);
                String contentid = c.getString(Locla_SID);
                Log.d("Result","contentid 결과"+contentid);
                String title = "제목이 없습니다.";
                if(c.has(Image))
                    title = c.getString(Title);
                Log.d("Result","PartyTitle 결과"+title);
                String addr1 = "주소가 없습니다.";
                if(c.has(address))
                    addr1 = c.getString(address);
                Log.d("Result","addr1 결과"+addr1);
                String firstimage ="null";
                //존재하지 않는경우 저장하지 않는다.
                if(c.has(Image))
                    firstimage = c.getString(Image);
                Log.d("Result","firstimage 결과"+firstimage);

                HashMap<String,String> LocalHash = new HashMap<String,String>();
                LocalHash.put(Locla_SID,contentid);
                LocalHash.put(Title,title);
                LocalHash.put(address,addr1);
                LocalHash.put(Image,firstimage);
                Locla_S_ListHash.add(LocalHash);
                // 아이템 추가.
                adapter.addItem(firstimage, title, addr1) ;
            }
            //행사정보 어댑터 리스트 뷰에 달기
            Locla_S_List.setAdapter(adapter);
            Log.d("Result","data 결과"+CreateURL());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected String CreateURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=";
        String last="&contentTypeId=&areaCode=31&sigunguCode=1&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo=1&_type=json";
        String data  = first + servicekey + last;
        return data;
    }
}