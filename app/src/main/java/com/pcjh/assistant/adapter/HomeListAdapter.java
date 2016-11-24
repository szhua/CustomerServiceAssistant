package com.pcjh.assistant.adapter;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.dialog.ProgressHUD;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.R;
import com.pcjh.assistant.activity.CheckPhotoActivity;
import com.pcjh.assistant.dao.AddMaterialFavoriteCountDao;
import com.pcjh.assistant.dao.AddMaterialRepostCountDao;
import com.pcjh.assistant.dao.RemoveMaterialFavoriteCountDao;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.HomeEntity;
import com.pcjh.assistant.entity.Image;
import com.pcjh.assistant.entity.Matrial;
import com.pcjh.assistant.fragment.CollectFragment;
import com.pcjh.assistant.fragment.HomeFragment;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.liabrary.ninegridimgview.NineGridImageView;
import com.pcjh.liabrary.ninegridimgview.NineGridImageViewAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by szhua on 2016/10/28.
 */
public class HomeListAdapter extends RecyclerView.Adapter implements INetResult {

    private Context context;
    private LayoutInflater inflater;
    private int  IMAGE_NAME;
    private ArrayList<Matrial> matrialArrayList =new ArrayList<>() ;
    private AddMaterialFavoriteCountDao addMaterialFavoriteCountDao ;
    private RemoveMaterialFavoriteCountDao removeMaterialFavoriteCountDao ;
    private AddMaterialRepostCountDao addMaterialRepostCountDao ;
    DbManager dbManager ;
    private ProgressHUD  mProgressHUD;

    /**
     * 显示加载进度条
     * @param show
     */
    public void showProgress(boolean show) {
        showProgressWithText(show, "加载中...");
    }

    /**
     * 显示加载进度条
     *
     * @param show
     * @param message
     */
    public void showProgressWithText(boolean show, String message) {
        if (show) {
            mProgressHUD = ProgressHUD.show(context, message, true, true, null);
        } else {
            if (mProgressHUD != null) {
                mProgressHUD.dismiss();
            }
        }
    }



    public void setMatrialArrayList(ArrayList<Matrial> matrialArrayList) {
        this.matrialArrayList = matrialArrayList;
        notifyDataSetChanged();
    }
    public HomeListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        addMaterialFavoriteCountDao =new AddMaterialFavoriteCountDao(context,this);
        removeMaterialFavoriteCountDao =new RemoveMaterialFavoriteCountDao(context,this) ;
        addMaterialRepostCountDao =new AddMaterialRepostCountDao(context,this) ;
        dbManager =new DbManager(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_home_layout, parent, false);
        HomeListHolder homeListHolder = new HomeListHolder(view);
        return homeListHolder;
    }

    public List<ImageView> getChildView(NineGridImageView nineGridImageView){
        ArrayList<ImageView> imageviews  =new ArrayList<ImageView>() ;
        int count =nineGridImageView.getChildCount() ;
        for (int i = 0; i < count; i++) {
            imageviews.add((ImageView) nineGridImageView.getChildAt(i));
        }
        return  imageviews ;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final HomeListHolder homeListHolder = (HomeListHolder) holder;
        homeListHolder.bind(matrialArrayList.get(position),context);

        homeListHolder.transformbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showProgressWithText(true,"正在处理···");
                 IMAGE_NAME =0 ;
                 final ArrayList<File> files =new ArrayList<File>();
                 Observable.just(homeListHolder.nineGridView)
                         .flatMap(new Func1<NineGridImageView, Observable<ImageView>>() {
                     @Override
                     public Observable<ImageView> call(NineGridImageView nineGridImageView) {
                         return Observable.from(getChildView(nineGridImageView));
                     }
                 })
                    .map(new Func1<ImageView, File>() {
                     @Override
                     public File call(ImageView imageView) {
                         return getFile(imageView);
                     }
                 })
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Subscriber<File>(){
                     @Override
                     public void onCompleted() {
                         Intent intent = new Intent();
                         ComponentName comp = new ComponentName("com.tencent.mm",
                                 "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                         intent.setComponent(comp);

                         if(files==null||files.size()==0){
                             intent.setAction(Intent.ACTION_SEND) ;
                             intent.setType("text/plain") ;
                         }else{
                             intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                             intent.setType("image/*");
                             ArrayList<Uri> imageUris = new ArrayList<Uri>();
                             for (File f : files) {
                                 imageUris.add(Uri.fromFile(f));
                             }
                             intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                         }
                         intent.putExtra("Kdescription", homeListHolder.content.getText().toString());
                         context.startActivity(intent);
                         showProgress(false);
                     }
                     @Override
                     public void onError(Throwable e) {
                         Log.i("Leilei",e.toString()) ;
                     }
                     @Override
                     public void onNext(File file) {
                         files.add(file);
                     }
                 });
                if(matrialArrayList.get(position).getId()!=null)
                addMaterialRepostCountDao.addMaterialRepostCount(getWx(),getToken(),matrialArrayList.get(position).getId(),position);
            }
        });
        final Matrial matrial =matrialArrayList.get(position);
        final boolean iscollected =dbManager.checkIsCollect(matrial.getId());
        try{
            if(iscollected){
               homeListHolder. collectIcon.setImageResource(R.drawable.collect);
                homeListHolder.collectNum.setTextColor(context.getResources().getColor(R.color.collect_color_un));
            }else{
                homeListHolder.  collectIcon.setImageResource(R.drawable.collect_un);
                homeListHolder. collectNum.setTextColor(context.getResources().getColor(R.color.collect_color));
            }
        }catch (Exception e){

        }

        homeListHolder. collectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(iscollected){
                    removeMaterialFavoriteCountDao.cancelFavoriateCount(getWx(),getToken(),matrial.getId(),position);
                    dbManager.deleteMatrial(matrial);
                    homeListHolder.collectIcon.setImageResource(R.drawable.collect_un);
                    homeListHolder.collectNum.setTextColor(context.getResources().getColor(R.color.collect_color));
                    notifyDataSetChanged();
                }else{
                    addMaterialFavoriteCountDao.addFavoriateCount(getWx(), getToken(),matrial.getId(),position);
                    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
                    String createtime =simpleDateFormat.format(new Date());
                    dbManager.addCollectMatrial(matrial,createtime);
                    homeListHolder.collectIcon.setImageResource(R.drawable.collect);
                    homeListHolder.collectNum.setTextColor(context.getResources().getColor(R.color.collect_color_un));
                    notifyDataSetChanged();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return matrialArrayList.size();
    }

    @Override
    public void onRequestSuccess(int requestCode) {

        int postion  ;
        switch (requestCode){
            case RequestCode.CODE_5:
               postion =addMaterialFavoriteCountDao.getPostion() ;
                matrialArrayList.get(postion).setFavorite_count(""+addMaterialFavoriteCountDao.getFavorite_count());
                notifyDataSetChanged();
                break;
            case  RequestCode.REMOVEFAVRIVATE :
                postion =removeMaterialFavoriteCountDao.getPostion() ;
                matrialArrayList.get(postion).setFavorite_count(""+removeMaterialFavoriteCountDao.getFavorite_count());
                notifyDataSetChanged();
                break;
            case  RequestCode.ADDMETRIALTRANCOUNT:
                postion =addMaterialRepostCountDao.getPosition() ;
                matrialArrayList.get(postion).setRepost_count(""+addMaterialRepostCountDao.getRepost_count());
                notifyDataSetChanged();
                break;
        }
    }
    @Override
    public void onRequestError(int requestCode, String errorInfo, int error_code) {
    }

    @Override
    public void onRequestFaild(int requestCode, String errorNo, String errorMessage) {

    }

    @Override
    public void onNoConnect() {

    }

    static class HomeListHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.firstview)
        View firstview;
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.date)
        TextView date;
        @InjectView(R.id.content)
        TextView content;
        @InjectView(R.id.ninegridview)
        NineGridImageView nineGridView;
        @InjectView(R.id.collect_bt)
        LinearLayout collectBt;
        @InjectView(R.id.transformbt)
        LinearLayout transformbt;
        @InjectView(R.id.collect_icon)
        ImageView collectIcon;
        @InjectView(R.id.collect_num)
        TextView collectNum;
        @InjectView(R.id.tranform_icon)
        ImageView tranformIcon;
        @InjectView(R.id.tranform_num)
        TextView tranformNum;

        HomeListHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            nineGridView.setAdapter(mAdapter);
        }

        private NineGridImageViewAdapter<Image> mAdapter = new NineGridImageViewAdapter<Image>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, Image s) {

                String path ="http://"+s.getServer()+s.getPath() ;
                Picasso.with(context)
                        .load(path)
                        .placeholder(context.getResources().getColor(R.color.text_color_light))
                        .resize(300,300)
                        .centerCrop()
                        .into(imageView);
            }
            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }
            @Override
            protected void onItemImageClick(Context context, int index, List<Image> list) {
//                Toast.makeText(context, "image position is " + index, Toast.LENGTH_SHORT).show();
//                Intent intent =new Intent(context, CheckPhotoActivity.class) ;
//                context.startActivity(intent);
            }
        };
        public void bind(final Matrial matrial , final Context context) {
            nineGridView.setImagesData(matrial.getImages());
            if(!TextUtils.isEmpty(matrial.getContent()))
            content.setText(matrial.getContent());
            if(!TextUtils.isEmpty(matrial.getFavorite_count())){
               collectNum.setText( matrial.getFavorite_count());}else{
                collectNum.setText("0");
            }
              tranformNum.setText(matrial.getRepost_count());
         try{
             long da =Long.parseLong(matrial.getAdd_time());
             Date date =new Date(da) ;
             SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd hh:mm");
             String result =dateFormat.format(date);
             this.date.setText(result);
         }catch (Exception e){
            e.printStackTrace();
         }
        }
    }

    public  File getFile(ImageView res){
        boolean success = false;
        File file = null;
        try {
            file = createStableImageFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        BitmapDrawable drawable = (BitmapDrawable) res.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (success) {
            return file;
        } else {
            return null;
        }
    }

    public  File createStableImageFile() throws IOException {
        IMAGE_NAME++;
        String imageFileName = Integer.toString(IMAGE_NAME) + ".jpg";
        File storageDir = context.getExternalCacheDir();
        File image = new File(storageDir, imageFileName);
        return image;
    }


    public String getWx(){
        String wx = SharedPrefsUtil.getValue(context,"wx","") ;
        return   wx ;
    }

    public String getToken(){
        String token = SharedPrefsUtil.getValue(context,"token","") ;
        return   token  ;
    }
}
