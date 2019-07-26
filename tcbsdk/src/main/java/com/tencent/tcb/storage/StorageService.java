package com.tencent.tcb.storage;

import android.content.Context;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.TcbException;
import com.tencent.tcb.utils.TcbListener;
import com.tencent.tcb.utils.TcbStorageListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class StorageService {

    private Request request;

    private final String getTempURLAction = "storage.batchGetDownloadUrl";

    public StorageService(String envName, Context context) {
        this.request = new Request(new Config(envName), context);
    }

    private void cosUploadFile(
            String cloudPath,
            final File file,
            final TcbStorageListener listener
    ) throws TcbException, JSONException {
        // 获取临时签名，上传 URL
        JSONObject metaData = getUploadMetadata(cloudPath);
        JSONObject data = metaData.getJSONObject("data");
        String url = data.getString("url");
        String token = data.getString("token");
        String cosFileId = data.getString("cosFileId");
        String sign = data.getString("authorization");
        String requestId = metaData.getString("requestId");
        String fileId = data.getString("fileId");

        final OkHttpClient client = new OkHttpClient();

        RequestBody fileBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/octet-stream");
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                source = Okio.source(file);
                // 文件总大小
                long total = file.length();
                // 已传输字节块
                long sum = 0;
                long read = 0;
                Buffer buf = new Buffer();

                while ((read = source.read(buf, 2048)) != -1) {
                    sum += read;
                    sink.write(buf, read);
                    int progress = (int) (sum * 1.0f / total * 100);
                    listener.onProgress(progress);
                }
            }
        };

        RequestBody requestBody = new MultipartBody
                .Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key", cloudPath)
                .addFormDataPart("signature", sign)
                .addFormDataPart("x-cos-meta-fileid", cosFileId)
                .addFormDataPart("x-cos-security-token", token)
                .addFormDataPart("file", cloudPath, fileBody)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder().url(url).post(requestBody).build();
        Response response;

        try {
            response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                JSONObject result = new JSONObject();
                result.put("requestId", requestId);
                result.put("fileId", fileId);
                listener.onSuccess(result);
            } else {
                throw new TcbException("RES_ERR", "upload error code " + response);
            }

        } catch (IOException e) {
            listener.onFailed(new TcbException(Code.IO_ERR, e.toString()));
        }
    }

    public void uploadFile(String cloudPath, File file, TcbStorageListener listener) {
        try {
            cosUploadFile(cloudPath, file, listener);
        } catch (JSONException e) {
            listener.onFailed(new TcbException(Code.JSON_ERR, e.toString()));
        } catch (TcbException e) {
            listener.onFailed(e);
        }
    }

    public void uploadFile(String cloudPath, String filePath, TcbStorageListener listener) {
        try {
            File file = new File(filePath);
            cosUploadFile(cloudPath, file, listener);
        } catch (JSONException e) {
            listener.onFailed(new TcbException(Code.JSON_ERR, e.toString()));
        } catch (TcbException e) {
            listener.onFailed(e);
        }
    }

    public void uploadFileAsync(final String cloudPath, final File file, final TcbStorageListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadFile(cloudPath, file, listener);
            }
        }).start();
    }

    public void uploadFileAsync(final String cloudPath, final String filePath, final TcbStorageListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadFile(cloudPath, filePath, listener);
            }
        }).start();
    }

    public void downloadFile(String fileId, String tempFilePath, TcbStorageListener listener) {
        String tempDownUrl = "";
        try {
            if (fileId == null || fileId.isEmpty()) {
                throw new TcbException(Code.EMPTY_PARAM, "fileId cannot be empty");
            }
            String[] fileList = {fileId};
            JSONObject tempUrlRes = getTempFileURL(fileList);
            JSONObject file = tempUrlRes.getJSONArray("fileList").getJSONObject(0);
            tempDownUrl = file.optString("download_url");
        } catch (JSONException e) {
            listener.onFailed(
                    new TcbException("GET_URL_ERROR",
                            "get file download url error. detail: " + e.toString())
            );
            return;
        } catch (TcbException e) {
            listener.onFailed(e);
            return;
        }

        byte[] buf = new byte[2048];
        int len = 0;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15000,
                    TimeUnit.MILLISECONDS).build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .addHeader("Accept", "*/*")
                    .addHeader("Connection", "keep-alive")
                    .url(tempDownUrl)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (response.body() == null) {
                throw new TcbException("RES_BODY_NULL", "response body is null");
            }

            inputStream = response.body().byteStream();

            File file = new File(tempFilePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fileOutputStream = new FileOutputStream(file);

            long total = response.body().contentLength();
            long sum = 0;

            while ((len = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / total * 100);
                listener.onProgress(progress);
            }

            fileOutputStream.flush();
            // 下载完成
            listener.onSuccess(null);
        } catch (TcbException e) {
            listener.onFailed(e);
        } catch (IOException e) {
            listener.onFailed(new TcbException("IO_ERR", e.toString()));
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                listener.onFailed(new TcbException(Code.IO_ERR,
                        "stream close false " + e.toString()));
            }
        }
    }

    public void downloadFileAsync(final String fileId, final String tempFilePath, final TcbStorageListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadFile(fileId, tempFilePath, listener);
            }
        }).start();
    }

    public JSONObject deleteFile(String[] fileList) throws TcbException {
        String deleteFileAction = "storage.batchDeleteFile";

        if (fileList.length < 1) {
            throw new TcbException("PARAM_INVALID", "fileList must not be empty");
        }

        for (String s : fileList) {
            if (s == null || s.isEmpty()) {
                throw new TcbException("PARAM_INVALID", "fileList must not be empty");
            }
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("fileid_list", fileList);
        JSONObject res = request.send(deleteFileAction, params);

        try {
            if (res.has("code")) {
                throw new TcbException(res.getString("code"), res.getString("message"));
            } else {
                JSONObject result = new JSONObject();
                JSONObject data = res.getJSONObject("data");
                result.put("requestId", res.getString("requestId"));
                result.put("fileList", data.getJSONArray("delete_list"));
                return result;
            }
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.toString() + res.toString());
        }
    }

    public void deleteFileAsync(final String[] fileList, final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = deleteFile(fileList);
                    listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }

    public JSONObject getTempFileURL(String[] fileList) throws TcbException {
        if (fileList.length < 1) {
            throw new TcbException("PARAM_INVALID", "fileList must not be empty");
        }

        // 将 ["fileId"] 转化成 [{ "fileid": "fileId", max_age: 86400 }]
        ArrayList<HashMap<String, Object>> files = new ArrayList<HashMap<String, Object>>();

        for (String s : fileList) {
            HashMap<String, Object> fileMeta = new HashMap<>();
            fileMeta.put("fileid", s);
            fileMeta.put("max_age", 1800);
            files.add(fileMeta);
        }

        // 构造请求参数
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("file_list", files);

        JSONObject res = request.send(getTempURLAction, params);

        if (res == null) {
            throw new TcbException("RES_NULL", "get a null response");
        }

        try {
            // 存在 code，说明返回值存在异常
            if (res.has("code")) {
                throw new TcbException(res.getString("code"), res.getString("message"));
            } else {
                JSONObject result = new JSONObject();
                JSONObject data = res.getJSONObject("data");
                result.put("requestId", res.getString("requestId"));
                result.put("fileList", data.getJSONArray("download_list"));
                return result;
            }
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.toString());
        }
    }

    public JSONObject getTempFileURL(ArrayList<FileMeta> fileList) throws TcbException {

        if (fileList.isEmpty()) {
            throw new TcbException("PARAM_INVALID", "fileList must not be empty");
        }

        ArrayList<HashMap<String, Object>> files = new ArrayList<HashMap<String, Object>>();

        for (FileMeta item : fileList) {
            HashMap<String, Object> fileMeta = new HashMap<>();
            fileMeta.put("fileid", item.fileID);
            fileMeta.put("max_age", item.maxAge);
            files.add(fileMeta);
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("file_list", files);

        JSONObject res = request.send(getTempURLAction, params);

        try {
            // 存在 code，说明返回值存在异常
            if (res.has("code")) {
                throw new TcbException(res.getString("code"), res.getString("message"));
            } else {
                JSONObject result = new JSONObject();
                JSONObject data = res.getJSONObject("data");
                result.put("requestId", res.getString("requestId"));
                result.put("fileList", data.getJSONArray("download_list"));
                return result;
            }
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.toString());
        }
    }

    public void getTempFileURLAsync(final String[] fileList, final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = getTempFileURL(fileList);
                    listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }

    public void getTempFileURLAsync(final ArrayList<FileMeta> fileList, final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = getTempFileURL(fileList);
                    listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }

    public JSONObject getUploadMetadata(String cloudPath) throws TcbException {
        final String action = "storage.getUploadMetadata";
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("path", cloudPath);
        JSONObject res = request.send(action, requestParams);

        // 存在 code，说明返回值存在异常
        if (res.has("code")) {
            throw new TcbException(res.optString("code"), res.optString("message"));
        } else {
            return res;
        }
    }
}


