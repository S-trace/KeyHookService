package com.s_trace.keyhookservice;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class KeyHookService extends AccessibilityService {
    private static final String TAG = KeyHookService.class.getSimpleName();

    @Override
    public boolean onKeyEvent(final KeyEvent event) {
        Log.d(TAG, "onKeyEvent(KeyEvent=" + event + ")");
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                Log.d(TAG, "KEYCODE_VOLUME_UP released");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Instrumentation inst = new Instrumentation();
                        try {
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP);
                        } catch (Exception e) {
                            Log.e(TAG, "KEYCODE_VOLUME_UP handle failed with Exception", e);
                        }
                    }
                }).start();
            }
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d(TAG, "KEYCODE_VOLUME_DOWN released");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Instrumentation inst = new Instrumentation();
                        UiAutomation automation = inst.getUiAutomation();
                        if (automation == null) {
                            Log.e(TAG, "automation == null");
                            return;
                        }
                        KeyEvent upEvent = KeyEvent.changeAction(event, KeyEvent.ACTION_UP);
                        automation.injectInputEvent(event, true);
                        automation.injectInputEvent(upEvent, true);
                    }
                }).start();
            }
            return true;
        }

        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    Log.d(TAG, KeyEvent.keyCodeToString(keyCode) + " pressed");
                    return true;
            }
            return true;
        }

        return super.onKeyEvent(event);
    }

    @Override
    public void onServiceConnected() {
        Log.v(TAG, "onServiceConnected()");
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt()");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v(TAG, "onAccessibilityEvent(AccessibilityEvent=" + event + ")");
    }
}