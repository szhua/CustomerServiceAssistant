package com.pcjh.assistant.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcjh.assistant.R;
import com.pcjh.assistant.base.BaseActivity;
import com.pcjh.assistant.fragment.CollectFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CollectActivity extends BaseActivity {

    @InjectView(R.id.back_bt)
    ImageView backBt;
    @InjectView(R.id.header_title)
    TextView headerTitle;
    @InjectView(R.id.done_bt)
    ImageView doneBt;
    @InjectView(R.id.date)
    TextView date;
    @InjectView(R.id.fragmentcontainer)
    LinearLayout fragmentcontainer;
    private CollectFragment  collectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.inject(this);
        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        collectFragment =new CollectFragment();
        fm.add(R.id.fragmentcontainer,collectFragment );
        fm.commit();

    }
    @Override
    protected void onStart() {
        super.onStart();
        setDoneBtListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new DatePickerDialog(CollectActivity.this,
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
                                Date da=new Date(year-1900,monthOfYear,dayOfMonth);
                                String result =simpleDateFormat.format(da);
                                date.setText(result);
                                collectFragment.dateChanged(result);
                            }
                        }
                        // 设置初始日期
                        , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }
}
