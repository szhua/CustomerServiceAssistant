package com.pcjh.assistant.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pcjh.assistant.R;
import com.pcjh.assistant.base.BaseActivity;
import com.pcjh.liabrary.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CheckPhotoActivity extends BaseActivity {

    @InjectView(R.id.viewpager)
    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_check_photo);
        ButterKnife.inject(this);

        viewpager.setAdapter(new CheckAdapter());


    }



    class  CheckAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return  view ==object ;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView =new PhotoView(CheckPhotoActivity.this) ;
            Picasso.with(CheckPhotoActivity.this).load("http://ww2.sinaimg.cn/large/610dc034jw1f9b46kpoeoj20ku0kuwhc.jpg").placeholder(R.mipmap.ic_launcher).into(photoView);
            photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(object!=null){
                container.removeView((View) object);
            }
        }
    }
}
