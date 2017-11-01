package io.github.hidroh.calendar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
 * Created by kug00 on 2017-10-27.
 */

public class TravelCourseActivity extends AppCompatActivity {
    //JSON 문자열을 저장할 문자열
    String travelcoursedata;
    //JSON에서 목록을 만들기위한 배열
    private static final String Course_SID="contentid", Title = "title", Image = "firstimage";

    //지역 정보를 담기위한 해쉬 리스트 선언
    ArrayList<HashMap<String, String>> Course_S_ListHash;
    //지역정보를 저장하기 위한 표현하는 리스트 뷰 선언
    ListView Course_S_List;
    //여행 리스트뷰 어댑터 선언
    TravelCourseListViewAdapter adapter;
    //메인화면에서 받은 지역데이터
    boolean lastitemVisibleFlag = false;		//화면에 리스트의 마지막 아이템이 보여지는지 체크
    //받은정보 데이터 페이지 카운터
    int Contentcount=1, maxcount =0;
    //받는 정보 분류
    int contentTypeId=25;
    //스피너 선언
    ArrayAdapter<CharSequence> adspin1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travlecourse);

        TextView party_title_5 = (TextView) findViewById(R.id.travelcourse_title);
        TextView party_title_6 = (TextView) findViewById(R.id.travelcourse_detail);
        // 타이틀글꼴 변경
        Typeface typeface_5 = Typeface.createFromAsset(getAssets(), "HoonWhitecatR.ttf");
        party_title_5.setTypeface(typeface_5);
        Typeface typeface_6 = Typeface.createFromAsset(getAssets(), "HoonWhitecatR.ttf");
        party_title_6.setTypeface(typeface_6);

        //아이디 찾기
        ImageButton TravelCourse_Back = (ImageButton)findViewById(R.id.travelcourse_back);
        Course_S_List = (ListView)findViewById(R.id.travelcourse_list);
        final Button Search_Btn = (Button)findViewById(R.id.travelcourse_search_btn);

        final Spinner spin1 = (Spinner) findViewById(R.id.spinner_1);
        spin1.setPrompt("대분류 지역을 선택하세요.");
        // 메인 카테고리 선택 (지역)
        adspin1 = ArrayAdapter.createFromResource(this, R.array.spinner_main, android.R.layout.simple_spinner_dropdown_item); // 첫번째 어댑터에 값 넣기
        // simple_spinner_dropdown_item은 안드로이드에서 제공하는 스피너 모양
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adspin1); //어댑터에 값들을 spinner에 넣기.. 첫번째 끝!!

        //지역검색 버튼 누를 시
        Search_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text1 = spin1.getSelectedItemPosition();
                String areaCode = String.valueOf(text1);
                Toast.makeText(TravelCourseActivity.this, "1"+text1, Toast.LENGTH_SHORT).show();
                Intent it = new Intent(TravelCourseActivity.this, LocalSearchActivity.class);
                it.putExtra("areaCode",areaCode);
                startActivity(it);
                finish();
            }
        });

        //클래스 생성
        //지역정보를 저장하기 위한
        Course_S_ListHash = new ArrayList<HashMap<String, String>>();
        //지역 정보를 커스텀  listView와 연결하기 위한 어뎁터
        adapter = new TravelCourseListViewAdapter();

        TravelCourse_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TravelCourseActivity.this,MainActivity.class);
                startActivity(it);
                finish();
            }
        });
        //리스트뷰 데이터 가져오기
        getData(CreateURL());

        //리스트뷰 바닥에 닿았을 때
        Course_S_List.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {
                    //TODO 화면이 바닦에 닿을때 처리
                    Log.d("listview","바닥에 닿음"+Contentcount);
                    int count = adapter.getCount();
                    // 아이템 추가.
                    //items.add("LIST" + Integer.toString(count + 1));
                    getData(CreateURL());
                    // listview 갱신
                    //adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });
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
                travelcoursedata=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void showList(){
        try {
            //전체 데이터 출력 travelcoursedata json으로 파싱 받은 데이터가 String 형식으로 되있다.
            Log.d("Result","travelcoursedata 전체데이터출력 : "+travelcoursedata);

            //Json data -> JsonOject 변환
            JSONObject jsonObj = new JSONObject(travelcoursedata);
            //JsonObject -> 하위 JsonObject Get
            String response = jsonObj.getString("response");
            Log.d("Result","response 결과"+response);

            JSONObject Response = new JSONObject(response);
            String body = Response.getString("body");
            Log.d("Result","body 결과"+body);

            JSONObject Body = new JSONObject(body);
            String items = Body.getString("items");
            Log.d("Result","items 결과"+items);

            //페이지 최대값
            String numOfRows = Body.getString("numOfRows");
            maxcount = Integer.parseInt(numOfRows);
            if (Contentcount<maxcount)
                Contentcount++;

            JSONObject Item = new JSONObject(items);
            String item = Item.getString("item");
            Log.d("Result","item 결과"+item);

            //JsonObject -> JSONArray 값 추출
            JSONArray ItemArray = Item.getJSONArray("item");

            //JSONArray 길이만큼 반복
            for(int i=0;i<ItemArray.length();i++){
                JSONObject c =ItemArray.getJSONObject(i);
                String contentid = c.getString(Course_SID);
                Log.d("Result","contentid 결과"+contentid);
                String title = "제목이 없습니다.";
                if(c.has(Image))
                    title = c.getString(Title);
                else continue;
                Log.d("Result","PartyTitle 결과"+title);
                String firstimage ="null";
                //존재하지 않는경우 저장하지 않는다.
                if(c.has(Image))
                    firstimage = c.getString(Image);
                else continue;
                Log.d("Result","firstimage 결과"+firstimage);

                HashMap<String,String> CourseHash = new HashMap<String,String>();
                CourseHash.put(Course_SID,contentid);
                CourseHash.put(Title,title);
                CourseHash.put(Image,firstimage);
                Course_S_ListHash.add(CourseHash);
                // 아이템 추가.
                adapter.addItem(firstimage, title) ;
            }
            //행사정보 어댑터 리스트 뷰에 달기
            Course_S_List.setAdapter(adapter);
            //Log.d("Result","data 결과"+CreateURL());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected String CreateURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey="+servicekey;
        String mid="&contentTypeId="+contentTypeId+"&areaCode=&sigunguCode=";
        String last="&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=15&pageNo="+Contentcount+"&_type=json";
        String data  = first  + mid  + last;
        return data;
    }
}
