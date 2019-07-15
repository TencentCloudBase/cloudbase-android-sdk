package com.tencent.tcb.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Utils.Format;
import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Query {
    protected Db db;
    protected String collName;
    private HashMap<String, Object> fieldFilters;
    private ArrayList<HashMap<String, String>> fieldOrders;
    private HashMap<String, Object> queryOptions;

    private Request request;

    /**
     * 初始化
     *
     * @param db        - 数据库的引用
     * @param collName  - 集合名称
     */
    public Query(@NonNull Db db, @NonNull String collName) {
        this(db, collName, new HashMap<String, Object>(), new ArrayList<HashMap<String, String>>(), new HashMap<String, Object>());
    }

    /**
     * 初始化
     *
     * @param db            - 数据库的引用
     * @param collName      - 集合名称
     * @param fieldFilters  - 过滤条件
     * @param fieldOrders   - 排序条件
     * @param queryOptions  - 查询条件
     */
    public Query(@NonNull Db db, @NonNull String collName, @NonNull HashMap<String, Object> fieldFilters, @NonNull ArrayList<HashMap<String, String>> fieldOrders, @NonNull HashMap<String, Object> queryOptions) {
        this.db = db;
        this.collName = collName;
        this.fieldFilters = fieldFilters;
        this.fieldOrders = fieldOrders;
        this.queryOptions = queryOptions;
        this.request = new Request(this.db.config, this.db.context);
    }

    public JSONObject get() throws TcbException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);

        // 处理排序条件
        ArrayList<HashMap<String, String>> cloneFieldOrders = new ArrayList<>();
        for (HashMap<String, String> order : this.fieldOrders)  {
            cloneFieldOrders.add(order);
        }
        if (cloneFieldOrders.size() > 0) {
            params.put("order", cloneFieldOrders);
        }

        // 处理过滤条件
        params.put("query", this.fieldFilters);

        // 处理查询条件
        if (this.queryOptions.containsKey("offset")) {
            int offset = (int)this.queryOptions.get("offset");
            if (offset > 0) {
                params.put("offset", offset);
            }
        }
        if (this.queryOptions.containsKey("limit")) {
            int limit = (int)this.queryOptions.get("limit");
            params.put("limit", Math.min(limit, 100));
        } else {
            params.put("limit", 100);
        }
        if (this.queryOptions.containsKey("projection")) {
            params.put("projection", this.queryOptions.get("projection"));
        }

        JSONObject res = this.request.sendMidData("database.queryDocument", params);

        if (res.has("code")) {
            return res;
        } else {
            JSONObject result = new JSONObject();
            try {
                JSONArray documents = res.getJSONObject("data").getJSONArray("list");
                documents = Util.formatResDocumentData(documents);

                result.put("requestId", res.getString("requestId"));
                result.put("data", documents);

                if (res.has("TotalCount")) {
                    result.put("TotalCount", res.getString("TotalCount"));
                }
                if (res.has("Limit")) {
                    result.put("Limit", res.getString("Limit"));
                }
                if (res.has("Offset")) {
                    result.put("Offset", res.getString("Offset"));
                }
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }
            return result;
        }
    }

    public JSONObject count() throws TcbException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("query", this.fieldFilters);

        JSONObject res = this.request.sendMidData("database.queryDocument", params);
        if (res.has("code")) {
            return res;
        } else {
            JSONObject result = new JSONObject();
            try {
                result.put("requestId", res.getString("requestId"));
                result.put("total", res.getJSONObject("data").getString("total"));
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }

            return result;
        }
    }

    /**
     * 查询条件
     *
     * @param query
     * @return
     * @throws TcbException
     */
    public Query where(HashMap<String, Object> query) throws TcbException {
        return new Query(this.db, this.collName, Format.dataFormat(query),this.fieldOrders, this.queryOptions);
    }

    /**
     * 设置排序方式
     *
     * @param fieldPath     字段路径
     * @param directionStr  排序方式
     * @return
     * @throws TcbException
     */
    public Query orderBy(String fieldPath, String directionStr) throws TcbException {
        Validate.isFieldPath(fieldPath);
        Validate.isFieldOrder(directionStr);

        HashMap<String, String> newOrder = new HashMap<>();
        newOrder.put("direction", directionStr);
        newOrder.put("field", fieldPath);

        ArrayList<HashMap<String, String>> combinedOrders = new ArrayList<>();
        combinedOrders.addAll(this.fieldOrders);
        combinedOrders.add(newOrder);

        return new Query(this.db, this.collName, this.fieldFilters, combinedOrders, this.queryOptions);
    }

    /**
     *
     * @param limit
     * @return
     */
    public Query limit(int limit) {
        HashMap<String, Object> combinedOptions = new HashMap<>();
        combinedOptions.putAll(this.queryOptions);
        combinedOptions.put("limit", limit);

        return new Query(this.db, this.collName, this.fieldFilters, this.fieldOrders, combinedOptions);
    }

    public Query skip(int offset) {
        HashMap<String, Object> combinedOptions = new HashMap<>();
        combinedOptions.putAll(this.queryOptions);
        combinedOptions.put("offset", offset);

        return new Query(this.db, this.collName, this.fieldFilters, this.fieldOrders, combinedOptions);
    }

    /**
     * 发起请求批量更新文档
     *
     * @param data
     * @return
     * @throws TcbException
     */
    public JSONObject update(@NonNull JSONObject data) throws TcbException {
        if (data.has("_id")) {
            throw new TcbException(Code.INVALID_PARAM, "不能更新_id的值");
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("query", this.fieldFilters);
        params.put("multi", true);
        params.put("merge", true);
        params.put("upsert", false);
        params.put("data", Format.dataFormat(data));
        params.put("interfaceCallSource", "BATCH_UPDATE_DOC");

        JSONObject res = request.sendMidData("database.updateDocument", params);
        if (res.has("code")) {
            return res;
        } else {
            JSONObject result = new JSONObject();
            try {
                result.put("requestId", res.getString("requestId"));
                result.put("upsertedId", res.getJSONObject("data").getString("upserted_id"));
                result.put("updated", res.getJSONObject("data").getBoolean("updated"));
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }

            return  result;
        }
    }

    /**
     * 指定要返回的字段
     *
     * @param projection
     * @return
     */
    public Query field(@NonNull HashMap<String, Boolean> projection) {
        // 把true和false转义为1和0
        HashMap<String, Number> newProjection = new HashMap<>();
        for (Map.Entry<String, Boolean> entry : projection.entrySet()) {
            if (entry.getValue()) {
                newProjection.put(entry.getKey(), 1);
            } else {
                newProjection.put(entry.getKey(), 0);
            }
        }

        HashMap<String, Object> option = new HashMap<>();
        option.putAll(this.queryOptions);
        option.put("projection", newProjection);

        return new Query(this.db, this.collName, this.fieldFilters, this.fieldOrders, option);
    }

    /**
     * 条件删除文档
     *
     * @return
     * @throws TcbException
     */
    public JSONObject remove() throws TcbException {
        if (this.queryOptions.size() > 0) {
            Log.w("Database.Query", "`offset`, `limit` and `projection` are not supported in remove() operation");
        }

        if (this.fieldFilters.size() > 0) {
            Log.w("Database.Query", "`orderBy` is not supported in remove() operation");
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", this.collName);
        params.put("query", this.fieldFilters);
        params.put("multi", true);

        JSONObject res = this.request.sendMidData("database.deleteDocument", params);
        if (res.has("code")) {
            return res;
        } else {
            JSONObject result = new JSONObject();
            try {
                result.put("requestId", res.getString("requestId"));
                result.put("deleted", res.getJSONObject("data").getBoolean("deleted"));
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.getMessage());
            }

            return  result;
        }

    }
}
