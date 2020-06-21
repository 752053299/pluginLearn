package com.example.mytestapp;

import android.util.Log;
import android.view.View;

public class ProxyClickListener implements View.OnClickListener {

    private View.OnClickListener oriListener;
    public ProxyClickListener(View.OnClickListener onClickListener){
        this.oriListener = onClickListener;
    }
    @Override
    public void onClick(View v) {
        Log.i("hook", "被hook到了啊");
        if (oriListener!=null){
            oriListener.onClick(v);
        }
    }
}
