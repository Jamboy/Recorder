package com.speakin.recorder.fileview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.speakin.recorder.R;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class FileViewActivity extends AppCompatActivity {

    private TextView mEditTVItemNo;
    private RecyclerView mRecyclerView;
    private EditText mETVItemInput;
    private String filePath;
    private String mRecivedMsg;
    private List<String> datas = new ArrayList<>();
    private List<String> paths = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_view_activity);
        Intent intent = getIntent();
        mRecivedMsg = intent.getStringExtra("filepath");
Log.d("file",mRecivedMsg);
        initDatas();
        initView();

        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                return new RecyclerView.ViewHolder(LayoutInflater.from(FileViewActivity.this).inflate(R.layout.item, parent, false)) {
                };

            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
                holder.itemView.findViewById(R.id.item_root).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        filePath = getFilePath(position);
//                        if (!EasyPermissions.hasPermissions(MainActivity.this, perms)) {
//                            EasyPermissions.requestPermissions(MainActivity.this, "需要访问手机存储权限！", 10086, perms);
//                        }else
                        if (position == 0){
                            String index = mETVItemInput.getText().toString().trim();
                            String tempPath =getFilePath(position)+"test"+"-"+index+".xlsx";
                            Log.d("jambo",tempPath);
//                            FileDisplayActivity.show(FileViewActivity.this, tempPath);

                        }else {
//                            FileDisplayActivity.show(FileViewActivity.this, filePath);
                        }
                    }
                });
                ((TextView) holder.itemView.findViewById(R.id.item_tv)).setText(getDatas().get(position));
            }

            @Override
            public int getItemCount() {
                return getDatas().size();
            }
        });

    }


    private void initView(){
        mETVItemInput = (EditText) findViewById(R.id.mETVItemInput);
        mETVItemInput.setText(mRecivedMsg);
    }

    private String getItemNO(){
        if (!TextUtils.isEmpty(mEditTVItemNo.getText())){
            return mEditTVItemNo.getText().toString().trim();
        }else{
            return "010";
        }
    }

    private void initDatas() {

        datas.add("根据工序打开对应文档");

        datas.add("打开本地doc文件");

        datas.add("打开本地txt文件");

        datas.add("打开本地excel文件");


        datas.add("打开本地ppt文件");


        datas.add("打开本地pdf文件");

        datas.add("测试调用浏览器从PC下载");


    }

    private List<String> getDatas() {

        if (datas != null && datas.size() > 0) {
            return datas;
        } else {
            datas = new ArrayList<>();
            initDatas();
            return datas;
        }

    }

    private String getFilePath(int position) {
        String path = null;
        switch (position) {
            case 0:
                path = "/storage/emulated/0/Download/com.jambo.download/";
                break;
            case 1:
                path = "/storage/emulated/0/Download/com.jambo.download/test-010.docx";

                break;


            case 2:
                path = "/storage/emulated/0/Download/com.jambo.download/test.txt";
                break;

            case 3:
                path = "/storage/emulated/0/Download/com.jambo.download/test.xlsx";
                break;
            case 4:
                path = "/storage/emulated/0/Download/com.jambo.download/test.pptx";
                break;

            case 5:
                path = "/storage/emulated/0/Download/com.jambo.download/test.pdf";
                break;

            case 6:
                path = "http://192.168.3.10:9090/";
                break;
        }
        return path;
    }
}
