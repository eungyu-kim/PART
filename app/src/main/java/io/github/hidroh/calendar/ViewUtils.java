package io.github.hidroh.calendar;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
/**
 * Created by User on 2017-10-24.
 */

public class ViewUtils { /// 목록에 이벤트 생성 시, 목록의 색깔이 변한다.
    public static int[] getCalendarColors(Context context) {
        int transparentColor = ContextCompat.getColor(context, android.R.color.transparent);
        TypedArray ta = context.getResources().obtainTypedArray(R.array.calendar_colors);
        int[] colors;
        if (ta.length() > 0) {
            colors = new int[ta.length()]; // colors.xml에 있는 색깔 순서대로 color 배열에 저장한 뒤, 목록에 다른 색깔을 부른다.
            for (int i = 0; i < ta.length(); i++) {
                colors[i] = ta.getColor(i, transparentColor);
            }
        } else {
            colors = new int[]{transparentColor};
        }
        ta.recycle();
        return colors;
    }
}
