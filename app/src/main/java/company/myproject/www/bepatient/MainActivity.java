package company.myproject.www.bepatient;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ViewPager mViewPager;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;
    private boolean swState;

    private Intent notiIntent;
    private PendingIntent notiPend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting");

        // 툴바 관련
        Toolbar mToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar); // 툴바 set
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀 없애고 커스텀 타이틀 적용

        // 탭 : 뷰페이저 with Sections adapter
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager); // 아직 어댑터가 담기지 않은 뷰페이저 전달
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager); // 어댑터가 달린 뷰페이저를 탭 레이아웃에 장착

        // 노티피케이션 터치시 액티비티 실행을 위한 펜딩인텐트 설정
        notiIntent = new Intent(getApplicationContext(), MainActivity.class);
        notiIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 액티비티 중복 실행을 막기 위한 플래그
        notiPend = PendingIntent.getActivity(getApplicationContext(), 0, notiIntent, FLAG_CANCEL_CURRENT); // FLAG_CANCEL_CURRENT 없애면 액티비티 중복 생성됨. 어디서 펜딩인텐트가 여러번 호출되는듯.
    }

    // SharedPreferences를 활용하여 switch 상태를 저장하기 위함.
    // SharedPrefereces는 액티비티가 보이지 않는 onStop 단계에서 저장이 이루어진다.
    @Override
    protected void onStop() {
        super.onStop();
        edit.putBoolean("switchState", swState); // SharedPrefereces에 상태 저장
        edit.apply();
    }

    // 툴바에 menu.xml 인플레이트
    // 스위치 버튼을 툴바에 넣는다.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        // 스위치 상태를 SharedPrefereces에 저장하기 위한
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); // SharedPreferences 생성
        edit = pref.edit(); // SharedPreferences 상태를 수정하기 위한 Editor 생성

        // onoff 스위치 기능 구현
        // inflate되는 menu.xml에서 action되는 layout에 접근해서 id값을 가져와야 함.
        // 이렇게 안 하고 바로 findViewById 하면 null reference 뜸.
        View swLayout = menu.findItem(R.id.mySwitch).getActionView(); // menu에서 findItem으로 item을 가져오고 거기서 Action되는 View를 가져옴.
        Switch mSwitch = swLayout.findViewById(R.id.switchForToolBar); // Action되는 View로부터 스위치 View로 접근.

        mSwitch.setChecked(pref.getBoolean("switchState", false)); // 스위치 초기상태 설정. 저장된 상태가 있다면 그걸로, 없으면 초기값으로.
        swState = pref.getBoolean("switchState", false); // 스위치를 바꾸지 않고 종료했을 경우에도(리스너가 동작하지 않았을때에도) 상태를 남기기 위하여
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                // 스위치 상태에 따른 반응
                if(isChecked) {
                    swState = isChecked; // 스위치 상태 저장
                    notificationControl(isChecked); // 노티피케이션 실행
                } else {
                    swState = isChecked; // 스위치 상태 저장
                    notificationControl(isChecked); // 노티피케이션 정지
                }
            }
        });

        return true;
    }

    // 탭 뷰페이저 : 받은 뷰페이저 객체에 프레그먼트와 타이틀 정보가 담긴 어댑터 객체를 세트
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab01_EmptyFragment(), "빈탭01");
        adapter.addFragment(new Tab02_EmptyFragment(), "빈탭02");
        adapter.addFragment(new Tab03_EmptyFragment(), "빈탭03");
        adapter.addFragment(new Tab04_EmptyFragment(), "빈탭04");
        viewPager.setAdapter(adapter);
    }

    // 노티피케이션 실행을 위한 설정 함수
    private void notificationControl(boolean isChecked) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 노티피케이션 실행
        if(isChecked) {

            // 노티피케이션 설정
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), "M_CH_ID")
                            .setSmallIcon(R.drawable.icon_noti)
                            .setContentTitle("Be patient 실행 중")
                            .setContentText("화면 켜짐 횟수 체크 중")
                            .setContentIntent(notiPend);
            Notification mNotification = mBuilder.build();
            mNotification.flags = Notification.FLAG_NO_CLEAR;
            mNotificationManager.notify(0, mNotification); // 노티피케이션 등록
        }
        else { // 노티피케이션 정지
            mNotificationManager.cancel(0);
        }

    }
}
