package io.github.smu.part;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by soslt on 2017-10-20.
 */

public class BaseActivity extends Activity {
    private static Typeface mTypeface;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        if (BaseActivity.mTypeface == null) {
            BaseActivity.mTypeface = Typeface.createFromAsset(getAssets(), "NanumPen.ttf");
        }

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
    }

    void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(mTypeface);
            }
            else if (child instanceof ViewGroup) {
                setGlobalFont((ViewGroup) child);
            }
        }
    }
}