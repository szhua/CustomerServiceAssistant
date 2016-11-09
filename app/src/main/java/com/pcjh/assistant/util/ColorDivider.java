//package com.pcjh.assistant.util;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.Rect;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.support.annotation.ColorRes;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//
//import com.pcjh.assistant.db.DbManager;
//import com.pcjh.assistant.entity.Label;
//import com.pcjh.assistant.entity.RConact;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import rx.Observable;
//import rx.functions.Func2;
//
///**
// * Created by szhua on 2016/10/26.
// */
//public class ColorDivider extends RecyclerView.ItemDecoration {
//    private static final int[] ATTRS = new int[]{
//            android.R.attr.listDivider
//    };
//
//    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
//
//    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
//
//    private Drawable mDivider;
//
//    private int mOrientation;
//
//    public ColorDivider(Context context, int orientation ,Resources resources ,@ColorRes int color) {
//        final TypedArray a = context.obtainStyledAttributes(ATTRS);
//        mDivider = new ColorDrawable(resources.getColor(color));
//        a.recycle();
//        setOrientation(orientation);
//    }
//
//    public void setOrientation(int orientation) {
//        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
//            throw new IllegalArgumentException("invalid orientation");
//        }
//        mOrientation = orientation;
//    }
//
//    @Override
//    public void onDraw(Canvas c, RecyclerView parent) {
//        if (mOrientation == VERTICAL_LIST) {
//            drawVertical(c, parent);
//        } else {
//            drawHorizontal(c, parent);
//        }
//
//    }
//
//
//    public void drawVertical(Canvas c, RecyclerView parent) {
//        final int left = parent.getPaddingLeft();
//        final int right = parent.getWidth() - parent.getPaddingRight();
//
//        final int childCount = parent.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            final View child = parent.getChildAt(i);
//            android.support.v7.widget.RecyclerView v = new android.support.v7.widget.RecyclerView(parent.getContext());
//            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
//                    .getLayoutParams();
//            final int top = child.getBottom() + params.bottomMargin;
//            final int bottom = top + mDivider.getIntrinsicHeight();
//            mDivider.setBounds(left, top, right, bottom);
//            mDivider.draw(c);
//        }
//    }
//
//    public void drawHorizontal(Canvas c, RecyclerView parent) {
//        final int top = parent.getPaddingTop();
//        final int bottom = parent.getHeight() - parent.getPaddingBottom();
//
//        final int childCount = parent.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            final View child = parent.getChildAt(i);
//            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
//                    .getLayoutParams();
//            final int left = child.getRight() + params.rightMargin;
//            final int right = left + mDivider.getIntrinsicHeight();
//            mDivider.setBounds(left, top, right, bottom);
//            mDivider.draw(c);
//        }
//    }
//
//    @Override
//    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
//        if (mOrientation == VERTICAL_LIST) {
//            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
//        } else {
//            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
//        }
//    }
//
//    /**
//     *
//     *  subscription = Observable.just(AppHolder.getInstance().getUsers().getUin()).map(new Func1<String, Users>() {
//    @Override
//    public Users call(String s) {
//    return getUserFromDatabase(AppHolder.getInstance().getUsers().getUin());
//    }
//    }).flatMap(new Func1<Users, Observable<?>>(){
//    @Override
//    public Observable<?> call(Users users) {
//    /**
//     * 通过zip的方法将多个线程一起进行
//     */
//    return Observable.zip(getConnactLabelIds(users), getRconacts(users), new Func2<List<Label>, List<RConact>, Object>() {
//        @Override
//        public Object call(List<Label> labels, List<RConact> rConacts) {
//            /**
//             * 在这里把数据进行统一的处理；
//             */
//            contactLabelList = (ArrayList<Label>) labels;
//            rConnactsList = (ArrayList<RConact>) rConacts;
//
//            if(rConnactsList!=null&&rConnactsList.size()>0){
//                Log.i("szhua","size11"+rConnactsList.size());
//            }
//            try{
//
//                DbManager db =new DbManager(TimerActivity.this) ;
//                ArrayList<RConact> data = (ArrayList<RConact>) db.quryForRconacts();
//                /**
//                 * data 于contactLabelList 比较得出新增或者删除的联系人;
//                 * 使用ArrayList的remove方法 ;
//                 */
//                rConnactsList.removeAll(data) ;
//                if(rConnactsList!=null&&rConnactsList.size()>0)
//                    Log.i("szhua","data"+rConnactsList.get(0).getNickname()) ;
//                if(data!=null&&data.size()>0){
//                    Log.i("szhua","size"+data.size());
//                }
//                // db.addRconact(rConnactsList);
//            }catch (Exception e){
//                Log.i("szhua",e.toString());
//            };
//            return null;
//        }
//    });
//}
//}) .subscribeOn(Schedulers.io())
//        .observeOn(Schedulers.io())
//        .subscribe(new Subscriber<Object>() {
//@Override
//public void onCompleted() {
//        }
//@Override
//public void onError(Throwable e) {
//        Log.i("leilei","erro1"+e.toString()) ;
//        }
//@Override
//public void onNext(Object o) {
//        if(SharedPrefsUtil.getValue(TimerActivity.this,"lastCreateTime",0L)!=0){
//        getMessageFromWxinFirst();
//        }
//        getMessageFromWxin();
//        }
//        }) ;
//     */
//
//
//
//}
//
