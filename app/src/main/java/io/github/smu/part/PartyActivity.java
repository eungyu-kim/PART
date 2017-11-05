package io.github.smu.part;

import android.content.Intent;
import android.graphics.Typeface;
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
 * Created by kug00 on 2017-10-12.
 */

public class PartyActivity extends AppCompatActivity {
    //JSON 문자열을 저장할 문자열
    String partydata;
    //JSON에서 목록을 만들기위한 배열
    private static final String PartyID="contentid", Title = "title", address = "addr1",
            Image = "firstimage", EventStart = "eventstartdate", EventEnd="eventenddate";

    boolean isLastItem = false;		//화면에 리스트의 마지막 아이템이 보여지는지 체크

    //받은정보 데이터 페이지 카운터
    int Contentcount=1, maxcount =0, countValue = 0, contentTypeId=12;

    //행사 정보를 담기위한 해쉬 리스트 선언
    ArrayList<HashMap<String, String>> PartyListHash;
    //행사정보를 표현하는 리스트 뷰 선언
    ListView PartyList;
    //여행 리스트뷰 어댑터 선언
    PartyAdapter adapter;
    //이벤트 시작 시간
    String eventStartDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party);

        TextView party_title_1 = (TextView) findViewById(R.id.tmonth1);
        TextView party_title_2 = (TextView) findViewById(R.id.textView4);

       // 타이틀글꼴 변경
        Typeface typeface_1 = Typeface.createFromAsset(getAssets(), "HoonWhitecatR.ttf");
        party_title_1.setTypeface(typeface_1);
        Typeface typeface_2 = Typeface.createFromAsset(getAssets(), "HoonWhitecatR.ttf");
        party_title_2.setTypeface(typeface_2);

        //아이디 찾기
        ImageButton GoMain = (ImageButton)findViewById(R.id.gomain);

        TextView TMonth1 = (TextView)findViewById(R.id.tmonth1);
        TextView TMonth2 = (TextView)findViewById(R.id.tmonth2);
        PartyList = (ListView)findViewById(R.id.partylist);

        final ProgressBar party_progressBar = (ProgressBar)findViewById(R.id.party_progressBar);

        //행사 정보를 담기위한 해쉬 리스트 생성
        PartyListHash = new ArrayList<HashMap<String,String>>();

        //x버튼 누를 시 이전 메인화면으로 이동
        GoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PartyActivity.this,MainActivity.class);
                startActivity(it);
            }
        });

        GetTime time = new GetTime();
        //월 텍스트에 출력
        TMonth1.setText(time.getTime(1));
        TMonth2.setText(time.getTime(1));

        //이벤트 시작시간 설정
        eventStartDate = time.getTime(2);

        //여행 리스트뷰 어댑터 생성
        adapter = new PartyAdapter();

        getData(CreateURL(),1);
        //피카소 연습
        //ImageView imageView = (ImageView) findViewById(R.id.imageView00);
        /*Picasso.with(this)
                .load("http://square.github.io/picasso/static/sample.png")
                .into((ImageView)findViewById(R.id.imageView00));*/

        //Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        //리스트뷰 바닥에 닿았을 때
        PartyList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻

                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isLastItem) {
                    //TODO 화면이 바닦에 닿을때 처리
                    int count = adapter.getCount();
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
                //마지막 위치이면, isLastItem에 true가 들어감
                isLastItem = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });

        //리스트 뷰 클릭시 이벤트
        PartyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                //LocalListViewItem item = (LocalListViewItem) parent.getItemAtPosition(position) ;

                Intent it = new Intent(PartyActivity.this, PartyDataActivity.class);

                //클릭 위치 출력
                // Log.d("ListView","position:"+position);

                //해쉬맵 가져오기
                HashMap<String, String> DetailHash;
                DetailHash = PartyListHash.get(position);

                //해쉬맵 넘기기
                it.putExtra("DetailHash", DetailHash);
                //다음 화면으로
                startActivity(it);
            }
        }) ;
    }


    public void getData(String url, final int update){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            int updatevalue;
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                updatevalue = update;
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
                partydata=result;
                showList(updatevalue);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void showList(int update){
        try {
            //전체 데이터 출력 partydata는 json으로 파싱 받은 데이터가 String 형식으로 되있다.
            Log.d("Result","partydata 전체데이터출력 : "+partydata);

            //Json data -> JsonOject 변환
            JSONObject jsonObj = new JSONObject(partydata);
            //JsonObject -> 하위 JsonObject Get
            String response = jsonObj.getString("response");
            Log.d("Result","response 결과"+response);

            JSONObject Response = new JSONObject(response);
            String body = Response.getString("body");
            Log.d("Result","body 결과"+body);

            JSONObject Body = new JSONObject(body);

            //페이지 최대값
            String totalCount = Body.getString("totalCount");
            Log.d("Result","totalCount 결과"+totalCount);
            maxcount = Integer.parseInt(totalCount);
            if (countValue<=maxcount)
                Contentcount++;
            if (countValue>=maxcount){
                Toast.makeText(PartyActivity.this, "마지막 목록입니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            String items = Body.getString("items");
            Log.d("Result","items 결과"+items);

            JSONObject Item = new JSONObject(items);
            String item = Item.getString("item");
            Log.d("Result","item 결과"+item);

            //JsonObject -> JSONArray 값 추출
            JSONArray ItemArray = Item.getJSONArray("item");

            //JSONArray 길이만큼 반복
            for(int i=0;i<ItemArray.length();i++){
                countValue++;
                JSONObject c =ItemArray.getJSONObject(i);
                String contentid = c.getString(PartyID);
                //Log.d("Result","contentid 결과"+contentid);
                String title = c.getString(Title);
                //Log.d("Result","PartyTitle 결과"+title);
                //존재하지 않는경우 저장하지 않는다.
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
                String eventstartdate = c.getString(EventStart);
                //Log.d("Result","eventstartdate 결과"+eventstartdate);
                String eventenddate = c.getString(EventEnd);
                //Log.d("Result","eventenddate 결과"+eventenddate);

                HashMap<String,String> PartyHash = new HashMap<String,String>();
                PartyHash.put(PartyID,contentid);
                PartyHash.put(Title,title);
                PartyHash.put(address,addr1);
                PartyHash.put(Image,firstimage);
                PartyHash.put(EventStart,eventstartdate);
                PartyHash.put(EventEnd,eventenddate);
                PartyListHash.add(PartyHash);
                // 아이템 추가.
                adapter.addItem(firstimage, title, addr1, eventstartdate, eventenddate) ;
            }
            //행사정보 어댑터 리스트 뷰에 달기
            if (update == 1) {
                PartyList.setAdapter(adapter);
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
        String first="http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchFestival?ServiceKey=" + servicekey;
        String mid="&eventStartDate="+eventStartDate+"&eventEndDate=&areaCode=&sigunguCode=";
        String last="&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo="+Contentcount+"&_type=json";
        String data  = first  + mid  + last;
        return data;
    }
}
