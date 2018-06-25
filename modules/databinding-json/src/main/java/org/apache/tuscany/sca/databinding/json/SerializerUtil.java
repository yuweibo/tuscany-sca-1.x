package org.apache.tuscany.sca.databinding.json;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.apache.tuscany.sca.databinding.json.KryoUtil.borrowKryo;
import static org.apache.tuscany.sca.databinding.json.KryoUtil.releaseKryo;


public class SerializerUtil {

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null) return null;
        Kryo kryo = borrowKryo();
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            Input input = new Input(byteArrayInputStream, bytes.length);
            T result = (T) kryo.readClassAndObject(input);
            if (null != result && !clazz.equals(result.getClass())) {
                result = null;
            }
            input.close();
            return result;
        } catch (Exception e) {
            //todo
            System.out.println("kryo # [deserialize] error in common deserialization, deserialize class:" + clazz.getName()
                    + ", classType: " + clazz.getComponentType() + ", exception:" + e.getMessage());
            return null;
        } finally {
            releaseKryo(kryo);
        }
    }

    public static <T> byte[] serialize(T value) {
        Kryo kryo = borrowKryo();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Output output = new Output(bos);
            kryo.writeClassAndObject(output, value);
            output.close();
            return bos.toByteArray();
        } catch (Exception e) {
            //todo
            System.out.println("kryo # [serialize] error in value common serialization, class:" + (value == null ? "null" : value.getClass()) + ",exception:" + e.getMessage());
            return new byte[0];
        } finally {
            releaseKryo(kryo);
        }
    }

}
