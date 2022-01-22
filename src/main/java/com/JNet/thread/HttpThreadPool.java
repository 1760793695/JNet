package com.JNet.thread;

import com.JNet.http.HttpResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class HttpThreadPool {

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2,
            4, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100),
            Thread::new, (r, executor) -> executor.getQueue().add(r));

    public static void handleResponse(SocketChannel socketChannel, String responseBody) {
        Map<String, Object> responseHeader = new HashMap<>();
        responseHeader.put("Date", new Date().toString());
        responseHeader.put("Content-Length", responseBody.getBytes().length);
        responseHeader.put("Content-Type", "text/html");
        HttpResponse response = HttpResponse.builder().responseLine("HTTP/1.1 200 OK\r\n")
                .responseHeader(responseHeader)
                .responseBody(responseBody).build();
        threadPool.submit(() -> {
            try {
                socketChannel.write(ByteBuffer.wrap(response.toString().getBytes(StandardCharsets.UTF_8)));
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
