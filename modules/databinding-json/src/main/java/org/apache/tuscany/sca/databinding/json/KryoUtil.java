package org.apache.tuscany.sca.databinding.json;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * https://blog.csdn.net/fanjunjaden/article/details/72823866
 */
public class KryoUtil {

    private static KryoPool kryoPool;

    public static Kryo borrowKryo() {
        if (kryoPool == null) {
            kryoPool = new KryoPool.Builder(new KryoFactory() {
                @Override
                public Kryo create() {
                    Kryo kryo = new Kryo();
                    kryo.setRegistrationRequired(false);
                    kryo.setReferences(false);
                    kryo.setDefaultSerializer(FieldSerializer.class);
                    //解决对象没有无参构造方法的问题
                    kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
                    return kryo;
                }
            }).softReferences().build();
        }
        return kryoPool.borrow();
    }

    public static void releaseKryo(Kryo kryo) {
        kryoPool.release(kryo);
    }
}
