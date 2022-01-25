package com.JNet.http;

import java.util.Map;

public class HttpRequest {

    private Map<String, String> headers;
    private String method;
    private Map<String, String> parameters;
    private String requestUri;
    private HttpSession httpSession;
    private String body;

    public HttpSession getSession() {
        return this.httpSession;
    }

    public void setSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    private HttpRequest() {

    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder(new HttpRequest());
    }

    public String getParameter(String parameterName) {
        return parameters.get(parameterName);
    }

    public Map<String, String> getParameterMap() {
        return this.parameters;
    }

    public String body() {
        return this.body;
    }

    public String uri() {
        return this.requestUri;
    }

    public static class HttpRequestBuilder {
        private HttpRequest httpRequest;

        private HttpRequestBuilder(HttpRequest httpRequest) {
            this.httpRequest = httpRequest;
        }

        public HttpRequestBuilder method(String method) {
            this.httpRequest.method = method;
            return this;
        }

        public HttpRequestBuilder headers(Map<String, String> headers) {
            this.httpRequest.headers = headers;
            return this;
        }

        public HttpRequestBuilder addHeader(String headerName, String headerValue) {
            this.httpRequest.headers.put(headerName, headerValue);
            return this;
        }

        public HttpRequestBuilder parameters(Map<String, String> parameters) {
            this.httpRequest.parameters = parameters;
            return this;
        }

        public HttpRequestBuilder requestUri(String uri) {
            this.httpRequest.requestUri = uri;
            return this;
        }

        public HttpRequestBuilder body(String body) {
            this.httpRequest.body = body;
            return this;
        }

        public HttpRequest build() {
            return this.httpRequest;
        }
    }
}
