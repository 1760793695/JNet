package com.JNet.http;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private String responseLine;
    private Map<String, Object> responseHeader;
    private static final String emptyLine = "\r\n";
    private String responseBody;
    private HttpCookie cookie;

    public HttpCookie getCookie() {
        return this.cookie;
    }

    public void setCookie(HttpCookie cookie) {
        this.cookie = cookie;
    }

    private HttpResponse() {
        this.responseHeader = new HashMap<>();
    }

    public Map<String, Object> headers() {
        return this.responseHeader;
    }

    public void addHeader(String name, Object value) {
        this.responseHeader.put(name, value);
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder(new HttpResponse());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        responseHeader.forEach((k, v) -> {
            builder.append(k).append(":").append(v).append("\n");
        });
        return responseLine + "\n" + builder.toString() + emptyLine + responseBody;
    }



    public static class HttpResponseBuilder {
        private HttpResponse httpResponse;

        private HttpResponseBuilder(HttpResponse httpResponse) {
            this.httpResponse = httpResponse;
        }

        public HttpResponseBuilder responseLine(String responseLine) {
            this.httpResponse.responseLine = responseLine;
            return this;
        }

        public HttpResponseBuilder responseHeader(Map<String, Object> responseHeader) {
            this.httpResponse.responseHeader = responseHeader;
            return this;
        }

        public HttpResponseBuilder responseBody(String responseBody) {
            this.httpResponse.responseBody = responseBody;
            return this;
        }

        public HttpResponse build() {
            return this.httpResponse;
        }
    }
}
