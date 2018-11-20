package company.myproject.www.bepatient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Context mContext; // MainActivity 컨텍스트

    private ViewPager mViewPager;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;
    private boolean swState; // 스위치 상태 저장용
    private SectionsPageAdapter adapter; // 프래그먼트를 전환시킬 어댑터

    /**
     * ServiceBinding
     */
    ScreenCountService countService; // 화면켜짐카운트 서비스
    boolean mBound = false; // 서비스바인딩 여부 확인
    ServiceConnection conn = new ServiceConnection() {
        // 서비스바인딩 할 때 던져줄 커넥션 객체
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // 서비스바인딩 성공
            ScreenCountService.ScreenCountBinder serviceBinder = (ScreenCountService.ScreenCountBinder) iBinder;
            countService = serviceBinder.getService(); // ScreenCountService에서 구현한 Binder에서 서비스의 함수에 접근할 수 있도록 마련해 둔 getService()에 접근.
            mBound = true; // 서비스 실행 여부 판단
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 예기치 않게 서비스와 연결이 끊기거나 종료되었을 때 호출되는 메서드. unbindService했다고 호출되지는 x.
            mBound = false; // 서비스 실행 여부 판단
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate : Starting");

        mContext = this;

        // 툴바 관련
        Toolbar mToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar); // 툴바 set
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀 없애고 커스텀 타이틀 적용

        // 탭 : 뷰페이저 with Sections adapter
        adapter = new SectionsPageAdapter(getSupportFragmentManager()); // 만들어둔 Section... 객체 생성
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager); // 아직 어댑터가 담기지 않은 뷰페이저 전달
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager); // 어댑터가 달린 뷰페이저를 탭 레이아웃에 장착

        // 화면켜짐 카운트 상황을 거의 실시간으로 보여주기 위한 핸들러 구현
        // 핸들러 이건 지연시켜 실행시키는거지 계속 실행시키는게 아님. 계속 돌릴 방법 강구.
        Handler mHandler = new Handler(); // 실시간으로 숫자가 변화함을 보이기 위한 핸들러
        mHandler.postDelayed(new Runnable() {
            //Do Something
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.d(TAG, "Handler Run");
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(adapter.getItem(0));
                ft.attach(adapter.getItem(0));
                ft.commit();
            }
        }, 1000); // 밀리세컨드(1000분의 1초)
    }

    // SharedPreferences를 활용하여 switch 상태를 저장하기 위함.
    // SharedPrefereces는 액티비티가 보이지 않는 onStop 단계에서 저장이 이루어진다.
    @Override
    protected void onStop() {
        super.onStop();
        edit.putBoolean("saveState", swState); // SharedPrefereces에 상태 저장
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

        mSwitch.setChecked(pref.getBoolean("saveState", false)); // 스위치 초기상태 설정. 저장된 상태가 있다면 그걸로, 없으면 초기값으로.
        swState = pref.getBoolean("saveState", false); // 스위치를 바꾸지 않고 종료했을 경우에도(리스너가 동작하지 않았을때에도) 상태를 남기기 위하여
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Intent intent = new Intent(getApplicationContext(), ScreenCountService.class);

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) { 
                    // 스위치 on
                    swState = isChecked; // 스위치 상태 저장
                    bindService(intent, conn, Context.BIND_AUTO_CREATE); // 서비스 시작
                } else { 
                    // 스위치 off
                    swState = isChecked; // 스위치 상태 저장
                    if(mBound) { // 현재 서비스가 돌고 있다면
                        unbindService(conn); // 서비스 종료
                        mBound = false; // 서비스바인딩 해제 알림
                    }
                }
            }
        });

        return true;
    }

    // 탭 뷰페이저 : 받은 뷰페이저 객체에 프레그먼트와 타이틀 정보가 담긴 어댑터 객체를 세트
    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new Tab01_CountFragment(), "카운트");
        adapter.addFragment(new Tab02_EmptyFragment(), "빈탭02");
        adapter.addFragment(new Tab03_EmptyFragment(), "빈탭03");
        adapter.addFragment(new Tab04_EmptyFragment(), "빈탭04");
        viewPager.setAdapter(adapter);
    }
}
