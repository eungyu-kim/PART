package io.github.hidroh.calendar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Downloader;

import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by soslt on 2017-10-26.
 */

public class LoginActivity  extends AppCompatActivity {

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //아이디 가져오기
        ImageButton GoHome = (ImageButton)findViewById(R.id.goHome);
        ImageButton GoToPlan = (ImageButton)findViewById(R.id.goplan);
        ImageButton GoTmap = (ImageButton)findViewById(R.id.goTmap);
        TextView Title = (TextView)findViewById(R.id.title);
        TextView RegisterButton = (TextView) findViewById(R.id.registerButton);

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
                Intent it = new Intent(LoginActivity.this, TmapMain.class);
                startActivity(it);
            }
        });

        //회원가입으로 화면 이동
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(it);
            }
        });

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();

                Response.Listener<String> responseLister = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("로그인에 성공했습니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                Intent intent = new Intent(LoginActivity.this, MyPage.class);
                                LoginActivity.this.startActivity(intent);
                                finish();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("계정을 다시 확인하세요.").setNegativeButton("다시 시도", null).create();
                                dialog.show();
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseLister);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}
