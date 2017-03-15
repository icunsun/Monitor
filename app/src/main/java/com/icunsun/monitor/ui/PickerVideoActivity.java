package com.icunsun.monitor.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.icunsun.monitor.R;
import com.icunsun.monitor.adapter.CommonAdapter;
import com.icunsun.monitor.adapter.VideoAdapter;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.model.Video;
import com.icunsun.monitor.util.ProgressDialogUtils;
import com.icunsun.monitor.widget.VideoItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PickerVideoActivity extends BaseActivity implements CommonAdapter.OnItemClickListener, Handler.Callback {

    public static final String TAG = PickerVideoActivity.class.getSimpleName();
    private static final int SHOW_HINT = 10;
    private static final int ADD_VIDEO = 11;
    private static final int CLOSE_DIALOG = 12;
    @BindView(R.id.tv_title)
    TextView mTitleTv;
    @BindView(R.id.tv_hint)
    TextView mHintTv;
    @BindView(R.id.rv_video)
    RecyclerView mVideoRv;


    private final int REQ_CODE_READ_WRITE_PER = 101;
    private final int REQ_CODE_CAMERA_OPEN = 102;
    private final int REQUEST_VIDEO_CAPTURE = 103;
    /**
     * 需要进行检测的权限数组
     */
    private String[] needPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private int mNumberOfColums = 3;
    private VideoAdapter mAdapter;

    private Handler mHandler;


    String mSupportedVideo[] = new String[]{".3gp", ".3GP", ".mp4", ".MP4", ".avi", ".mkv", ".flv", ".wmv"};


    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissionList = findDeniedPermissions(permissions);
        if (null != needRequestPermissionList && needRequestPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissionList.toArray(
                            new String[needRequestPermissionList.size()]),
                    REQ_CODE_READ_WRITE_PER);
        } else {
            getData();
        }
    }


    /**
     * 获取需要申请的权限列表
     *
     * @param permissions
     * @return
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissionList.add(perm);
            }
        }
        return needRequestPermissionList;
    }


    @OnClick({R.id.tv_finish, R.id.btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_finish:
                int count = mAdapter.getItemCount();
                for (int i = 0; i < count; i++) {
                    Video video = mAdapter.get(i);
                    if (video.isSeleted()) {
                        String path = mAdapter.get(i).getSdcardPath();
                        Intent intent = new Intent();
                        intent.putExtra(TypeConstants.VIDEO, path);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;
            case R.id.btn_back:
                this.finish();
                break;
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_picker_video;
    }

    @Override
    public void init(Bundle savedInstanceState) {


        mHandler = new Handler(this);

        mAdapter = new VideoAdapter(this, null);
        mAdapter.add(new Video(TypeConstants.VIDEO));
        GridLayoutManager layoutManager = new GridLayoutManager(this, mNumberOfColums);
        mVideoRv.setLayoutManager(layoutManager);
        mVideoRv.setAdapter(mAdapter);
        mVideoRv.addItemDecoration(new VideoItemDecoration(10));
        mAdapter.setListener(this);
        checkPermissions(needPermissions);

    }

    /**
     * 搜查手机上所有的视频
     */
    private void getData() {
        mAdapter.clear();
        mAdapter.add(new Video(TypeConstants.VIDEO));
        ProgressDialogUtils.showDialog(this, "正在获取中");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] columns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID};
                String orderBy = MediaStore.Video.Media._ID + " desc";
                Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        columns, null, null, orderBy);

                final boolean mediaAvailable = null != cursor && cursor.getCount() > 0;
                if (!mediaAvailable) {
                    mHandler.sendEmptyMessage(SHOW_HINT);
                    mHandler.sendEmptyMessage(CLOSE_DIALOG);
                    return;
                }
                while (cursor.moveToNext()) {
                    final String sdcardPath = cursor.getString(cursor.getColumnIndex
                            (MediaStore.Video.Media.DATA));
                    Log.d(TAG, "run: " + sdcardPath);
                    File file = new File(sdcardPath);
                    if (!file.exists() || file.length() <= 0 || !isValidVIDEO(sdcardPath)) {
                        continue;
                    }
                    Video video = new Video(sdcardPath);
                    Message msg = Message.obtain();
                    msg.what = ADD_VIDEO;
                    msg.obj = video;
                    mHandler.sendMessage(msg);
                }
                mHandler.sendEmptyMessage(CLOSE_DIALOG);

            }
        }).start();

    }


    public boolean isValidVIDEO(String path) {
        boolean isSupported = false;
        for (String formats : mSupportedVideo) {
            if (path.endsWith(formats)) {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }


    private void showMissPermissionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle)
                .setMessage(R.string.notifyMsg)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }


                })
                .setCancelable(false)
                .show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_READ_WRITE_PER: {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showMissPermissionDialog();
                    return;
                }
                getData();
            }
            break;
            case REQ_CODE_CAMERA_OPEN: {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showMissPermissionDialog();
                    return;
                }
                record();
            }
            break;
        }
    }


    private String getPath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_VIDEO_CAPTURE:
                    Intent intent = new Intent();
                    intent.putExtra(TypeConstants.VIDEO, getPath(data.getData()));
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    }

    public void record() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(PickerVideoActivity.this,
                Manifest.permission.CAMERA);
        if (permissionCheck1 == 0) {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            } else {
                Toast.makeText(this, "POST", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                ActivityCompat.requestPermissions(PickerVideoActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        REQ_CODE_CAMERA_OPEN);
            }
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        if (position == 0) {
            record();
            return;
        }
        int count = mAdapter.getItemCount();
        for (int i = 0; i < count; i++) {
            if (position == i) {
                mAdapter.get(i).setSeleted(true);
            } else {
                mAdapter.get(i).setSeleted(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ADD_VIDEO:
                if (msg.obj instanceof Video) {
                    Video video = (Video) msg.obj;
                    mAdapter.add(video);
                }
                return true;
            case SHOW_HINT:
                mHintTv.setVisibility(View.VISIBLE);
                return true;
            case CLOSE_DIALOG:
                ProgressDialogUtils.closeDialog();
                return true;
        }
        return false;
    }
}

