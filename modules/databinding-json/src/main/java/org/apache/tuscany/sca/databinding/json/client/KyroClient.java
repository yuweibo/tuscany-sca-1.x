package org.apache.tuscany.sca.databinding.json.client;


import java.lang.reflect.Proxy;

public class KyroClient {

    public static <T> T lookup(Class<T> t, String url) {
        return (T) Proxy.newProxyInstance(KyroClient.class.getClassLoader(), new Class[]{t}, new KryoRPCHandler(url));
    }

}

