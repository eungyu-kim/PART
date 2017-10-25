package io.github.hidroh.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kug00 on 2017-10-15.
 */

public class GetTime {
    long mNow;
    Date mDate;
    //전체 일자 출력
    SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat mFormat = new SimpleDateFormat("MM");
    SimpleDateFormat kFormat = new SimpleDateFormat("yyyy년MM월dd일");

    //파라미터에 따라 0일때 전체 일자 1일때 월 출력
    public String getTime(int x){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        if (x==0)
            return Format.format(mDate);
        else if (x==1)
            return mFormat.format(mDate);
        else
            return "현재일자를 받지 못했습니다.";
    }

    public String DateParse(String StringDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년MM월dd일");
        try {
            Date date = format.parse(StringDate);
            String dateTime = dateFormat.format(date);
            return dateTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
