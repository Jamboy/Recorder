package com.speakin.recorder.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.speakin.recorder.R;
import com.speakin.recorder.RecorderApp;
import com.speakin.recorder.fileview.FileViewActivity;
import com.speakin.recorder.module.control.MasterControlManager;
import com.speakin.recorder.module.control.SlaveControlManager;
import com.speakin.recorder.utils.IpUtil;

import org.json.JSONObject;

import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity {


    private TextView textView;
    private TextView mTVDisplayInfo;
    private EditText mETInputItemNo;
    private String mReceviedMessage = null;

    private MasterControlManager masterControlManager;
    private SlaveControlManager slaveControlManager;
    private boolean isMaster = false;  //是否为主机
    private RecorderApp mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initPermission();
        initView();
        refreshIP();
    }

    private void initPermission() {

        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(MainActivity.this, perms)) {
            EasyPermissions.requestPermissions(MainActivity.this, "需要访问手机存储权限！", 10086, perms);
        }
    }

    private void initData() {
        mApplication = (RecorderApp) this.getApplicationContext();
        masterControlManager = new MasterControlManager(); //主机控制
        slaveControlManager = new SlaveControlManager();	//丛机控制

        slaveControlManager.setControlManagerCallback(new SlaveControlManager.SlaveControlManagerCallback() {
            @Override
            public void onFoundMaster(String masterIp, JSONObject masterInfo) {
                mTVDisplayInfo.setText(masterIp + " " + masterInfo.toString());
            }

            @Override
            public void onConnectedMaster(String masterIp, Exception ex) {
                mTVDisplayInfo.setText(masterIp + " connected");
            }

            @Override
            public void onDisconnectMaster(String masterIp, Exception ex) {
                mTVDisplayInfo.setText(masterIp + " disconnected");
            }

            @Override
            public void onReceiveMessage(String message) {
                Toast.makeText(MainActivity.this,"receive mes:"+message,Toast.LENGTH_SHORT).show();
                mReceviedMessage = message;
                mTVDisplayInfo.setText("message: " + message);

            }
        });

        masterControlManager.setControlManagerCallback(new MasterControlManager.MasterControlManagerCallback() {
            @Override
            public void onServerError(Exception ex) {
                mTVDisplayInfo.setText("server error: " + ex.getLocalizedMessage());
            }

            @Override
            public void onClientConnected(String clientSocket) {
                mTVDisplayInfo.setText("client: " + clientSocket);
            }

            @Override
            public void onClientDisconnect(String clientSocket) {
                mTVDisplayInfo.setText("disconnected: " + clientSocket);
            }

            @Override
            public void onMessageReceive(String clientSocket, String message) {
                Toast.makeText(MainActivity.this,"receive mes:"+message,Toast.LENGTH_SHORT).show();
                mTVDisplayInfo.setText("message: " + message);
            }

            @Override
            public void onReceiveFile(String clientSocket, String filePath) {
                Toast.makeText(MainActivity.this, "receive file" + filePath, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
		/*
		主机模式，点击启动，设置is true
		*/
        final Button mMasterBtn = findViewById(R.id.masterBtn);
        mMasterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                masterControlManager.start(mApplication.getTeamID());
                isMaster = true;
                if (isMaster){
                    mMasterBtn.setText("主机模式：已开启");
                }else {
                    mMasterBtn.setText("主机模式：已关闭");
                }
Log.d("main","ismaster = true");
            }
        });

//        打开fileView
        Button mOpenFVBtn = findViewById(R.id.mOpenFVBtn);
        mOpenFVBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAnotherActivity(MainActivity.this,FileViewActivity.class,"filepath",mReceviedMessage);
            }
        });


		/*
		从机模式
		*/
        Button mSlaveBtn = findViewById(R.id.slaveBtn);
        mSlaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slaveControlManager.start(mApplication.getTeamID());
                isMaster = false;
                if (!isMaster){
                    mMasterBtn.setText("从机模式：已开启");
                }else {
                    mMasterBtn.setText("从机模式：已关闭");
                }
            }
        });

		/*
		更新IP
		*/
        
		textView = (TextView) findViewById(R.id.text1);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshIP();
            }
        });

		/*
		发送消息
		判断isMaster 区分发送
		*/

        mTVDisplayInfo = (TextView) findViewById(R.id.text2);
        mETInputItemNo = (EditText) findViewById(R.id.mETInputItemNo);

        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMaster) {
                    masterControlManager.send("Hello, I am client ：" + getETInputItemNo() + System.currentTimeMillis());
                } else {
                    slaveControlManager.send("Hello, I am slave ："  + getETInputItemNo() + System.currentTimeMillis());
                }
            }
        });


		/*
		停止 判断isMaster
		*/		
        findViewById(R.id.stopBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMaster) {
                    masterControlManager.stop();
                } else {
                    slaveControlManager.stop();
                }
            }
        });

		/*
		发送文件
		*/
        Button mSendFileBtn = findViewById(R.id.sendeFile);
        mSendFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"click send file", Toast.LENGTH_SHORT).show();
                String filepath = "/storage/emulated/0/Download/1996-music.wmv";
                slaveControlManager.sendFile(filepath);
                Log.d("main", "after"+filepath);
            }
        });

        Spinner mSpinTeamId = (Spinner) findViewById(R.id.mSpiTeamId);
//        获取下拉TeamID
        mSpinTeamId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String [] teams = getResources().getStringArray(R.array.teamsId);
                    mApplication.setTeamID(teams[position]);
                    Toast.makeText(MainActivity.this,"click" + mApplication.getTeamID(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        获取下拉ItemNo
        Spinner mSpinItemNO = (Spinner) findViewById(R.id.mSpiItemNo);
        mSpinItemNO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String items[] = getResources().getStringArray(R.array.itemNo);
                mApplication.setItemNo(items[position]);
Log.d("main",mApplication.getItemNo() + "@" + mApplication.getTeamID());
                Toast.makeText(MainActivity.this,"click"+mApplication.getItemNo(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



	/*
	 *	获取并设置IP
	*/

    private void refreshIP() {
        String ip1 = IpUtil.getHostIP();//获取IP static

        textView.setText("本机IP: " + ip1 );
    }

    /*

    */
    private String getETInputItemNo(){
        if (TextUtils.isEmpty(mETInputItemNo.getText().toString().trim())){
Log.d("main","is empty");
            return "";
        }else {
            return mETInputItemNo.getText().toString().trim();
        }

    }

    public void openAnotherActivity(Context context1, Class c,String tag,String message){
        Intent intent = new Intent(context1, c);
        intent.putExtra(tag,"test send msg");
        startActivity(intent);
    }

	/*
	退出则停止	
	*/
    
	@Override
    protected void onDestroy() {
        super.onDestroy();
        slaveControlManager.stop();
        masterControlManager.stop();
    }
}
