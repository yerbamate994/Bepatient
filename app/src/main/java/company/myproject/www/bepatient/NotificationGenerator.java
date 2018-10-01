package company.myproject.www.bepatient;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationGenerator {

    private static final String TAG = "NotificationGenerator";
    private Context mContext; // 다른 액티비티의 Context를 얻어와서 쓰기 위함

    public NotificationGenerator(Context mContext) {
        Log.d(TAG, "NotificationGeneratorConstructor : Starting");

        this.mContext = mContext;
    }

    // 노티피케이션 설정 및 실행을 위한 함수
    public void notificationControl(boolean isChecked) {
        Log.d(TAG, "notificationControl : Starting");

        // 노티피케이션 터치시 액티비티 실행을 위한 펜딩인텐트 설정
        Intent mIntent = new Intent(mContext, MainActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 액티비티 중복 실행을 막기 위한 플래그
        PendingIntent mPendingIntent =
                PendingIntent.getActivity(mContext, 0, mIntent, FLAG_CANCEL_CURRENT);
        // FLAG_CANCEL_CURRENT 없애면 액티비티 중복 생성됨. 어디서 펜딩인텐트가 여러번 호출되는듯.

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE); // 노티피케이션 매니저 획득

        // 노티피케이션 실행 분기
        if (isChecked) {
            // 노티피케이션 설정
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext, "M_CH_ID")
                            .setSmallIcon(R.drawable.icon_noti)
                            .setContentTitle("Be patient 실행 중")
                            .setContentText("화면 켜짐 횟수 체크 중")
                            .setContentIntent(mPendingIntent);
            Notification mNotification = mBuilder.build();
            mNotification.flags = Notification.FLAG_NO_CLEAR; // 노티피케이션 삭제 안 되도록
            mNotificationManager.notify(0, mNotification); // 노티피케이션 실행
        } else {
            mNotificationManager.cancel(0); // 노티피케이션 정지
        }
    }
}
