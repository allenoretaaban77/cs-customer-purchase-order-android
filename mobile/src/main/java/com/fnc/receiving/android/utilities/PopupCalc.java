package com.fnc.receiving.android.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.fnc.receiving.android.R;

public class PopupCalc extends RelativePopupWindow {

    Button btn;

    public PopupCalc(Context context) {
        setContentView(LayoutInflater.from(context).inflate(R.layout.popup_calcu, null));

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(false);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public Button getBtn1() { btn = (Button) getContentView().findViewById(R.id.cal_1); return btn; }
    public Button getBtn2() { btn = (Button) getContentView().findViewById(R.id.cal_2); return btn; }
    public Button getBtn3() { btn = (Button) getContentView().findViewById(R.id.cal_3); return btn; }
    public Button getBtn4() { btn = (Button) getContentView().findViewById(R.id.cal_4); return btn; }
    public Button getBtn5() { btn = (Button) getContentView().findViewById(R.id.cal_5); return btn; }
    public Button getBtn6() { btn = (Button) getContentView().findViewById(R.id.cal_6); return btn; }
    public Button getBtn7() { btn = (Button) getContentView().findViewById(R.id.cal_7); return btn; }
    public Button getBtn8() { btn = (Button) getContentView().findViewById(R.id.cal_8); return btn; }
    public Button getBtn9() { btn = (Button) getContentView().findViewById(R.id.cal_9); return btn; }
    public Button getBtn0() { btn = (Button) getContentView().findViewById(R.id.cal_0); return btn; }
    public Button getBtnDot() { btn = (Button) getContentView().findViewById(R.id.cal_dot); return btn; }
    public Button getBtnPlus() { btn = (Button) getContentView().findViewById(R.id.cal_plus); return btn; }
    public Button getBtnDelete() { btn = (Button) getContentView().findViewById(R.id.cal_delete); return btn; }
    public Button getBtnCancel() { btn = (Button) getContentView().findViewById(R.id.cal_cancel); return btn; }

    @Override
    public void showOnAnchor(@NonNull View anchor, int vertPos, int horizPos, int x, int y) {
        super.showOnAnchor(anchor, vertPos, horizPos, x, y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //circularReveal(anchor);
        }
    }
}
