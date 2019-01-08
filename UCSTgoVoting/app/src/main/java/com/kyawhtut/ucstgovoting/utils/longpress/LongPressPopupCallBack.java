package com.kyawhtut.ucstgovoting.utils.longpress;

import android.view.MotionEvent;
import android.view.View;

public abstract class LongPressPopupCallBack implements LongPressPopupInterface {

    @Override
    public void onPressStart(View pressedView, MotionEvent motionEvent) {
    }

    @Override
    public void onPressContinue(int progress, MotionEvent motionEvent) {
    }

    @Override
    public void onPressStop(MotionEvent motionEvent) {
    }

    @Override
    public void onLongPressStart(MotionEvent motionEvent) {
    }

    @Override
    public void onLongPressContinue(int longPressTime, MotionEvent motionEvent) {
    }

    @Override
    public void onLongPressEnd(MotionEvent motionEvent) {
    }
}
