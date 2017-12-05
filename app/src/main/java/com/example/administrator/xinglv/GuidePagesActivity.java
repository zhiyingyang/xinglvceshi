package com.example.administrator.xinglv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

//滑动gif页
public class GuidePagesActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.vp_guide)
    public ViewPager vpGuide;
    private int[] gifId = {R.mipmap.ceshi, R.mipmap.ceshi1, R.mipmap.ceshi2};
    //是否在最后一个页面
    private boolean flag;
    //是否跳转过去了
    private boolean flagActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_pages);
        ButterKnife.bind(this);
        intView();
    }

    //初始化
    public void intView() {
        List<View> mViewList = new ArrayList<View>();
        for (int i = 0; i < gifId.length; i++) {
            GifImageView gifImageView = new GifImageView(getApplicationContext());
            gifImageView.setBackgroundResource(gifId[i]);
            mViewList.add(gifImageView);
        }
        //mViewList.add(new View(getApplicationContext()));

        vpGuide.setAdapter(new AdapterViewpager(mViewList));
        vpGuide.addOnPageChangeListener(this);
    }

    //ViewPager滑动监听
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 2 && flag && flagActivity) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            flagActivity = false;
        }
        if (position == 2) {
            flag = true;
        }

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class AdapterViewpager extends PagerAdapter {
        private List<View> mViewList;

        public AdapterViewpager(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {//必须实现
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {//必须实现
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {//必须实现，实例化
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {//必须实现，销毁
            container.removeView(mViewList.get(position));
        }
    }


}
