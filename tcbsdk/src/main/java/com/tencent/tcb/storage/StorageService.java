package com.tencent.tcb.storage;

import android.content.Context;
import android.util.Log;

import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


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

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private void cosUploadFile(String url, String cloudPath, byte[] data, String sign, String cosFileId, String token) throws TcbException {
        final OkHttpClient client = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(MultipartBody.FORM, data);

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
        String TAG = "E";
        try {
            response = client.newCall(request).execute();
            Log.d(TAG, " upload jsonString =" + response);

            if (!response.isSuccessful()) {
                throw new TcbException("E", "upload error code " + response);
            } else {
                Log.d("Info", "OK");
            }

        } catch (IOException e) {
            Log.d(TAG, "upload IOException ", e);
        }

    }


    public void uploadFile(String cloudPath, String filePath) throws JSONException, TcbException, IOException {
        JSONObject metaData = getUploadMetadata(cloudPath);
        Log.d("MetaData", metaData.toString());
        JSONObject data = metaData.getJSONObject("data");
        String url = data.getString("url");
        String token = data.getString("token");
        String cosFileId = data.getString("cosFileId");
        String sign = data.getString("authorization");

        FileInputStream fileInputStream = new FileInputStream(filePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] b = new byte[1024];
        int bytesRead = 0;

        while ((bytesRead = fileInputStream.read(b)) != -1) {
            bos.write(b, 0, bytesRead);
        }

        byte[] fileByteArray = bos.toByteArray();

        cosUploadFile(url, cloudPath, fileByteArray, sign, cosFileId, token);
    }

    public void downloadFile(String fileId, String tempFilePath, OnDownloadListener listener) {
        String tempDownUrl = "";
        try {
            String[] fileList = {fileId};
            JSONObject tempUrlRes = getTempFileURL(fileList);
            tempDownUrl = (String) tempUrlRes.getJSONArray("fileList").get(0);
        } catch (JSONException e) {
            listener.onDownloadFailed(null, new TcbException("GET_URL_ERROR", "get file download url error. detail: " + e.toString()));
            return;
        } catch (TcbException e) {
            listener.onDownloadFailed(null, e);
            return;
        }

        byte[] buf = new byte[2048];
        int len = 0;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder().url(tempDownUrl).build();
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
            listener.onDownloadSuccess();
        } catch (TcbException e) {
            listener.onDownloadFailed(null, e);
        } catch (IOException e) {
            listener.onDownloadFailed(e, null);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                Log.e("IOException", "stream close false" + e.toString());
            }
        }
    }

    public JSONObject deleteFile(String[] fileList) throws JSONException, TcbException {
        String deleteFileAction = "storage.batchDeleteFile";

        if (fileList.length < 1) {
            throw new TcbException("PARAM_INVALID", "fileList must not be empty");
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
            files.add(fileMeta);
        }

        // 构造请求参数
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

    public JSONObject getTempFileURL(ArrayList<FileMeta> fileList) throws TcbException, JSONException {

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

    public interface OnDownloadListener {
        // 下载成功
        void onDownloadSuccess();

        // 下载进度
        void onProgress(int progress);

        // 下载失败
        void onDownloadFailed(IOException e, TcbException err);
    }
}


