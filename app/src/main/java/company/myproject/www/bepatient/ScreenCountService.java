package company.myproject.www.bepatient;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

public class ScreenCountService extends Service {

    public static final String TAG ="ScreenCountService";

    // 서비스가 시작되면 onStartCommand가 호출 됨.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(1, setNotification()); // TaskKiller에 서비스가 죽지 않도록 하기 위하여
        // + 노티피케이션 실행

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true); // Foreground 죽이고, 노티피케이션도 죽임.
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 노티피케이션 설정 함수
    public Notification setNotification() {

        // 노티피케이션 터치시 액티비티 실행을 위한 인텐트 설정
        Intent mIntent = new Intent(this, MainActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 액티비티 중복 실행을 막기 위한 플래그

        // 설정된 인텐트를 펜딩인텐트에 set
        PendingIntent mPendingIntent =
                PendingIntent.getActivity(this, 0, mIntent, FLAG_CANCEL_CURRENT);
        // FLAG_CANCEL_CURRENT 없애면 액티비티 중복 생성됨. 어디서 펜딩인텐트가 여러번 호출되는듯.

        // 이것저것 노티피케이션 설정
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "M_CH_ID")
                        .setSmallIcon(R.drawable.icon_noti)
                        .setContentTitle("Be patient 실행 중")
                        .setContentText("화면 켜짐 횟수 체크 중")
                        .setContentIntent(mPendingIntent);

        Notification mNotification = mBuilder.build(); // 지금까지 설정한 Builder를 Build해서 Notification에 부착.
        mNotification.flags = Notification.FLAG_NO_CLEAR; // 노티피케이션 삭제 안 되도록 플래그 설정

        return mNotification; // 완성된 노티피케이션 덩어리를 리턴
    }
}
