package com.JNet.interceptor;

import com.JNet.http.HttpRequest;

public interface Interceptor {

    boolean preHandler(HttpRequest httpRequest);
}
