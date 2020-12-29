package com.jxkj.base.utils.photopicker.widget;

import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;

import com.jxkj.base.frame.R;


/**
 * Desc:
 * Author:zhujb
 * Date:2020/5/8
 */
public class MenuButton extends AppCompatButton {
    public MenuButton(Context context) {
        super(context);
        setBackgroundResource(R.drawable.btn_send_bg);
    }

    public void setTitle(String title) {
        setText(title);
    }
}
