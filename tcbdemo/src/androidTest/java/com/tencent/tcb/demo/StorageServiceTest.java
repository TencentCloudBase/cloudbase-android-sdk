package com.tencent.tcb.demo;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.tencent.tcb.storage.StorageService;
import com.tencent.tcb.utils.TcbException;
import com.tencent.tcb.utils.TcbStorageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageServiceTest {
    private static String filePath = "";
    private static StorageService storage = null;
    private static String fileId = "";

    @BeforeClass
    public static void prepareTest() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // 初始化 storage
        storage = new StorageService(Constants.envName, context);
        String root = "/data/data/com.tencent.tcb.demo";
        filePath = createFile(root);
    }

    // 创建一个测试用的文件
    public static String createFile(String root) {
        String filePath = root + "/data.txt";
        File file = new File(filePath);
        String data = "Data to write";
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            out.write(data.getBytes());
            return filePath;
        } catch (IOException e) {
            fail(e.toString());
            return "";
        }
    }

    // 测试文件上传
    @Test
    public void storageTest1() {
        storage.uploadFile("txt/data.txt", filePath, new TcbStorageListener() {
            @Override
            public void onSuccess(JSONObject result) {
                String requestId = result.optString("requestId");
                fileId = result.optString("fileId");
                assertFalse(requestId.isEmpty());
                assertFalse(fileId.isEmpty());
            }

            @Override
            public void onProgress(int progress) {
                assertTrue(progress >= 0);
                assertTrue(progress <= 100);
            }

            @Override
            public void onFailed(TcbException e) {
                fail(e.toString());
            }
        });
    }

    // 测试获取临时下载链接
    @Test
    public void storageTest2() {
        final String[] fileList = {fileId};
        try {
            JSONObject res = storage.getTempFileURL(fileList);
            String requestId = res.optString("requestId");
            JSONArray resFileList = res.getJSONArray("fileList");
            assertFalse(requestId.isEmpty());
            assertNotEquals(resFileList.length(), 0);
            JSONObject file = resFileList.getJSONObject(0);
            String fileID = file.getString("fileID");
            assertFalse(fileID.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // 测试获取上传文件元数据
    @Test
    public void storageTest3() {
        String cloudPath = "test";
        try {

            JSONObject metaData = storage.getUploadMetadata(cloudPath);
            JSONObject data = metaData.getJSONObject("data");
            String url = data.getString("url");
            String token = data.getString("token");
            String cosFileId = data.getString("cosFileId");
            String sign = data.getString("authorization");
            String requestId = metaData.getString("requestId");
            String resFileId = data.getString("fileId");
            assertFalse(url.isEmpty());
            assertFalse(token.isEmpty());
            assertFalse(cosFileId.isEmpty());
            assertFalse(sign.isEmpty());
            assertFalse(requestId.isEmpty());
            assertFalse(resFileId.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // 测试下载文件
    @Test
    public void storageTest4() {
        storage.downloadFile(
                fileId,
                filePath,
                new TcbStorageListener() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // result 为 null
                        assertNull(result);
                    }

                    @Override
                    public void onProgress(int progress) {
                        assertTrue(progress >= 0);
                        assertTrue(progress <= 100);
                    }

                    @Override
                    public void onFailed(TcbException e) {
                        fail(e.toString());
                    }
                });
    }

    // 测试删除文件
    @Test
    public void storageTest5() {
        String[] fileList = {fileId};

        try {
            JSONObject res = storage.deleteFile(fileList);
            String requestId = res.optString("requestId");
            JSONArray resFileList = res.getJSONArray("fileList");
            assertFalse(requestId.isEmpty());
            assertNotEquals(resFileList.length(), 0);
            JSONObject file = resFileList.getJSONObject(0);
            String code = file.getString("code");
            assertEquals(code, "SUCCESS");
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

}