package io.github.smu.part;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by kug00 on 2017-11-03.
 */

public class TravelCourse_Detail extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travelcourse_detail);

        //아이디 찾기
        ImageButton travelcourse_detail_back = (ImageButton)findViewById(R.id.travelcourse_detail_back);

        //뒤로 가기기
        travelcourse_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
