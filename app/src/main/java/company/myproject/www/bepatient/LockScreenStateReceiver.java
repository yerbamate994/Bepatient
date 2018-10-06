package company.myproject.www.bepatient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 스마트폰 잠금 해제하면 카운트가 쌓이도록 하는 리시버 클래스 정의
 */
public class LockScreenStateReceiver extends BroadcastReceiver {

    public static final String TAG = "LockScreenStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            Log.i(TAG, "Intent.ACTION_USER_PRESENT # onReceive");

        }
    }
}
