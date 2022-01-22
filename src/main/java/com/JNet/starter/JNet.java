package com.JNet.starter;

import com.JNet.http.HttpRequest;
import com.JNet.http.HttpRequestListener;
import com.JNet.http.Route;
import com.JNet.interceptor.Interceptor;
import com.JNet.interceptor.InterceptorRegistry;
import com.JNet.thread.HttpThreadPool;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class JNet {

    private Map<String, Route> routers;
    private int maxReadable;
    private InterceptorRegistry registry;

    private JNet(int xml) {
        SAXBuilder builder = new SAXBuilder();
        try {
            InputStream is = new FileInputStream("src/main/resources/jnet.xml");
            Document document = builder.build(is);
            Integer readable = parseReadable(document);
            if (readable == 0) {
                this.maxReadable = 1024;
            } else {
                this.maxReadable = readable;
            }
            Map<String, List<String>> map = parseInterceptors(document);
            if (map != null && !map.isEmpty()) {
                InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
                map.forEach((k, v) -> {
                    try {
                        v.forEach(e -> {
                            try {
                                interceptorRegistry.addInterceptor(k, (Interceptor) Class.forName(e).newInstance());
                            } catch (Exception instantiationException) {
                                throw new RuntimeException("拦截器定义异常");
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException("拦截器定义异常");
                    }
                });
                this.registry = interceptorRegistry;
            }
            this.routers = parseRouters(document);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("配置文件加载失败，请检查配置文件");
        }
    }

    private Map<String, Route> parseRouters(Document document) {
        Map<String, Route> map = new HashMap<>();
        Element rootElement = document.getRootElement();
        Element routers = rootElement.getChild("routers");
        List<Element> children = routers.getChildren();
        children.forEach(e -> {
            Route route = new Route();
            route.setMethod(e.getAttribute("method").getValue());
            String listenerClass = e.getAttribute("listenerClass").getValue();
            try {
                route.setListener((HttpRequestListener) Class.forName(listenerClass).newInstance());
                map.put(e.getAttribute("path").getValue(), route);
            } catch (Exception classNotFoundException) {
                throw new RuntimeException("路由创建错误，请检查路由配置");
            }
        });
        return map;
    }

    private Map<String, List<String>> parseInterceptors(Document document) {
        Element rootElement = document.getRootElement();
        Element interceptors = rootElement.getChild("interceptors");
        if (interceptors == null) {
            return null;
        }
        Map<String, List<String>> map = new HashMap<>();
        List<Element> children = interceptors.getChildren();
        children.forEach(e -> {
            String path = e.getAttribute("path").getValue();
            String interceptorClass = e.getAttribute("class").getValue();
            if (map.containsKey(path)) {
                map.get(path).add(interceptorClass);
            } else {
                List<String> classes = new ArrayList<>();
                classes.add(interceptorClass);
                map.put(path, classes);
            }
        });
        return map;
    }

    private JNet() {
        this.maxReadable = 1024;
        this.routers = new HashMap<>();
    }

    public static JNet open() {
        return new JNet();
    }

    public static JNet open(int maxReadable) {
        JNet jNet = new JNet();
        jNet.maxReadable = maxReadable;
        jNet.routers = new HashMap<>();
        return jNet;
    }

    private Integer parseReadable(Document document) {
        return Integer.parseInt(document.getRootElement().getAttribute("maxReadable").getValue());
    }

    public static JNet openWithXML() {
        return new JNet(1);
    }

    public void addInterceptorRegistry(InterceptorRegistry registry) {
        this.registry = registry;
    }

    public Route get(String uri) {
        Route route = new Route();
        route.setMethod("GET");
        this.routers.put(uri, route);
        return route;
    }

    public Route post(String uri) {
        Route route = new Route();
        route.setMethod("POST");
        this.routers.put(uri, route);
        return route;
    }

    public void listen(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Http Server Start on " + port);
        while (true) {
            int count = selector.select();
            if (count > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        if (socketChannel != null) {
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(this.maxReadable);
                        channel.read(buffer);
                        String request = new String(buffer.array());
                        buffer.clear();
                        switch (getMethod(request)) {
                            case "GET":
                                boolean getResult = true;
                                HttpRequest getRequest = processGetRequest(request);
                                if (this.registry != null) {
                                    getResult = registry.doInterceptor(getRequest.uri(), getRequest);
                                }
                                if (getResult) {
                                    Route getRoute = routers.get(getRequest.uri());
                                    if (getRoute == null) {
                                        HttpThreadPool.handleResponse(channel, "404");
                                    } else {
                                        if (!"GET".equals(getRoute.getMethod())) {
                                            HttpThreadPool.handleResponse(channel, "403");
                                        } else {
                                            String responseBody = getRoute.getListener().handler(getRequest);
                                            HttpThreadPool.handleResponse(channel, responseBody);
                                        }
                                    }
                                } else {
                                    HttpThreadPool.handleResponse(channel, "");
                                }
                                break;
                            case "POST":
                                boolean postResult = true;
                                HttpRequest postRequest = processPostRequest(request);
                                if (this.registry != null) {
                                    postResult = registry.doInterceptor(postRequest.uri(), postRequest);
                                }
                                if (postResult) {
                                    Route postRoute = routers.get(postRequest.uri());
                                    if (postRoute == null) {
                                        HttpThreadPool.handleResponse(channel, "404");
                                    } else {
                                        if (!"POST".equals(postRoute.getMethod())) {
                                            HttpThreadPool.handleResponse(channel, "403");
                                        } else {
                                            String responseBody = postRoute.getListener().handler(postRequest);
                                            HttpThreadPool.handleResponse(channel, responseBody);
                                        }
                                    }
                                } else {
                                    HttpThreadPool.handleResponse(channel, "");
                                }
                                break;
                        }
                    }
                }
            }
        }
    }


    private HttpRequest processGetRequest(String read) {
        HttpRequest.HttpRequestBuilder builder = HttpRequest.builder().method(getMethod(read));
        builder.parameters(getParameters(read));
        builder.requestUri(getUri(read));
        builder.headers(getHeaders(read));
        return builder.build();
    }

    private HttpRequest processPostRequest(String read) {
        HttpRequest.HttpRequestBuilder builder = HttpRequest.builder().method(getMethod(read));
        builder.parameters(postParameters(read));
        builder.requestUri(getUri(read));
        builder.headers(getHeaders(read));
        return builder.build();
    }

    private String getMethod(String read) {
        return read.split("\n")[0].split(" ")[0];
    }

    private String getUri(String read) {
        return read.split("\n")[0].split(" ")[1].split("\\?")[0];
    }

    private Map<String, String> getParameters(String read) {
        Map<String, String> paramMap = new HashMap<>();
        try {
            String[] parameters = read.split("\n")[0].split(" ")[1].split("\\?")[1].split("&");
            for (String param : parameters) {
                String[] paramKeyValue = param.split("=");
                paramMap.put(paramKeyValue[0], paramKeyValue[1]);
            }
            return paramMap;
        } catch (ArrayIndexOutOfBoundsException e) {
            return paramMap;
        }
    }

    private Map<String, String> postParameters(String read) {
        Map<String, String> paramMap = new HashMap<>();
        try {
            String[] requestInfo = read.split("\n");
            String parameters = requestInfo[requestInfo.length - 1];
            String[] paramKeyValue = parameters.split("&");
            for (String param : paramKeyValue) {
                String[] paramKV = param.split("=");
                paramMap.put(paramKV[0], paramKV[1]);
            }
            return paramMap;
        } catch (ArrayIndexOutOfBoundsException e) {
            return paramMap;
        }
    }

    private Map<String, String> getHeaders(String read) {
        Map<String, String> headers = new HashMap<>();
        String[] keyValue = read.split("\n");
        for (String kv : keyValue) {
            if (kv.contains(": ")) {
                String[] headerKV = kv.split(": ");
                headers.put(headerKV[0], headerKV[1]);
            }
        }
        return headers;
    }
}
