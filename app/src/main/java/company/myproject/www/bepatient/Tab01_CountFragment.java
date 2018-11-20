package company.myproject.www.bepatient;

import android.os.Bundle;
import android.support.annotation.Nullable;
<<<<<<< HEAD
=======
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
>>>>>>> 44382871f05eaca501eeb60dfe5d79fdf1de3e8a
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 1번 탭
 * 화면켜짐횟수 디스플레이 구현
 */
public class Tab01_CountFragment extends android.support.v4.app.Fragment {

    public static final String TAG = "Tab01_CountFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Tab01_CountFragment : onCreateView");
        View view = inflater.inflate(R.layout.fragment_tab01, container, false);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 프래그먼트를 뗏다 붙였다 하며 화면을 갱신하는 것 처럼 보이게 연출
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(Tab01_CountFragment.this);
                ft.attach(Tab01_CountFragment.this);
                ft.commitAllowingStateLoss();
            }
        }, 1000); // 1초에 한 번씩 계속 화면을 뗏다 붙였다... 실시간 갱신 처럼 보이게

        TextView mTextView = view.findViewById(R.id.screenOnCount); // 텍스트뷰 접근

        // 텍스트 변경
<<<<<<< HEAD
        if(!((MainActivity)getActivity()).mBound) { // 현재 서비스가 돌고 있지 않으면
            mTextView.setTextSize(32);
            mTextView.setText("No signal");
        } else if(((MainActivity)getActivity()).mBound) { // 현재 서비스가 돌고 있으면
            mTextView.setText("" + ((MainActivity)getActivity()).countService.getScreenOnCount());
        }
=======
        mTextView.setText("" + ScreenCountService.getScreenOnCount());
>>>>>>> 44382871f05eaca501eeb60dfe5d79fdf1de3e8a

        return view;
    }
}
