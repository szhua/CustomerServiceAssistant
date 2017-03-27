package com.pcjh.assistant.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 可嵌套在ScrollView中使用的GridView
 * （解决加入该控件后，ScrollView默认不是置顶状态的方法是：gridView.setFocusable(false);）
 */
public class NoScrollGridView extends GridView {

    public NoScrollGridView(Context context) {
        super(context);
    }

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    ;;
    public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //父控件能给出的最大尺寸为测量的最大值，AT_MOST 尺寸按照子控件的大小核算
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
