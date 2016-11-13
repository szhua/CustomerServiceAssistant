package com.pcjh.assistant.adapter;

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

import com.pcjh.assistant.R;
import com.pcjh.assistant.activity.CheckPhotoActivity;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.HomeEntity;
import com.pcjh.assistant.entity.Image;
import com.pcjh.assistant.entity.Matrial;
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
public class HomeListAdapter extends RecyclerView.Adapter {


    private Context context;
    private LayoutInflater inflater;
    private int  IMAGE_NAME;
    private ArrayList<Matrial> matrialArrayList =new ArrayList<>() ;


    public void setMatrialArrayList(ArrayList<Matrial> matrialArrayList) {
        this.matrialArrayList = matrialArrayList;
        notifyDataSetChanged();
    }

    public HomeListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final HomeListHolder homeListHolder = (HomeListHolder) holder;
        homeListHolder.bind(matrialArrayList.get(position),context);
        homeListHolder.collectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeListHolder.collectIcon.setImageResource(R.drawable.collect);
                homeListHolder.collectNum.setTextColor(context.getResources().getColor(R.color.collect_color_un));
            }
        });
        homeListHolder.transformbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                   .subscribe(new Subscriber<File>() {
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
                ((HomeListHolder) holder).tranformIcon.setVisibility(View.GONE);
                ((HomeListHolder) holder).tranformNum.setText(" 已转发 ");
                homeListHolder.tranformNum.setTextColor(context.getResources().getColor(R.color.transform_color));
            }
        });

    }

    @Override
    public int getItemCount() {
        return matrialArrayList.size();
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
                        .placeholder(R.mipmap.ic_launcher)
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
                Toast.makeText(context, "image position is " + index, Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(context, CheckPhotoActivity.class) ;
                context.startActivity(intent);
            }
        };
        public void bind(final Matrial matrial , final Context context) {
            nineGridView.setImagesData(matrial.getImages());
            if(!TextUtils.isEmpty(matrial.getContent()))
            content.setText(matrial.getContent());
            final DbManager dbManager =new DbManager(context);
            final boolean iscollected =dbManager.checkIsCollect(matrial.getId());
          try{
              if(iscollected){
                  collectIcon.setImageResource(R.drawable.collect);
                  collectNum.setTextColor(context.getResources().getColor(R.color.collect_color_un));
              }else{
                  collectIcon.setImageResource(R.drawable.collect_un);
                  collectNum.setTextColor(context.getResources().getColor(R.color.collect_color));
              }
          }catch (Exception e){

          }

            collectBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if(iscollected){
                      dbManager.deleteMatrial(matrial);
                      collectIcon.setImageResource(R.drawable.collect_un);
                      collectNum.setTextColor(context.getResources().getColor(R.color.collect_color));
                  }else{
                      SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
                      String createtime =simpleDateFormat.format(new Date());
                      dbManager.addCollectMatrial(matrial,createtime);
                      collectIcon.setImageResource(R.drawable.collect);
                      collectNum.setTextColor(context.getResources().getColor(R.color.collect_color_un));
                  }

                }
            });










         try{
             long da =Long.parseLong(matrial.getAdd_time());
             Date date =new Date(da) ;
             SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd hh:mm");
             String result =dateFormat.format(date);
             this.date.setText(result);
         }catch (Exception e){

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
}
