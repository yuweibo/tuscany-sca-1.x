package org.apache.tuscany.sca.databinding.json.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.axiom.om.util.Base64;
import org.apache.tuscany.sca.databinding.json.SerializerUtil;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.tuscany.sca.databinding.json.client.JSONRPCConstants.*;

class KryoRPCHandler implements InvocationHandler {

    private static final MediaType JSON_MEDIA_TYPE
            = MediaType.parse("application/json; charset=utf-8");

    private static final AtomicLong id = new AtomicLong(0);

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS).build();

    private String url;

    public KryoRPCHandler(String url) {
        this.url = url;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String content = requestContent(method, args);
        Response response = execute(content);
        String responseStr = response.body().string();
        JSONObject responseJson = JSONObject.parseObject(responseStr);
        Long id = responseJson.getLong(RPC_BODY_ID);
        if (responseJson.containsKey(RPC_RESPONSE_RESULT)) {
            return handleResult(responseJson);
        } else if (responseJson.containsKey(RPC_RESPONSE_EXCEPTION)) {
            throw handleException(responseJson, RPC_RESPONSE_EXCEPTION);
        } else if (responseJson.containsKey(RPC_RESPONSE_SYS_EXCEPTION)) {
            throw handleException(responseJson, RPC_RESPONSE_SYS_EXCEPTION);
        } else {
            throw new IllegalArgumentException("unknown jsonrpc result!!!");
        }
    }

    private Object handleResult(JSONObject resultJson) throws ClassNotFoundException {
        JSONObject result = resultJson.getJSONObject(RPC_RESPONSE_RESULT);
        return SerializerUtil.deserialize(Base64.decode(result.getString(RPC_PARAM_KRYO_VAL)), Class.forName(result.getString(RPC_PARAM_CLASSNAME)));
    }

    private Response execute(String content) throws IOException {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, content);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return okHttpClient.newCall(request).execute();
    }

    private String requestContent(Method method, Object[] args) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(RPC_BODY_ID, id.incrementAndGet());
        requestBody.put(RPC_REQUEST_BODY_METHOD, method.getName());
        List<String> params = new ArrayList<>();
        for (Object arg : args) {
            JSONObject paramJsonObj = new JSONObject();
            paramJsonObj.put(RPC_PARAM_CLASSNAME, arg.getClass().getName());
            paramJsonObj.put(RPC_PARAM_KRYO_VAL, Base64.encode(SerializerUtil.serialize(arg)));
            params.add(paramJsonObj.toJSONString());
        }
        requestBody.put(PRC_REQUEST_BODY_PARAMS, params);
        return JSON.toJSONString(requestBody);
    }

    private Throwable handleException(JSONObject resultJson, String exceptionType) {
        JSONObject exceptionJson = resultJson.getJSONObject(exceptionType);
        try {
            String exceptionStr = exceptionJson.getString(RPC_PARAM_KRYO_VAL);
            //todo
            System.out.println("execptionType:" + exceptionType + ",exceptionStr:" + exceptionStr);
            return (Throwable) JSON.parseObject(exceptionStr, Class.forName(exceptionJson.getString(RPC_PARAM_CLASSNAME)));
        } catch (ClassNotFoundException e) {
            //todo
            System.out.println("ClassNotFoundException" + e.getMessage());
            return e;
        }
    }
}
