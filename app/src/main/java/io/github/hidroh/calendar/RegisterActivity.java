package io.github.hidroh.calendar;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by soslt on 2017-10-26.
 */

public class RegisterActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private Spinner spinner;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        spinner = (Spinner) findViewById(R.id.majorSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.major, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        TextView Title = (TextView) findViewById(R.id.title);

        // 타이틀글꼴 변경
        Typeface typeface = Typeface.createFromAsset(getAssets(), "NanumPen.ttf");
        Title.setTypeface(typeface);
        Title.append("회원 가입");
    }

}
