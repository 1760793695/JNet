package com.JNet.interceptor;

import com.JNet.http.HttpRequest;

import java.util.*;

public class InterceptorRegistry {

    private Map<String, List<Interceptor>> interceptors = new HashMap<>();

    public void addInterceptor(String uri, Interceptor interceptor) {
        List<Interceptor> inters = this.interceptors.get(uri);
        if (inters == null) {
            List<Interceptor> list = new ArrayList<>();
            list.add(interceptor);
            interceptors.put(uri, list);
        } else {
            inters.add(interceptor);
        }
    }

    public boolean doInterceptor(String uri, HttpRequest httpRequest) {
        List<Interceptor> chain = this.interceptors.get(uri);
        if (chain != null) {
            for (Interceptor interceptor : chain) {
                boolean result = interceptor.preHandler(httpRequest);
                if (!result) {
                    return false;
                }
            }
        }
        return true;
    }
}
