package company.myproject.www.bepatient;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ViewPager mViewPager;

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
    }

    // 툴바에 menu.xml 인플레이트
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    // 받은 뷰페이저 객체에 프레그먼트와 타이틀 정보가 담긴 어댑터 객체를 세트
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab01_EmptyFragment(), "빈탭01");
        adapter.addFragment(new Tab02_EmptyFragment(), "빈탭02");
        adapter.addFragment(new Tab03_EmptyFragment(), "빈탭03");
        adapter.addFragment(new Tab04_EmptyFragment(), "빈탭04");
        viewPager.setAdapter(adapter);
    }
}
