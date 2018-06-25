package org.apache.tuscany.sca.databinding.json;

import com.alibaba.fastjson.JSONObject;
import org.apache.axiom.om.util.Base64;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;

import static org.apache.tuscany.sca.databinding.json.client.JSONRPCConstants.RPC_PARAM_CLASSNAME;
import static org.apache.tuscany.sca.databinding.json.client.JSONRPCConstants.RPC_PARAM_KRYO_VAL;

public class KryoJavaBean2JSON extends BaseTransformer<Object, Object> implements PullTransformer<Object, Object> {


    public Object transform(Object source, TransformationContext context) {
        JSONObject result = new JSONObject();
        result.put(RPC_PARAM_CLASSNAME, source.getClass().getName());
        result.put(RPC_PARAM_KRYO_VAL, Base64.encode(SerializerUtil.serialize(source)));
        return result.toJSONString();
    }

    protected Class<Object> getSourceType() {
        return Object.class;
    }

    protected Class<Object> getTargetType() {
        return Object.class;
    }

    @Override
    public String getSourceDataBinding() {
        return JavaBeansDataBinding.NAME;
    }

    @Override
    public String getTargetDataBinding() {
        return JSONDataBinding.NAME;
    }
}
