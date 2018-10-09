package company.myproject.www.bepatient;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * 1번 탭
 * 화면켜짐횟수 디스플레이 구현
 */
public class Tab01_CountFragment extends android.support.v4.app.Fragment {

    public static final String TAG = "Tab01_CountFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab01, container, false);

        TextView mTextView = view.findViewById(R.id.screenOnCount); // 텍스트뷰 접근

        Handler mHandler = new Handler(); // 실시간으로 숫자가 변화함을 보이기 위한 핸들러
        mHandler.postDelayed(new Runnable() {
            //Do Something
            @Override
            public void run() {
                // TODO Auto-generated method stub
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(Tab01_CountFragment.this).attach(Tab01_CountFragment.this).commitAllowingStateLoss();
            }
        }, 1000); // 밀리세컨드(1000분의 1초)

        if(!((MainActivity)getActivity()).isService) { // 현재 서비스가 돌고 있지 않으면
            mTextView.setTextSize(32);
            mTextView.setText("스위치를 켜세요!" +
                    "");
        } else if(((MainActivity)getActivity()).isService) { // 현재 서비스가 돌고 있으면
            mTextView.setText("" + ((MainActivity)getActivity()).countService.getScreenOnCount());
        }

        return view;
    }
}
