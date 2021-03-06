package com.JNet.thread;

import com.JNet.http.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.*;

public class HttpThreadPool {

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2,
            4, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100),
            Thread::new, (r, executor) -> executor.getQueue().add(r));

    public static void handleResponse(SocketChannel socketChannel, HttpResponse httpResponse, HttpRequest httpRequest, String responseBody) {
        httpResponse.addHeader("Content-Length", responseBody.getBytes(StandardCharsets.UTF_8).length);
        httpResponse.addHeader("Date", new Date().toString());
        httpResponse.addHeader("Content-Type", "text/html");
        httpResponse.setResponseBody(responseBody);
        if (!httpRequest.headers().containsKey("Cookie") || !httpRequest.headers().get("Cookie").contains("sessionId")) {
            HttpCookie cookie = new HttpCookie();
            cookie.add("sessionId", httpRequest.getSession().getSessionId());
            cookie.setPath(httpRequest.uri());
            httpResponse.addHeader("Set-Cookie", cookie.toString());
        }
        threadPool.submit(() -> {
            try {
                socketChannel.write(ByteBuffer.wrap(httpResponse.toString().getBytes(StandardCharsets.UTF_8)));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
