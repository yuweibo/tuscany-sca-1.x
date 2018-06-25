package org.apache.tuscany.sca.databinding.json;

import com.alibaba.fastjson.JSONObject;
import org.apache.axiom.om.util.Base64;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;

import static org.apache.tuscany.sca.databinding.json.client.JSONRPCConstants.RPC_PARAM_CLASSNAME;
import static org.apache.tuscany.sca.databinding.json.client.JSONRPCConstants.RPC_PARAM_KRYO_VAL;

public class KryoJSON2JavaBean implements PullTransformer<Object, Object> {

    public Object transform(Object source, TransformationContext context) {
        JSONObject paramJsonObj = JSONObject.parseObject((String) source);
        try {
            return SerializerUtil.deserialize(Base64.decode(paramJsonObj.getString(RPC_PARAM_KRYO_VAL)), Class.forName(paramJsonObj.getString(RPC_PARAM_CLASSNAME)));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class.forName error", e);
        }
    }

    public String getSourceDataBinding() {
        return JSONDataBinding.NAME;
    }

    public String getTargetDataBinding() {
        return JavaBeansDataBinding.NAME;
    }

    public int getWeight() {
        return 5000;
    }
}
