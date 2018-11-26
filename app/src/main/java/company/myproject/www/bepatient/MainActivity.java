package company.myproject.www.bepatient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
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
    private ViewPager mViewPager;
    private SectionsPageAdapter adapter; // 프래그먼트를 전환시킬 어댑터
    private Intent serviceIntent;

    /**
     * ServiceBinding 관련 시작
     */
    ScreenCountService countService; // 화면켜짐카운트 서비스 클래스
    Boolean isBinding; // 서비스바인딩 여부 확인용 변수
    ServiceConnection conn = new ServiceConnection() {
        // 서비스바인딩 할 때 던져줄 커넥션 객체
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // 서비스바인딩 성공
            ScreenCountService.ScreenCountBinder serviceBinder = (ScreenCountService.ScreenCountBinder) iBinder;
            countService = serviceBinder.getService(); // ScreenCountService에서 구현한 Binder에서 서비스의 함수에 접근할 수 있도록 마련해 둔 getService()에 접근.
            //saveBindServiceState(true); // 서비스바인딩 상태 저장
            isBinding = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 예기치 않게 서비스와 연결이 끊기거나 종료되었을 때 호출되는 메서드. unbindService했다고 호출되지는 x.
            //saveBindServiceState(false); // 서비스바인딩 상태 저장
            isBinding = false;
        }
    };
    /**
     * ServiceBinding 관련 종료
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

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

        // 서비스 시작용 인텐트
        serviceIntent = new Intent(getApplicationContext(), ScreenCountService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // onStop은 스마트폰 화면만 꺼도 호출 됨.(액티비티가 전면에 없으면 무조건 호출)
    @Override
    protected void onStop() {
        super.onStop();
        saveSwitchState(loadSwitchState()); // 화면 안 보일 때 스위치 상태 저장
        //saveBindServiceState(loadBindServiceState()); // 화면 안 보일 때 서비스바인딩 상태 저장.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //saveBindServiceState(false); // 어플 종료되면 서비스바인딩도 해제되니까 false로 저장.
    }

    /**
     * 툴바에 menu.xml 인플레이트
     * 스위치 버튼을 툴바에 넣는다.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        /**
         * OnOff 스위치 기능 구현 관련 시작
         */
        View swLayout = menu.findItem(R.id.mySwitch).getActionView(); // menu에서 findItem으로 item을 가져오고 거기서 Action되는 View를 가져옴.
        Switch mSwitch = swLayout.findViewById(R.id.switchForToolBar); // Action되는 View로부터 스위치 View로 접근. 바로 findView.. 하면 Null Ref...뜸

        if(loadSwitchState()) { // 만약 저장된 스위치 상태(변수값)가 true 라면(초기값은 false)
            mSwitch.setChecked(true); // 스위치 초기 상태를 On으로 표시
            //if(!loadBindServiceState()) { // 스위치 초기 상태가 On인데 서비스는 바인딩되어 있지 않다면
//            if(!isBinding) { // 스위치 초기 상태가 On인데 서비스는 바인딩되어 있지 않다면
//                bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE); // 서비스바인드 시작
//                //saveBindServiceState(true);
//            }
        }

        // 스위치 OnOff 리스너
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) { // 스위치 on
                    bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE); // 서비스바인드 시작
                    startService(serviceIntent); // 화면켜짐카운트 누적시키는 서비스 시작
                    saveSwitchState(isChecked); // 스위치 상태를 'pref_MainActivity' 파일에 저장
                } else { // 스위치 off
                    stopService(serviceIntent);
                    if(loadBindServiceState()) { // 현재 바인드서비스가 돌고 있다면
                        unbindService(conn); // 서비스 종료
                        //saveBindServiceState(false); // 서비스바인딩 상태 저장
                        isBinding = false;
                    }
                    saveSwitchState(isChecked); // 스위치 상태와 서비스바인딩여부 'pref_MainActivity' 파일에 저장
                }
            }
        });
        /**
         * OnOff 스위치 기능 구현 관련 종료
         */

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

    // 현재 스위치 상태를 'pref_MainActivity' 파일에 저장시키는 함수
    private void saveSwitchState(Boolean sw) {
        SharedPreferences pref = getSharedPreferences("pref_MainActivity", Activity.MODE_PRIVATE); // 스위치 상태를 저장해둔 pref 파일 가져오기
        SharedPreferences.Editor editor = pref.edit(); // SharedPreferences 상태를 수정하기 위한 Editor 생성
        editor.putBoolean("switchState", sw); // switchState 변수에 스위치 상태값 저장
        editor.apply();
    }

    // 현재 서비스바인딩 상태를 'pref_MainActivity' 파일에 저장시키는 함수
    private void saveBindServiceState(Boolean mBind) {
        SharedPreferences pref = getSharedPreferences("pref_MainActivity", Activity.MODE_PRIVATE); // 스위치 상태를 저장해둔 pref 파일 가져오기
        SharedPreferences.Editor editor = pref.edit(); // SharedPreferences 상태를 수정하기 위한 Editor 생성
        editor.putBoolean("serviceBindState", mBind);
        editor.apply();
    }

    // 스위치의 현재 상태를 불러오기 위한 함수
    public boolean loadSwitchState() {
        SharedPreferences pref = getSharedPreferences("pref_MainActivity", Activity.MODE_PRIVATE); // 스위치 상태를 저장해둔 pref 파일 가져오기
        return pref.getBoolean("switchState", false); // switchState 변수의 값을 return. 초기값은 false.
    }

    // 서비스바인딩 현재 상태를 불러오기 위한 함수
    public boolean loadBindServiceState() {
        SharedPreferences pref = getSharedPreferences("pref_MainActivity", Activity.MODE_PRIVATE); // 스위치 상태를 저장해둔 pref 파일 가져오기
        return pref.getBoolean("serviceBindState", false); // serviceBindState 변수의 값을 return. 초기값은 false.
    }
}
