package io.github.hidroh.calendar;

        import android.content.Intent;
        import android.graphics.Typeface;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.text.SimpleDateFormat;
        import java.util.Date;

public class MainActivity extends AppCompatActivity {
    long mNow;
    Date mDate;

    //전체 일자 출력
    SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat mFormat = new SimpleDateFormat("MM");

    //메인 카테고리 스피너 값은 strings.xml에 item으로 선언함..
    //스피너 선언
    ArrayAdapter<CharSequence> adspin1, adspin2; // adspin1은 대분류, adspin2는 세부분류

    // 검색 시, 선택된 메세지 띄우기 초기화
    String choice_main="";
    String choice_detail="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner spin1 = (Spinner) findViewById(R.id.spinner_1);
        final Spinner spin2 = (Spinner) findViewById(R.id.spinner_2);
        spin1.setPrompt("대분류 지역을 선택하세요.");
        Button btn_search = (Button) findViewById(R.id.main_search_btn);

        //메인 카테고리 선택 (지역)
        adspin1 = ArrayAdapter.createFromResource(this, R.array.spinner_main, android.R.layout.simple_spinner_dropdown_item); // 첫번째 어댑터에 값 넣기
        // simple_spinner_dropdown_item은 안드로이드에서 제공하는 스피너 모양
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adspin1); //어댑터에 값들을 spinner에 넣기.. 첫번째 끝!!
        adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_seoul, android.R.layout.simple_spinner_dropdown_item);
        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기

        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  // 첫번째 스피너 클릭시 이벤트
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long id) { // position은 어딜 선택했는지 위치

                if (adspin1.getItem(position).equals("서울")) {//spinner에 값을 가져와서 position이 클릭 한것이 서울인지 확인합니다.
                        choice_main = "서울" ; // 버튼 클릭시 값 출력
                        adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_seoul, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                choice_detail = adspin2.getItem(position).toString();
                            }

                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });

                    }else if (adspin1.getItem(position).equals("인천")) {
                    choice_main = "인천"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Incheon, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("대전")) {
                    choice_main = "대전"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Daujeon, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("대구")) {
                    choice_main = "대구"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Daegu, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("광주")) {
                    choice_main = "광주"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Gwangju, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("부산")) {
                    choice_main = "부산"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Busan, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("울산")) {
                    choice_main = "울산"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Ulsan, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("세종특별자치시")) {
                    choice_main = "세종특별자치시"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Sejung, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("경기도")) {
                    choice_main = "경기도"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Gyeonggido, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("강원도")) {
                    choice_main = "강원도"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Gangwondo, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("충청북도")) {
                    choice_main = "충청북도"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Chungcheongbukdo, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("충청남도")) {
                    choice_main = "충청남도"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Chungcheongnamdo, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("경상북도")) {
                    choice_main = "경상북도"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Gyeongsangbukdo, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("경상남도")) {
                    choice_main = "경상남도"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Gyeongsangnamdo, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("전라북도")) {
                    choice_main = "전라북도"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Jeollabukdo, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("전라남도")) {
                    choice_main = "전라북도"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Jeollanamdo, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (adspin1.getItem(position).equals("제주특별시")) {
                    choice_main = "제주특별시"; // 버튼 클릭시 값 출력
                    adspin2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.spinner_Island, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2); // 2번째 어레이값 스피너에 넣기
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_detail = adspin2.getItem(position).toString();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, choice_main + " > " + choice_detail, Toast.LENGTH_SHORT).show(); // 선택된 대분류,세부분류를 출력함
            }
        });
        //아이디 가져오기
        ImageButton GoToPlan = (ImageButton)findViewById(R.id.goplan);
        ImageButton GoTmap = (ImageButton)findViewById(R.id.goTmap);
        LinearLayout GoParty = (LinearLayout)findViewById(R.id.goparty);
        TextView Month1 = (TextView)findViewById(R.id.month1);
        TextView Month2 = (TextView)findViewById(R.id.month2);
        Button Search_Btn = (Button)findViewById(R.id.main_search_btn);
        TextView title = (TextView) findViewById(R.id.Title);

        // 타이틀글꼴 변경
        Typeface typeface = Typeface.createFromAsset(getAssets(), "NanumPen.ttf");
        title.setTypeface(typeface);
        title.append("PART");

        //여행계획으로 화면 이동
        GoToPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, PlanActivity.class);
                startActivity(it);
            }
        });

        //길찾기로 화면 이동!! 여기서 TmapMain으로 이동해서 시작
        GoTmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        //        Intent it = new Intent(MainActivity.this, TmapMain.class);
        //          startActivity(it);
            }
        });

        //행사정보 화면으로 이동
        GoParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, PartyActivity.class);
                startActivity(it);
            }
        });

        //지역검색 버튼 누를 시
        Search_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, LocalSearchActivity.class);
                startActivity(it);
                finish();
            }
        });

        //현재 월 텍스트 출력
        Month1.setText(getTime(1));
        Month2.setText(getTime(1));

    }

    //파라미터에 따라 0일때 전체 일자 1일때 월 출력
    private String getTime(int x){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        if (x==0)
            return Format.format(mDate);
        else if (x==1)
            return mFormat.format(mDate);
        else
            return "현재일자를 받지 못했습니다.";
    }
}