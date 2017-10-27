package io.github.hidroh.calendar;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by soslt on 2017-10-27.
 */

public class RegisterRequest extends StringRequest {

    final static private String URL = "http://prtrip.cafe24.com/UserRegister.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userPassword, String userBirthday, String userGender, String userAge, String userEmail, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userBirthday", userBirthday);
        parameters.put("userGender", userGender);
        parameters.put("userAge", userAge);
        parameters.put("userEmail", userEmail);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
