package com.example.io.bio;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>阻塞式Server，一个服务端连接多个客户端，每个连接创建一个Thread
 * 使用windows的Telnet来测试
 *
 * @author 蒋睿立
 * @version 1
 * @date 2020/5/26
 */
public class BIOServer {


    public final static int PORT = 8000;

    public static void main(String[] args) throws Exception {

        //创建一个线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("服务器启动，端口号：" + PORT);

        while (true) {
            //监听客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println("客户端连接");

            //有客户端连接，就创建一个线程进行通讯
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    //开始通讯
                    byte[] bytes = new byte[1024];
                    try (InputStream inputStream = socket.getInputStream()) {
                        //循环读取客户端发送的数据
                        while (true) {
                            int count = inputStream.read(bytes);
                            if (count == -1) {
                                break;
                            }
                            System.out.println(new String(bytes, 0, count));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("关闭与客户端的连接");
                    }

                }
            });
        }
    }
}
