package com.protv.mm.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.protv.mm.R;
import com.protv.mm.adapters.VideoAdapter;
import com.protv.mm.models.VideoModel;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executor;


public class FragmentTwo extends Fragment {
    View v;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<VideoModel> videoLists =new ArrayList<>();
    VideoAdapter adapter;
    Executor postExecutor;
    private int storageRequestCode=123;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_two, container, false);
        setUpView();
        postExecutor = ContextCompat.getMainExecutor(getActivity());
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},storageRequestCode);

        return v;
    }

    private void setUpView(){
        recyclerView=v.findViewById(R.id.recyclerViewVideo);
        swipeRefreshLayout=v.findViewById(R.id.swipe);
        adapter=new VideoAdapter(videoLists,getActivity());
        GridLayoutManager gm=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        if(isPermissionGranted()) {
            swipeRefreshLayout.setRefreshing(true);
            //new VideoLoader(getActivity()).start();
            new VideoLoader().start();
        }else {
            takePermission();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isPermissionGranted()){
                    videoLists.clear();
                    new VideoLoader().start();
                }
                else takePermission();
            }
        });
    }


    private boolean isPermissionGranted(){
        int  writeExternalStorage= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return  writeExternalStorage== PackageManager.PERMISSION_GRANTED;
    }

    private void takePermission(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            if(requestCode==101){
                boolean readExternalStorage=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(readExternalStorage){
                    new VideoLoader().start();
                }else {
                    takePermission();
                }
            }
        }
    }


    public class VideoLoader extends Thread{
        @Override
        public void run() {
            super.run();

            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES+"/ProTV");
            File[] videoFiles =directory.listFiles();
            Log.e("VideoLoader ","Start Video Loading");
            if(videoFiles!=null){
                for (File videoFile : videoFiles) {

                    Uri uri=Uri.fromFile(videoFile);

                    if (Build.VERSION.SDK_INT >= 24) {
                        uri = FileProvider.getUriForFile(getActivity(), getActivity(). getPackageName() + ".provider", videoFile);
                    }
                    videoLists.add(new VideoModel(uri,videoFile.getName(),0,400,null));
                }

            }
            postExecutor.execute(new Runnable() {
                @Override
                public void run() {

                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }


    /*

    This class can only be used for android version and higher

    public  class VideoLoader extends Thread {

        Context context;
        public VideoLoader(Context context) {

            this.context=context;
        }

        @Override
        public void run() {

            Uri collection;
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                collection= MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            }else{
                collection=MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }

            String [] projection=new String[]{
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE
            };

            String selection=MediaStore.Video.Media.BUCKET_DISPLAY_NAME+" = ? ";
            String [] selectionArgs=new String[]{
                    "ProTV"
            };

            String sortOrder= MediaStore.Video.Media.DATE_ADDED+" DESC";

            postExecutor.execute(new Runnable() {
                @Override
                public void run() {

                    try (Cursor cursor=context.getContentResolver().query(
                            collection,
                            projection,
                            selection,
                            selectionArgs,
                            sortOrder
                    )){
                        int idColumn= cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                        int nameColumn=cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                        int durationColumn=cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                        int sizeColumn=cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

                        videoLists.clear();
                        while(cursor.moveToNext()){
                            long id=cursor.getLong(idColumn);
                            String name=cursor.getString(nameColumn);
                            int duration=cursor.getInt(durationColumn);
                            int size=cursor.getInt(sizeColumn);

                            Uri contentUri= ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,id);
                            Bitmap thumbnail = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                try {
                                    thumbnail = context.getContentResolver().loadThumbnail(contentUri,new Size(640,480),null);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            videoLists.add(new VideoModel(contentUri,name,duration,size,thumbnail));
                        }

                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }
    }

    *
     */
}
