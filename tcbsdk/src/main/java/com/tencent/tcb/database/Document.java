package com.tencent.tcb.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Utils.Format;
import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.TcbException;
import com.tencent.tcb.utils.TcbListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Document {

    private Db db;
    private String collName;
    private String id;
    private Request request;
    private HashMap<String, Number> projection;

    public Document(@NonNull Db db, @NonNull String collName, @Nullable String docID) {
        this(db, collName, docID, new HashMap<String, Number>());
    }

    private Document(@NonNull Db db, @NonNull String collName, @Nullable String docID, @Nullable HashMap<String, Number> projection) {
        this.db = db;
        this.collName = collName;
        this.id = docID;
        this.projection = projection;
        this.request = new Request(this.db.config, this.db.context);
    }

    /**
     * 创建一篇文档
     *
     * @param data 文档数据
     * @return
     * @throws TcbException
     */
    public JSONObject create(@NonNull JSONObject data) throws TcbException {
        // 格式化
        try {
            data = Format.dataFormat(data);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("data", data);
        if (this.id != null && !this.id.isEmpty()) {
            params.put("_id", this.id);
        }

        JSONObject res = this.request.sendMidData("database.addDocument", params);

        if (res.has("code")) {
            throw new TcbException(res.optString("code"), res.optString("message"));
        } else {
            JSONObject result = new JSONObject();
            try {
                result.put("requestId", res.getString("requestId"));
                result.put("id", res.getJSONObject("data").getString("_id"));
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }
            return result;
        }
    }

    /**
     * 创建一篇文档（异步）
     *
     * @param listener
     */
    public void createAsync(@NonNull final JSONObject data, @NonNull final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = create(data);
                    listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }

    /**
     * 创建或添加数据
     *
     * 如果文档ID不存在，则创建该文档并插入数据，根据返回数据的 upserted_id 判断
     * 添加数据的话，根据返回数据的 set 判断影响的行数
     *
     * @param data 文档数据
     * @return
     * @throws TcbException
     */
    public JSONObject set(@NonNull JSONObject data) throws TcbException {
        if (data.has("_id")) {
            throw new TcbException(Code.INVALID_PARAM, "不能更新_id的值");
        }

        if (this.id == null || this.id.isEmpty()) {
            throw new TcbException(Code.INVALID_PARAM, "docId不能为空");
        }

        boolean hasOperator = this.checkOperatorMixed(data);
        // 不能包含操作符
        if (hasOperator) {
            throw new TcbException(Code.DATABASE_REQUEST_FAILED, "update operator complicit");
        }

        // 格式化
        try {
            data = Format.dataFormat(data);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("multi", false);
        params.put("merge", false);
        params.put("upsert", true);
        params.put("data", data);
        params.put("interfaceCallSource", "SINGLE_SET_DOC");

        if (this.id != null && !this.id.isEmpty()) {
            try {
                JSONObject query = new JSONObject();
                query.put("_id", this.id);
                params.put("query", query);
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.toString());
            }
        }

        JSONObject res = this.request.sendMidData("database.updateDocument", params);

        if (res.has("code")) {
            throw new TcbException(res.optString("code"), res.optString("message"));
        } else {
            JSONObject result = new JSONObject();
            try {
                result.put("requestId", res.getString("requestId"));
                result.put("updated", res.getJSONObject("data").getInt("updated"));
                result.put("upsertedId", res.getJSONObject("data").getString("upserted_id"));
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }
            return result;
        }
    }

    /**
     * 创建或添加数据（异步）
     *
     * 如果文档ID不存在，则创建该文档并插入数据，根据返回数据的 upserted_id 判断
     * 添加数据的话，根据返回数据的 set 判断影响的行数
     *
     * @param data
     * @param listener
     */
    public void setAsync(@NonNull final JSONObject data, @NonNull final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = set(data);
                    listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }

    /**
     * 更新数据
     *
     * @param data 文档数据
     * @return
     * @throws TcbException
     */
    public JSONObject update(@NonNull JSONObject data) throws TcbException {
        if (data.has("_id")) {
            throw new TcbException(Code.INVALID_PARAM, "不能更新_id的值");
        }

        // 格式化
        try {
            data = Format.dataFormat(data);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }

        HashMap<String, String> query = new HashMap<>();
        query.put("_id", this.id);

        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("data", data);
        params.put("query", query);
        params.put("multi", false);
        params.put("merge", true);
        params.put("upsert", false);
        params.put("interfaceCallSource", "SINGLE_UPDATE_DOC");

        JSONObject res = this.request.sendMidData("database.updateDocument", params);

        if (res.has("code")) {
            throw new TcbException(res.optString("code"), res.optString("message"));
        } else {
            JSONObject result = new JSONObject();
            try {
                result.put("requestId", res.getString("requestId"));
                result.put("updated", res.getJSONObject("data").getInt("updated"));
                result.put("upsertedId", res.getJSONObject("data").getString("upserted_id"));
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }
            return result;
        }
    }

    /**
     * 更新数据（异步）
     *
     * @param data
     * @param listener
     */
    public void updateAsync(@NonNull final JSONObject data, @NonNull final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = update(data);
                    listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }

    /**
     * 删除文档
     *
     * @return
     * @throws TcbException
     */
    public JSONObject remove() throws TcbException {
        HashMap<String, String> query = new HashMap<>();
        query.put("_id", this.id);

        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("query", query);
        params.put("multi", false);

        JSONObject res = this.request.sendMidData("database.deleteDocument", params);

        if (res.has("code")) {
            throw new TcbException(res.optString("code"), res.optString("message"));
        } else {
            JSONObject result = new JSONObject();
            try {
                result.put("requestId", res.getString("requestId"));
                result.put("deleted", res.getJSONObject("data").getString("deleted"));
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }
            return result;
        }
    }

    /**
     * 删除文档（异步）
     *
     * @param listener
     */
    public void removeAsync(@NonNull final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = remove();
                    listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }

    public JSONObject get() throws TcbException {
        HashMap<String, String> query = new HashMap<>();
        query.put("_id", this.id);

        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("query", query);
        params.put("multi", false);
        params.put("projection", this.projection);

        JSONObject res = this.request.send("database.queryDocument", params);

        if (res.has("code")) {
            throw new TcbException(res.optString("code"), res.optString("message"));
        } else {
            JSONObject result = new JSONObject();
            try {
                JSONArray documents = res.getJSONObject("data").getJSONArray("list");
                documents = Util.formatResDocumentData(documents);

                JSONObject data = res.getJSONObject("data");

                result.put("requestId", res.getString("requestId"));
                result.put("data", documents);

                if (data.has("total")) {
                    result.put("total", data.getInt("total"));
                }
                if (data.has("limit")) {
                    result.put("limit", data.getInt("limit"));
                }
                if (data.has("offset")) {
                    result.put("offset", data.getInt("offset"));
                }
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }
            return result;
        }
    }

    public void getAsync(@NonNull final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = get();
                    listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }

    /**
     * 指定要返回的字段
     *
     * @param projection
     * @return
     */
    public Document field(@NonNull HashMap<String, Boolean> projection) {
        // 把true和false转义为1和0
        HashMap<String, Number> newProjection = new HashMap<>();
        for (Map.Entry<String, Boolean> entry : projection.entrySet()) {
            if (entry.getValue()) {
                newProjection.put(entry.getKey(), 1);
            } else {
                newProjection.put(entry.getKey(), 0);
            }
        }
        return new Document(this.db, this.collName, this.id, newProjection);
    }

    private boolean checkOperatorMixed(@NonNull JSONObject data) {
        //todo:
        return false;
    }

}
