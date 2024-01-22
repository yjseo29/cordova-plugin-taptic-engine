package org.apache.cordova;

import android.os.Build;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

public class TapticEngine extends CordovaPlugin {
    View view;

    private static final String TAG = "TapticEngine";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        try {
            view = (View) webView.getClass().getMethod("getView").invoke(webView);
        } catch (Exception e) {
            view = (View) webView;
        }
    }

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "action : " + action);
        Log.d(TAG, "args : " + args.toString());

        if ("selection".equals(action)) {
            HapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
        } else if ("notification".equals(action)) {
            int type;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if ("warning".equals(args.getString(0)) || "error".equals(args.getString(0))) {
                    type = HapticFeedbackConstants.REJECT;
                } else {
                    type = HapticFeedbackConstants.CONFIRM;
                }
            } else {
                type = HapticFeedbackConstants.VIRTUAL_KEY;
            }
            HapticFeedback(type);
        } else if ("impact".equals(action)) {
            HapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        } else if ("gestureSelectionStart".equals(action) || "gestureSelectionChanged".equals(action) || "gestureSelectionEnd".equals(action)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                int type = HapticFeedbackConstants.GESTURE_START;
                if ("gestureSelectionChanged".equals(action)) {
                    type = HapticFeedbackConstants.CLOCK_TICK;
                } else if ("gestureSelectionEnd".equals(action)) {
                    type = HapticFeedbackConstants.GESTURE_END;
                }
                HapticFeedback(type);
            } else {
                callbackContext.error("Requires API level 30 or higher");
                return false;
            }
        } else {
            return false;
        }

        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
        return true;
    }

    /**
     * HapticFeedback https://developer.android.com/reference/android/view/HapticFeedbackConstants
     * @param hapticFeedbackType HapticFeedbackConstants
     */
    void HapticFeedback(int hapticFeedbackType) {
        view.performHapticFeedback(hapticFeedbackType);
    }
}