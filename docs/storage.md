# 存储

<!-- TOC -->
- 存储
  - [上传文件](#上传文件)
  - [获取文件下载链接](#获取文件下载链接)
  - [删除文件](#删除文件)
<!-- /TOC -->

### 简单用法

下面所有的方法均已挂载到 TCB 类，可以直接使用，如：

```java
import com.tencent.tcb.TCB;

TCB tcb = new TCB("envName", context);
tcb.storage.uploadFile();
```

等价于

```java
import com.tencent.tcb.storage.StorageService;

StorageService storage = new StorageService("envName", context);
storage.uploadFile();
```

### 上传文件

- `public void uploadFile(String cloudPath, File file, TcbStorageListener listener)`
- `public void uploadFile(String cloudPath, String filePath, TcbStorageListener listener)`

请求参数

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | --- |
| cloudPath | String | 是 | 文件的绝对路径，包含文件名。例如 foo/bar.jpg、foo/bar/baz.jpg 等，不能包含除[0-9 , a-z , A-Z]、/、!、-、_、.、、* 和中文以外的字符，使用 / 字符来实现类似传统文件系统的层级结构。[查看详情](https://cloud.tencent.com/document/product/436/13324)
| filePath | String | 是 | 上传文件本地路径
| file | File | 是 | 上传的文件
| listener | TcbStorageListener | 是 | 上传状态监听器

TcbStorageListener

```java
interface TcbStorageListener {
    // 请求成功
    void onSuccess(JSONObject result);
    // 请求失败
    void onFailed(TcbException e);
    // 传输进度
    void onProgress(int progress);
}
```

示例代码

```java
StorageService storage = new StorageService("envName", context);
storage.uploadFile("txt/data.txt", filePath, new TcbStorageListener() {
    @Override
    public void onSuccess(JSONObject result) {
        Log.d(LogTag, result.toString());
        String requestId = result.optString("requestId");
        fileId = result.optString("fileId");
    }
    @Override
    public void onProgress(int progress) {
        // 上传进度
    }
    @Override
    public void onFailed(TcbException e) {
        // 上传失败
    }
});
```

### 下载文件

`public void downloadFile(String fileId, String tempFilePath, TcbStorageListener listener)`

请求参数

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | --- |
| fileId | String | 是 | 要下载的文件 ID
| tempFilePath | String | 是 | 要下载的文件的存储路径
| listener | TcbStorageListener | 是 | 下载文件状态监听函数

TcbStorageListener

```java
interface TcbStorageListener {
    // 请求成功
    void onSuccess(JSONObject result);
    // 请求失败
    void onFailed(TcbException e);
    // 传输进度
    void onProgress(int progress);
}
```

示例代码

```java
StorageService storage = new StorageService("envName", context);
storage.downloadFile(
    fileId,
    filePath,
    new TcbStorageListener() {
        @Override
        public void onSuccess(JSONObject result) {
            // result 为 null
        }
        @Override
        public void onProgress(int progress) {
            // 下载进度
        }
        @Override
        public void onFailed(TcbException e) {
            // 下载错误
        }
    }
);
```

### 删除文件

`public JSONObject deleteFile(String[] fileList)`

请求参数

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | --- |
| fileList | String[] | 是 | 要删除的文件 ID 组成的数组

响应参数

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | --- |
| code | String | 否 | 状态码，操作成功则不返回
| message | String | 否 | 错误描述
| fileList | JSONArray[JSONObject<code, fileID>] | 否 | 删除结果组成的数组
| requestId | String | 否 | 请求序列号，用于错误排查

fileList

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | --- |
| code | String | 否 | 删除结果，成功为 SUCCESS
| fileID | String | 否 | 文件ID

示例代码

```java
String fileId = "xxx";
String[] fileList = {fileId};
StorageService storage = new StorageService("envName", context);

try {
    JSONObject res = storage.deleteFile(fileList);
    String requestId = res.optString("requestId");
    JSONArray resFileList = res.getJSONArray("fileList");
    JSONObject file = resFileList.getJSONObject(0);
    String code = file.getString("code"); // code == "SUCCESS"
} catch (TcbException e) {
    // 异常
} catch (JSONException e) {
    // JSON 解析失败
}
```

### 获取文件临时下载链接

- `public JSONObject getTempFileURL(String[] fileList)`
- `public JSONObject getTempFileURL(ArrayList<FileMeta> fileList)`

请求参数

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | --- |
| fileList | String[] | 是 | 要下载的文件的 fileId 组成的数组
| fileList | ArrayList<FileMeta> | 是 | 下载的文件信息组成的数组

FileMeta

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | --- |
| fileID | String | 是 | 要下载的文件的 fileId
| maxAge | Int | 是 | 临时下载链接的有效时间，单位：秒（S）

响应参数

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | --- |
| code | String | 否 | 状态码，操作成功则不返回
| message | String | 否 | 错误描述
| fileList | JSONArray[JSONObject<fileID, tempFileURL>] | 否 | 下载文件的临时 URL 数组
| requestId | String | 否 | 请求序列号，用于错误排查

fileList

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | --- |
| code | String | 否 | 获取文件下载链接结果，成功为 SUCCESS
| fileID | String | 否 | 文件ID
| tempFileURL | String | 否 | 文件临时下载链接
| download_url | String | 否 | 文件临时下载链接



示例代码

```java
String fileId = "xxx";
String[] fileList = {fileId};
StorageService storage = new StorageService("envName", context);

try {
    JSONObject res = storage.getTempFileURL(fileList);
    String requestId = res.optString("requestId");
    JSONArray resFileList = res.getJSONArray("fileList");
    JSONObject file = resFileList.getJSONObject(0);
    String tempFileURL = file.getString("tempFileURL"); // 临时下载链接
} catch (TcbException e) {
    // 失败
} catch (JSONException e) {
    // JSON 解析错误
}
```
