package com.g.openglstudy.fliter;

import android.content.Context;
import android.util.Log;

import com.g.openglstudy.R;


public class ScreenFilter extends BaseFilter {
    private static final String TAG = "ScreenFilter";

    public ScreenFilter(Context mContext) {
        super(mContext, R.raw.screen_vert, R.raw.screen_frag);
        Log.i("Magic-"+TAG,"ScreenFilter---11");

    }


}
