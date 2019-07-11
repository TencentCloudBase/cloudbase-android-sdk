package com.tencent.tcb.storage;

import android.content.Context;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
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

    public StorageService(Config config, Context context) {
        this.request = new Request(config, context);
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void cosUploadFile(
            String cloudPath,
            final File file,
            final FileTransportListener listener
    ) throws TcbException, JSONException {
        // 获取临时签名，上传 URL
        JSONObject metaData = getUploadMetadata(cloudPath);
        JSONObject data = metaData.getJSONObject("data");
        String url = data.getString("url");
        String token = data.getString("token");
        String cosFileId = data.getString("cosFileId");
        String sign = data.getString("authorization");

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
                listener.onSuccess();
            } else {
                throw new TcbException("RES_ERR", "upload error code " + response);
            }

        } catch (IOException e) {
            listener.onFailed(new TcbException(Code.IO_ERR, e.toString()));
        }
    }

    public void uploadFile(String cloudPath, File file, FileTransportListener listener) {
        try {
            cosUploadFile(cloudPath, file, listener);
        } catch (JSONException e) {
            listener.onFailed(new TcbException(Code.JSON_ERR, e.toString()));
        } catch (TcbException e) {
            listener.onFailed(e);
        }
    }

    public void uploadFile(String cloudPath, String filePath, FileTransportListener listener) {
        try {
            File file = new File(filePath);
            cosUploadFile(cloudPath, file, listener);
        } catch (JSONException e) {
            listener.onFailed(new TcbException(Code.JSON_ERR, e.toString()));
        } catch (TcbException e) {
            listener.onFailed(e);
        }
    }

    public void downloadFile(String fileId, String tempFilePath, FileTransportListener listener) {
        String tempDownUrl = "";
        try {
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
            listener.onSuccess();
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
                listener.onFailed(new TcbException(Code.IO_ERR,"stream close false " + e.toString() ));
            }
        }
    }

    public JSONObject deleteFile(String[] fileList) throws JSONException, TcbException {
        String deleteFileAction = "storage.batchDeleteFile";

        if (fileList.length < 1) {
            throw new TcbException("PARAM_INVALID", "fileList must not be empty");
        }

        for (String s : fileList) {
            if (s.isEmpty()) {
                throw new TcbException("PARAM_INVALID", "fileList must not be empty");
            }
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("fileid_list", fileList);
        JSONObject res = request.send(deleteFileAction, params);

        if (res.has("code")) {
            throw new TcbException(res.getString("code"), res.getString("message"));
        } else {
            JSONObject result = new JSONObject();
            JSONObject data = result.getJSONObject("data");
            result.put("requestId", result.getString("requestId"));
            result.put("fileList", result.getJSONArray("delete_list"));
            return result;
        }
    }

    public JSONObject getTempFileURL(String[] fileList) throws TcbException, JSONException {
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
    }

    public JSONObject getTempFileURL(ArrayList<FileMeta> fileList) throws TcbException,
            JSONException {

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
    }

    public JSONObject getUploadMetadata(String cloudPath) throws JSONException, TcbException {
        final String action = "storage.getUploadMetadata";
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("path", cloudPath);
        JSONObject res = request.send(action, requestParams);

        // 存在 code，说明返回值存在异常
        if (res.has("code")) {
            throw new TcbException(res.getString("code"), res.getString("message"));
        } else {
            return res;
        }
    }

    public interface FileTransportListener {
        // 传输成功
        void onSuccess();

        // 传输进度
        void onProgress(int progress);

        // 传输失败
        void onFailed(TcbException e);
    }
}

