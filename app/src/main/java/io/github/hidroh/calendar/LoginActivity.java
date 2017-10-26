package io.github.hidroh.calendar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by soslt on 2017-10-26.
 */

public class LoginActivity  extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //아이디 가져오기
        ImageButton GoHome = (ImageButton)findViewById(R.id.goHome);
        ImageButton GoToPlan = (ImageButton)findViewById(R.id.goplan);
        ImageButton GoTmap = (ImageButton)findViewById(R.id.goTmap);
        TextView Title = (TextView)findViewById(R.id.title);

        // 타이틀글꼴 변경
        Typeface typeface = Typeface.createFromAsset(getAssets(), "NanumPen.ttf");
        Title.setTypeface(typeface);
        Title.append("PART");

        //홈 화면 이동
        GoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(it);
            }
        });

        //여행계획으로 화면 이동
        GoToPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginActivity.this, PlanActivity.class);
                startActivity(it);
            }
        });

        //길찾기로 화면 이동!! 여기서 TmapMain으로 이동해서 시작
        GoTmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //        Intent it = new Intent(LoginActivity.this, TmapMain.class);
                //          startActivity(it);
            }
        });

    }
}
