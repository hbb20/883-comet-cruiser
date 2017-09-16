package com.hbb20.mycometcruiser;

/**
 * Created by hbb20 on 9/16/17.
 */

import android.content.Context;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by HBB20 on 7/3/2016.
 */
public class RetrofitHelper {
    private static RetrofitHelper retrofitHelper;
    private static Retrofit retrofit;
    private static RetrofitService retrofitService;
    private static String TAG = "<<RETRO CONTROLLER>> ";

    private RetrofitHelper() {

    }

    /**
     * returns the retrofit service to make http launchEditActivity.
     *
     * @param context
     * @return
     */
    public static RetrofitService getRetrofitService(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(getLocalhostHttpInterceptedClient(context))
                //.client(getInterceptedClient(context))
                .build();

        return retrofit.create(RetrofitService.class);
    }

    private static OkHttpClient getLocalhostHttpInterceptedClient(final Context context) {
        try {

            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .method(original.method(), original.body());

                    builder.addHeader("platform", "Android");
                    builder.addHeader("version-code", BuildConfig.VERSION_CODE + "");
                    Request request = builder.build();
                    return chain.proceed(request);
                }
            });

            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            okHttpClientBuilder.readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);

            return okHttpClientBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static OkHttpClient getInterceptedClient(final Context context) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .method(original.method(), original.body());

                    builder.addHeader("platform", "Android");
                    builder.addHeader("version-code", BuildConfig.VERSION_CODE + "");

                    Request request = builder.build();
                    return chain.proceed(request);
                }
            });

            okHttpClientBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            okHttpClientBuilder.readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);

            //conncetion specs
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .allEnabledTlsVersions()
                    .allEnabledCipherSuites()
                    .build();
            okHttpClientBuilder.connectionSpecs(Collections.singletonList(spec));


            return okHttpClientBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
