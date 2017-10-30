package io.github.hidroh.calendar;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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
    //메인화면에서 받은 지역데이터
    String MainCategory, MiddleCategory;
    boolean lastitemVisibleFlag = false;		//화면에 리스트의 마지막 아이템이 보여지는지 체크
    //받은정보 데이터 페이지 카운터
    int Contentcount=1, maxcount =0;
    //받는 정보 분류
    int contentTypeId=12;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localsearch);

        //아이디 찾기
        ImageButton Search_back = (ImageButton)findViewById(R.id.search_back);
        Button Restaurant = (Button)findViewById(R.id.restaurant);
        Locla_S_List = (ListView)findViewById(R.id.search_list);
        Button Destination = (Button)findViewById(R.id.destination);
        Button Leisure = (Button)findViewById(R.id.ㅣeisure);
        Button Hotel = (Button)findViewById(R.id.hotel);

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
                startActivity(it);
                finish();
            }
        });

        //정보 전달 받기
        Intent it = getIntent();
        MainCategory = it.getStringExtra("areaCode");
        MiddleCategory = it.getStringExtra("sigunguCode");
        getData(CreateURL(MainCategory,MiddleCategory));

        //맛집 버튼을 눌렀을 때
        Restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count ;
                count = adapter.getCount() ;
                contentTypeId =39;
                Contentcount=1;
                if (count > 0) {
                        // listview 데이터 삭제
                        adapter.removeall();
                        getData(CreateURL(MainCategory,MiddleCategory));
                        Locla_S_List.setAdapter(adapter);
                }
            }
        });

        //관광지 버튼을 눌렀을 때
        Destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count ;
                count = adapter.getCount() ;
                contentTypeId =12;
                Contentcount=1;
                if (count > 0) {
                    // listview 데이터 삭제
                    adapter.removeall();
                    getData(CreateURL(MainCategory,MiddleCategory));
                    Locla_S_List.setAdapter(adapter);
                }
            }
        });

        //레포츠 버튼을 눌렀을 때
        Leisure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count ;
                count = adapter.getCount() ;
                contentTypeId = 28;
                Contentcount=1;
                if (count > 0) {
                    // listview 데이터 삭제
                    adapter.removeall();
                    getData(CreateURL(MainCategory,MiddleCategory));
                    Locla_S_List.setAdapter(adapter);
                }
            }
        });

        //숙박업소 버튼이 눌렸을 때
        Hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count ;
                count = adapter.getCount() ;
                contentTypeId = 32;
                Contentcount=1;
                if (count > 0) {
                    // listview 데이터 삭제
                    adapter.removeall();
                    getData(CreateURL(MainCategory,MiddleCategory));
                    Locla_S_List.setAdapter(adapter);
                }
            }
        });

        //리스트뷰 바닥에 닿았을 때
        Locla_S_List.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    getData(CreateURL(MainCategory,MiddleCategory));
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

        //리스트 뷰 클릭시 이벤트
        Locla_S_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                //LocalListViewItem item = (LocalListViewItem) parent.getItemAtPosition(position) ;
                Intent it = new Intent(LocalSearchActivity.this, DetailActivity.class);

                //클릭 위치 출력력
                // Log.d("ListView","position:"+position);


                //컨텐츠 ID 넣기
                it.putExtra("It_ContentTypeId",contentTypeId);

                //해쉬맵 가져오기
                HashMap<String,String> DetailHash;
                DetailHash = Locla_S_ListHash.get(position);

                //해쉬맵 넘기기
                it.putExtra("DetailHash",DetailHash);
                //다음 화면으로
                startActivity(it);
            }
        }) ;
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
                String contentid = c.getString(Locla_SID);
                Log.d("Result","contentid 결과"+contentid);
                String title = "제목이 없습니다.";
                if(c.has(Image))
                    title = c.getString(Title);
                else continue;
                Log.d("Result","PartyTitle 결과"+title);
                String addr1 = "주소가 없습니다.";
                if(c.has(address))
                    addr1 = c.getString(address);
                else continue;
                Log.d("Result","addr1 결과"+addr1);
                String firstimage ="null";
                //존재하지 않는경우 저장하지 않는다.
                if(c.has(Image))
                    firstimage = c.getString(Image);
                else continue;
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
            //Log.d("Result","data 결과"+CreateURL());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected String CreateURL(String areaCode, String sigunguCode) {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=" + servicekey;
        String mid="&contentTypeId="+contentTypeId+"&areaCode=" + areaCode + "&sigunguCode="+sigunguCode;
        String last="&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=15&pageNo="+Contentcount+"&_type=json";
        String data  = first  + mid  + last;
        return data;
    }
}