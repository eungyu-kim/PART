package io.github.smu.part;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
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
 * Created by kug00 on 2017-11-04.
 */

public class Total_Search extends AppCompatActivity {
    //검색어
    String Search_Value;
    //리스트뷰
    ListView total_search_list;
    //받은정보 데이터 페이지 카운터
    int Contentcount=1, maxcount =0, countValue = 0;
    //JSON 문자열을 저장할 문자열
    String totalsearchdata;
    //통합 정보를 담기위한 해쉬 리스트 선언
    ArrayList<HashMap<String, String>> Total_S_ListHash;
    //JSON에서 목록을 만들기위한 배열
    private static final String Total_SID="contentid", Title = "title", address = "addr1", Image = "firstimage"
            ,Contenttypeid ="contenttypeid";
    //여행 리스트뷰 어댑터 선언
    LocalListViewAdapter adapter;
    boolean lastitemVisibleFlag = false;		//화면에 리스트의 마지막 아이템이 보여지는지 체크
    String ContenttypeidValue ="12";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_search);

        //정보 전달 받기
        Intent it = getIntent();
        Search_Value = it.getStringExtra("Search_Value");
        Log.d("result","Search_Value"+Search_Value);

        //아이디 찾기
        ImageButton total_search_back = (ImageButton)findViewById(R.id.total_search_back);
        total_search_list = (ListView)findViewById(R.id.total_search_list);
        final ProgressBar party_progressBar = (ProgressBar)findViewById(R.id.total_search_progressBar);

        //뒤로 가기
        total_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Total_Search.this, MainActivity.class);
                startActivity(it);
                finish();
            }
        });

        //통합 검색 정보를 저장하기 위한
        Total_S_ListHash = new ArrayList<HashMap<String, String>>();
        //통합 검색 정보를 커스텀  listView와 연결하기 위한 어뎁터
        adapter = new LocalListViewAdapter();
        getData(CreateURL(),1);

        //리스트뷰 바닥에 닿았을 때
        total_search_list.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    //프로그레스 생성
                    party_progressBar.setVisibility(view.VISIBLE);
                    // listview 갱신
                    getData(CreateURL(),2);
                    //프로그레스 없앰
                    party_progressBar.setVisibility(view.GONE);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });

        //리스트 뷰 클릭시 이벤트
        total_search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                //LocalListViewItem item = (LocalListViewItem) parent.getItemAtPosition(position) ;
                //해쉬맵 가져오기
                HashMap<String, String> TotalHash;
                TotalHash = Total_S_ListHash.get(position);
                ContenttypeidValue = TotalHash.get(Contenttypeid);
                Log.d("result","ContenttypeidValue"+ContenttypeidValue);

                if (ContenttypeidValue.equals("12")) {
                    Intent it = new Intent(Total_Search.this, DetailActivity.class);

                    //클릭 위치 출력
                    // Log.d("ListView","position:"+position);
                    int contentTypeId = Integer.parseInt(ContenttypeidValue);
                    //컨텐츠 ID 넣기
                    it.putExtra("It_ContentTypeId", contentTypeId);

                    //해쉬맵 가져오기
                    HashMap<String, String> DetailHash;
                    DetailHash = Total_S_ListHash.get(position);

                    //해쉬맵 넘기기
                    it.putExtra("DetailHash", DetailHash);
                    //다음 화면으로
                    startActivity(it);
                }
                //맛집 상세정보 넘기기
                if (ContenttypeidValue.equals("39")) {
                    Intent food_it = new Intent(Total_Search.this, FoodActivity.class);

                    //Log.d("ListView","position:"+contentTypeId);
                    //클릭 위치 출력력
                    // Log.d("ListView","position:"+position);
                    int contentTypeId = Integer.parseInt(ContenttypeidValue);
                    //컨텐츠 ID 넣기
                    food_it.putExtra("It_ContentTypeId", contentTypeId);

                    //해쉬맵 가져오기
                    HashMap<String, String> FoodHash;
                    FoodHash = Total_S_ListHash.get(position);

                    //해쉬맵 넘기기
                    food_it.putExtra("DetailHash", FoodHash);

                    //다음 화면으로
                    startActivity(food_it);
                }
                //레포츠 상세정보 넘기기
                if (ContenttypeidValue.equals("28")) {
                    Intent Leisure_it = new Intent(Total_Search.this, LeisureActivity.class);

                    //Log.d("ListView","position:"+contentTypeId);
                    //클릭 위치 출력력
                    // Log.d("ListView","position:"+position);
                    int contentTypeId = Integer.parseInt(ContenttypeidValue);
                    //컨텐츠 ID 넣기
                    Leisure_it.putExtra("It_ContentTypeId", contentTypeId);

                    //해쉬맵 가져오기
                    HashMap<String, String> DetailHash;
                    DetailHash = Total_S_ListHash.get(position);

                    //해쉬맵 넘기기
                    Leisure_it.putExtra("DetailHash", DetailHash);
                    //다음 화면으로
                    startActivity(Leisure_it);
                }

                //숙박 상세정보 넘기기
                if (ContenttypeidValue.equals("32")) {
                    Intent Leisure_it = new Intent(Total_Search.this, HotelActivity.class);

                    //Log.d("ListView","position:"+contentTypeId);
                    //클릭 위치 출력력
                    // Log.d("ListView","position:"+position);
                    int contentTypeId = Integer.parseInt(ContenttypeidValue);
                    //컨텐츠 ID 넣기
                    Leisure_it.putExtra("It_ContentTypeId", contentTypeId);

                    //해쉬맵 가져오기
                    HashMap<String, String> DetailHash;
                    DetailHash = Total_S_ListHash.get(position);

                    //해쉬맵 넘기기
                    Leisure_it.putExtra("DetailHash", DetailHash);
                    //다음 화면으로
                    startActivity(Leisure_it);
                }

                if (ContenttypeidValue.equals("15")) {
                    Intent Party_it = new Intent(Total_Search.this, PartyDataActivity.class);

                    //Log.d("ListView","position:"+contentTypeId);
                    //클릭 위치 출력력
                    // Log.d("ListView","position:"+position);
                    int contentTypeId = Integer.parseInt(ContenttypeidValue);
                    //컨텐츠 ID 넣기
                    Party_it.putExtra("It_ContentTypeId", contentTypeId);

                    //해쉬맵 가져오기
                    HashMap<String, String> DetailHash;
                    DetailHash = Total_S_ListHash.get(position);

                    //해쉬맵 넘기기
                    Party_it.putExtra("DetailHash", DetailHash);
                    //다음 화면으로
                    startActivity(Party_it);
                }
            }
        }) ;


    }

    public void getData(String url, final int update){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            int Udate;
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                Udate = update;
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
                totalsearchdata=result;
                showList(Udate);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void showList(int update){
        try {
            //전체 데이터 출력 localsearchdata json으로 파싱 받은 데이터가 String 형식으로 되있다.
            //Log.d("Result","localsearchdata 전체데이터출력 : "+localsearchdata);

            //Json data -> JsonOject 변환
            JSONObject jsonObj = new JSONObject(totalsearchdata);
            //JsonObject -> 하위 JsonObject Get
            String response = jsonObj.getString("response");
            //Log.d("Result","response 결과"+response);

            JSONObject Response = new JSONObject(response);
            String body = Response.getString("body");
            //Log.d("Result","body 결과"+body);

            JSONObject Body = new JSONObject(body);
            String items = Body.getString("items");
            //Log.d("Result","items 결과"+items);

            //페이지 최대값
            String totalCount = Body.getString("totalCount");
            //Log.d("Result","totalCount 결과"+totalCount);
            //Log.d("Result","countValue 결과"+countValue);
            maxcount = Integer.parseInt(totalCount);
            if (countValue<=maxcount)
                Contentcount++;
            if (countValue>=maxcount){
                Toast.makeText(Total_Search.this, "마지막 목록입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject Item = new JSONObject(items);
            String item = Item.getString("item");
            //Log.d("Result","item 결과"+item);

            //JsonObject -> JSONArray 값 추출
            JSONArray ItemArray = Item.getJSONArray("item");

            //JSONArray 길이만큼 반복
            for(int i=0;i<ItemArray.length();i++){
                countValue++;
                JSONObject c =ItemArray.getJSONObject(i);
                String contentid = c.getString(Total_SID);
                //Log.d("Result","contentid 결과"+contentid);
                String title = "제목이 없습니다.";
                if(c.has(Image))
                    title = c.getString(Title);
                else continue;
                //Log.d("Result","PartyTitle 결과"+title);
                String addr1 = "주소가 없습니다.";
                if(c.has(address))
                    addr1 = c.getString(address);
                else continue;
                //Log.d("Result","addr1 결과"+addr1);
                String firstimage ="null";
                //존재하지 않는경우 저장하지 않는다.
                if(c.has(Image))
                    firstimage = c.getString(Image);
                else continue;
                //Log.d("Result","firstimage 결과"+firstimage);


                //존재하지 않는경우 저장하지 않는다.
                if(c.has(Contenttypeid))
                    ContenttypeidValue = c.getString(Contenttypeid);
                else continue;
                //Log.d("Result","ContenttypeidValue 결과"+ContenttypeidValue);

                HashMap<String,String> LocalHash = new HashMap<String,String>();
                LocalHash.put(Total_SID,contentid);
                LocalHash.put(Title,title);
                LocalHash.put(address,addr1);
                LocalHash.put(Image,firstimage);
                LocalHash.put(Contenttypeid,ContenttypeidValue);
                Total_S_ListHash.add(LocalHash);
                // 아이템 추가.
                adapter.addItem(firstimage, title, addr1) ;
            }
            if (update == 1) {
                //행사정보 어댑터 리스트 뷰에 달기
                total_search_list.setAdapter(adapter);
                //Log.d("Result","data 결과"+CreateURL());
            }
            if (update == 2) {
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected String CreateURL() {
        String servicekey = "8F4FRvrVqxyBojiBd%2F7SGgGkxpeG6bUdOfq3MHZFGEvVCs2rr%2FB8QBNsjAnt4JyqUK0hHYbb64Or9bcma65Tgw%3D%3D";
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchKeyword?ServiceKey=" + servicekey;
        String mid="&keyword="+Search_Value+"&areaCode=&sigunguCode=";
        String last="&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=15&pageNo="+Contentcount+"&_type=json";
        String data  = first  + mid  + last;
        return data;
    }
}
