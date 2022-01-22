package com.JNet.http;

import java.util.Map;

public class HttpResponse {

    private String responseLine;
    private Map<String, Object> responseHeader;
    private String emptyLine = "\r\n";
    private String responseBody;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        responseHeader.forEach((k, v) -> {
            builder.append(k).append(":").append(v).append("\n");
        });
        return responseLine + builder.toString() + emptyLine + responseBody;
    }

    private HttpResponse() {

    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder(new HttpResponse());
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
