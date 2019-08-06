package com.fnc.order.android.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fnc.order.android.R;

public class PopupRemarks extends RelativePopupWindow {

    LinearLayout msgBox;
    TextView infoMsg;
    EditText remarksMsg;

    public PopupRemarks(Context context) {
        setContentView(LayoutInflater.from(context).inflate(R.layout.popup_remarks, null));

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public LinearLayout getBox() {
        msgBox = (LinearLayout) getContentView().findViewById(R.id.remarks_msg);
        return msgBox;
    }

    public TextView getInfoMessage() {
        infoMsg = (TextView) getContentView().findViewById(R.id.tv_infomsg);
        return infoMsg;
    }

    public EditText getRemarksBox() {
        remarksMsg = (EditText) getContentView().findViewById(R.id.et_remarkmsg);
        return remarksMsg;
    }

    @Override
    public void showOnAnchor(@NonNull View anchor, int vertPos, int horizPos, int x, int y) {
        super.showOnAnchor(anchor, vertPos, horizPos, x, y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //circularReveal(anchor);
        }
    }
}

