package com.tencent.tcb.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Utils.Format;
import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Document {

    private Db db;
    private String collName;
    private String id;
    private Request request;

    public HashMap<String, Object> projection;

    public Document(@NonNull Db db, @NonNull String collName, @Nullable String docID, @Nullable HashMap<String, Object> projection) {
        this.db = db;
        this.collName = collName;
        this.id = docID;
        this.projection = projection;
        this.request = new Request(this.db.config);
    }

    /**
     * 创建一篇文档
     *
     * @param data 文档数据
     * @return
     * @throws TcbException
     */
    public JSONObject create(HashMap<String, Object> data) throws TcbException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("data", Format.dataFormat(data));
        if (this.id != null && !this.id.isEmpty()) {
            params.put("_id", this.id);
        }

        JSONObject res = this.request.sendMidData("database.addDocument", params);

        if (res.has("code")) {
            return res;
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
     * 创建或添加数据
     *
     * 如果文档ID不存在，则创建该文档并插入数据，根据返回数据的 upserted_id 判断
     * 添加数据的话，根据返回数据的 set 判断影响的行数
     *
     * @param data 文档数据
     * @return
     * @throws TcbException
     */
    public JSONObject set(@NonNull HashMap<String, Object> data) throws TcbException {
        if (data.containsKey("_id")) {
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

        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("multi", false);
        params.put("merge", false);
        params.put("upsert", true);
        params.put("data", data);
        params.put("interfaceCallSource", "SINGLE_SET_DOC");
        if (this.id != null && !this.id.isEmpty()) {
            params.put("_id", this.id);
        }

        JSONObject res = this.request.sendMidData("database.updateDocument", params);

        if (res.has("code")) {
            return res;
        } else {
            JSONObject result = new JSONObject();
            try {
                result.put("requestId", res.getString("requestId"));
                result.put("updated", res.getJSONObject("data").getString("updated"));
                result.put("upsertedId", res.getJSONObject("data").getString("upserted_id"));
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }
            return result;
        }
    }

    /**
     * 更新数据
     *
     * @param data 文档数据
     * @return
     * @throws TcbException
     */
    public JSONObject update(@NonNull HashMap<String, Object> data) throws TcbException {
        if (data.containsKey("_id")) {
            throw new TcbException(Code.INVALID_PARAM, "不能更新_id的值");
        }

        HashMap<String, String> query = new HashMap<>();
        query.put("_id", this.id);

        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("data", Format.dataFormat(data));
        params.put("query", query);
        params.put("multi", false);
        params.put("merge", true);
        params.put("upsert", false);
        params.put("interfaceCallSource", "SINGLE_UPDATE_DOC");

        JSONObject res = this.request.sendMidData("database.updateDocument", params);

        if (res.has("code")) {
            return res;
        } else {
            JSONObject result = new JSONObject();
            try {
                result.put("requestId", res.getString("requestId"));
                result.put("updated", res.getJSONObject("data").getString("updated"));
                result.put("upsertedId", res.getJSONObject("data").getString("upserted_id"));
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }
            return result;
        }
    }

    /**
     * 删除文档
     *
     * @return
     * @throws TcbException
     */
    public JSONObject remove() throws TcbException{
        HashMap<String, String> query = new HashMap<>();
        query.put("_id", this.id);

        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("query", query);
        params.put("multi", false);

        JSONObject res = this.request.sendMidData("database.deleteDocument", params);

        if (res.has("code")) {
            return res;
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

    private boolean checkOperatorMixed(HashMap<String, Object> data) {
        return false;
    }

}
