package com.icunsun.monitor.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.icunsun.monitor.R;
import com.icunsun.monitor.adapter.UploadImageAdapter;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.model.MonitorInfo;
import com.icunsun.monitor.model.MonitorUser;
import com.icunsun.monitor.util.FileUtils;
import com.icunsun.monitor.util.ImageUtils;
import com.icunsun.monitor.util.ProgressDialogUtils;
import com.icunsun.monitor.util.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import dagger.ObjectGraph;
import me.iwf.photopicker.PhotoPicker;

public class UploadActivity extends BaseActivity implements AdapterView.OnItemClickListener, AMapLocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = UploadActivity.class.getSimpleName();
    private static final int REQUEST_PICKER_VIDEO = 10;
    @BindView(R.id.et_content)
    EditText mContentEt;
    @BindView(R.id.tv_location)
    TextView mLocationTv;
    @BindView(R.id.gv_pic)
    GridView mGridView;
    @BindView(R.id.image_video)
    ImageView mVideoImage;
    @BindView(R.id.image_add_video)
    ImageView mAddImage;
    @BindView(R.id.layout_video)
    View mView;


    @Inject
    FFmpeg ffmpeg;

    private String mAddress;


    /**
     * 判断当前是否已经定位
     */
    private boolean isLocation;

    private ArrayList<String> mData;
    private ArrayList<File> mCompressPics;
    private UploadImageAdapter mAdapter;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mOption;

    /**
     * 需要进行检测的权限数组
     */
    private String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int PERMISSION_REQUESTCODE = 100;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    private String mVideoPath;
    private String mContent;
    //被压缩视频的路径
    private File mTargetFile;
    private String mCode = String.valueOf(TypeConstants.ALL);


    @Override
    public int getContentViewResId() {
        return R.layout.activity_upload;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        ObjectGraph.create(new DaggerDependencyModule(this)).inject(this);
        loadFFMpegBinary();


        mData = new ArrayList<>();
        mCompressPics = new ArrayList<>();
        mData.add(TypeConstants.ADD);
        mAdapter = new UploadImageAdapter(this, mData);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLocation) {
            if (isNeedCheck) {
                checkPermissions(needPermissions);
            }
        }
    }

    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissionList = findDeniedPermissions(permissions);
        if (null != needRequestPermissionList
                && needRequestPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissionList.toArray(
                            new String[needRequestPermissionList.size()]),
                    PERMISSION_REQUESTCODE);
        } else {
            startLocation();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUESTCODE:
                if (verifyPermissions(grantResults)) {
                    startLocation();
                } else {
                    showMissPermissionDialog();
                    isNeedCheck = false;
                }
                break;
        }
    }

    private void showMissPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @OnClick({R.id.btn_send, R.id.image_add_video, R.id.view_video, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                uploadMonitorInfo();
                break;
            case R.id.image_add_video:
            case R.id.view_video:
                pickerVideo();
                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }


    /**
     * 开始定位
     */
    private void startLocation() {
        ProgressDialogUtils.showDialog(this, "正在获取当前位置，请稍候...");
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mOption.setLocationCacheEnable(true);
        mOption.setOnceLocationLatest(true);
        mOption.setNeedAddress(true);
        mOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mOption);
        mLocationClient.setLocationListener(this);


        mLocationClient.startLocation();
        isLocation = true;

    }

    private void uploadMonitorInfo() {
        mContent = mContentEt.getText().toString().trim();
        if (TextUtils.isEmpty(mContent)) {
            ToastUtils.toast(this, "请输入文字描述!");
            return;
        }
        if (mAdapter.getCount() < 3) {
            ToastUtils.toast(this, "请上传至少3张图片!");
            return;
        }
        if (mView.getVisibility() == View.GONE) {
            ToastUtils.toast(this, "请上传一个简短的视频!");
            return;
        }

        // 先压缩要上传的视频
        compressVideo();
    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }


    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UploadActivity.this.finish();
                    }
                })
                .create()
                .show();

    }

    /**
     * 上传
     *
     * @param content
     */
    private void upload(final String content) {
        final ProgressDialog dialog = new ProgressDialog(this, android.R.style.Theme_DeviceDefault_Dialog);
        dialog.setCancelable(false);

        dialog.setTitle("上传中，请稍候...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        mData.remove(TypeConstants.ADD);

        // 先过滤压缩图片
        filterPic(mData);


        mData.add(mTargetFile.getAbsolutePath());
        String[] filePath = mData.toArray(new String[mData.size()]);
        dialog.show();
        BmobFile.uploadBatch(filePath, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                int size = list1.size();
                if (size == mData.size()) {
                    // 删除压缩时缓存的图片
                    for (File f : mCompressPics) {
                        if (f.exists()) {
                            f.delete();
                        }
                    }
                    // 删除压缩的视频
                    if (mTargetFile.exists()) {
                        mTargetFile.delete();
                    }
                    Date date = new Date();
                    long time = date.getTime();
                    String dateTime = DateUtils.formatDateTime(UploadActivity.this, time, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                    MonitorUser user = MonitorUser.getCurrentUser(MonitorUser.class);
                    MonitorInfo info = new MonitorInfo();
                    info.setTime(dateTime);
                    info.setPictures(list1.subList(0, size - 1));
                    info.setAddress(mAddress);
                    info.setVideo(list1.get(size - 1));
                    info.setContent(content);
                    info.setState(TypeConstants.STATE_REVIEW);
                    String code = mCode.substring(0, 2);
                    info.setProvinceCode(Integer.parseInt(code));
                    info.setName(user.getName());
                    info.setUserId(user.getObjectId());
                    dialog.setTitle("正在保存,请稍候...");
                    info.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            ToastUtils.toast(UploadActivity.this, "上传成功!");
                            dialog.dismiss();
                            finish();
                        }
                    });

                }
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                dialog.setTitle("正在上传第" + curIndex + "个文件,共" + total + "个。");
                dialog.setProgress(curPercent);

            }

            @Override
            public void onError(int i, String s) {
                ToastUtils.toast(UploadActivity.this, "上传错误");
                dialog.dismiss();
            }
        });

    }

    /**
     * 压缩视频
     */
    private void compressVideo() {
        mTargetFile = new File(FileUtils.getCacheDir(this), "VIDEO_" + System.currentTimeMillis() + ".mp4");
        String cmd = "-threads 4 -y -i " + mVideoPath + " -strict experimental -vcodec libx264 -preset veryfast -crf 28 -r 10 " + mTargetFile.getAbsolutePath();
        String[] command = cmd.split(" ");
        execFFmpegBinary(command);
    }


    private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    ToastUtils.toast(UploadActivity.this, "压缩失败!");
                }

                @Override
                public void onSuccess(String s) {
                    // 压缩视频成功开始上传
                    upload(mContent);
                }

                @Override
                public void onProgress(String s) {

                }

                @Override
                public void onStart() {
                    ProgressDialogUtils.showDialog(UploadActivity.this, "正在压缩视频，可能需要较长时间!");
                }

                @Override
                public void onFinish() {
                    ProgressDialogUtils.closeDialog();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
            ToastUtils.toast(this, "压缩失败," + e.getMessage());
        }
    }

    /**
     * 将大小超过1M的图片进行压缩
     *
     * @param data
     */
    private void filterPic(ArrayList<String> data) {
        for (String path : data) {
            File file = new File(path);
            if (FileUtils.getFileSize(file).endsWith("B") || FileUtils.getFileSize(file).endsWith("KB")) {
                continue;
            } else {
                String compressPath = ImageUtils.compressPic(file, this);
                data.set(data.indexOf(path), compressPath);
                // 将被压缩的图片路径进行保存
                File f = new File(compressPath);
                mCompressPics.add(f);
            }
        }
    }

    /**
     * 选择视频
     */
    private void pickerVideo() {
        Intent intent = new Intent(this, PickerVideoActivity.class);
        startActivityForResult(intent, REQUEST_PICKER_VIDEO);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * gridview的item点击监听回调
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        pickerPictures();
    }

    /**
     * 选择图片
     */
    private void pickerPictures() {
        mData.remove(TypeConstants.ADD);
        PhotoPicker.builder()
                .setPhotoCount(3)
                .setSelected(mData)
                .setPreviewEnabled(true)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoPicker.REQUEST_CODE:
                    if (data != null) {
                        ArrayList<String> selectPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        if (selectPhotos.size() < 3) {
                            selectPhotos.add(TypeConstants.ADD);
                        }
                        mData.clear();
                        mData.addAll(selectPhotos);
                        mAdapter.notifyDataSetChanged();
                    }

                    break;
                case REQUEST_PICKER_VIDEO:
                    mVideoPath = data.getStringExtra(TypeConstants.VIDEO);
                    Glide.with(this)
                            .load(mVideoPath)
                            .into(mVideoImage);
                    mView.setVisibility(View.VISIBLE);
                    mAddImage.setVisibility(View.INVISIBLE);

                    break;
            }
        } else {
            switch (requestCode) {
                case PhotoPicker.REQUEST_CODE:
                    if (mData.size() < 3) {
                        mData.add(TypeConstants.ADD);
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 定位监听
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == 0) {
            mAddress = aMapLocation.getAddress();
            mCode = aMapLocation.getAdCode();
        } else {
            mAddress = "中国";
        }
        ProgressDialogUtils.closeDialog();
        mLocationTv.setText(mAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }
}
