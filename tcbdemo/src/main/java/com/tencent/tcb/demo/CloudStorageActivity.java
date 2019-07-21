package com.tencent.tcb.demo;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.tencent.tcb.storage.StorageService;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONObject;

public class CloudStorageActivity extends AppCompatActivity {
    private final String LogTag = "CloudStorage";
    private TextView resultText;
    private ImageView resultImage;
    private Handler uiHandler;
    private StorageService storage;
    private String fileId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_storage);

        resultText = (TextView) findViewById(R.id.upload_file_result);
        resultImage = (ImageView) findViewById(R.id.download_Image);
        uiHandler = new Handler();
        storage = new StorageService(Constants.config(), this);

        Button uploadButton = (Button) findViewById(R.id.upload_button);
        Button downloadButton = (Button) findViewById(R.id.download_button);
        Button getUrlButton = (Button) findViewById(R.id.get_download_url_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile();
            }
        });
        getUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDownloadUrl();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            // 使用云存储上传图片
            uploadFile(imagePath);
        }
    }

    /**
     * 云存储上传文件
     */
    private void uploadFile(final String localPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                storage.uploadFile(
                        "test.svg",
                        localPath,
                        new StorageService.FileTransportListener() {
                            @Override
                            public void onSuccess(final JSONObject result) {
                                // 打印结果
                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        resultText.setText("上传成功：" + result.toString());
                                    }
                                });
                                fileId = result.optString("fileId");
                            }

                            @Override
                            public void onProgress(int progress) {
                                Log.d(LogTag, String.valueOf(progress));
                            }

                            @Override
                            public void onFailed(TcbException e) {
                                Log.e(LogTag, e.toString());
                            }
                        });

            }
        }).start();
    }

    /**
     * 云存储下载文件
     */
    private void downloadFile() {
        if (fileId == null || fileId.isEmpty()) {
            resultText.setText("请先上传图片!");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 设置下载图片的地址
                final String downloadFIlePath = "/data/data/com.tencent.tcb.demo/files/test.svg";

                storage.downloadFile(
                        fileId,
                        downloadFIlePath,
                        new StorageService.FileTransportListener() {
                            @Override
                            public void onSuccess(final JSONObject result) {
                                // 显示图片
                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bitmap bitmap = BitmapFactory.decodeFile(downloadFIlePath);
                                        resultImage.setImageBitmap(bitmap);
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int progress) {
                                Log.d(LogTag, String.valueOf(progress));
                            }

                            @Override
                            public void onFailed(TcbException e) {
                                Log.e(LogTag, e.toString());
                            }
                        });
            }
        }).start();
    }

    /**
     * 云存储获取下载地址
     */
    private void getDownloadUrl() {
        if (fileId == null || fileId.isEmpty()) {
            resultText.setText("请先上传图片!");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 设置下载图片的地址
                final String downloadFIlePath = "/data/data/com.tencent.tcb.demo/files/test.svg";

                // 该接口也支持批量获取
                storage.downloadFile(
                        fileId,
                        new StorageService.FileTransportListener() {
                            @Override
                            public void onSuccess(final JSONObject result) {
                                // 显示图片
                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        resultText.setText("获取下载地址成功：" + result.toString());
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int progress) {
                                Log.d(LogTag, String.valueOf(progress));
                            }

                            @Override
                            public void onFailed(TcbException e) {
                                Log.e(LogTag, e.toString());
                            }
                        });
            }
        }).start();
    }

    /**
     * 选择上传图片
     */
    private void chooseImage() {
        verifyStoragePermissions();

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e) {
            Log.e(e.toString(), e.toString());
        }
    }

    /**
     * 获取图片访问权限
     */
    private void verifyStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(CloudStorageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            final int REQUEST_EXTERNAL_STORAGE = 1;
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            ActivityCompat.requestPermissions(
                    CloudStorageActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
