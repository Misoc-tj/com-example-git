package com.example.spring.layui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@RestController
public class AsynServletController {

    @RequestMapping("servletRed.do")
    public void servletReq(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //1.获取异步的上下文
        AsyncContext asyncContext = request.startAsync();
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) throws IOException {
                System.out.println("完成是触发...");
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                System.out.println("超时是触发...");
            }

            @Override
            public void onError(AsyncEvent asyncEvent) throws IOException {
                System.out.println("错误时触发...");
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                System.out.println("异步开始时触发...");
            }
        });
        asyncContext.setTimeout(2000);
        asyncContext.start(() -> {
            try {
                TimeUnit.SECONDS.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //这个一定要触发完成
                asyncContext.complete();
            }
        });

    }


    @RequestMapping("callableReq.do")
    public Callable<String> callableReq() throws Exception {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "callable....";
            }
        };
    }

    @Configuration
    class RequestAsyncPoolConfig extends WebMvcConfigurerAdapter {

        @Resource
        private ThreadPoolTaskExecutor threadPoolTaskExecutor;

        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            configurer.setDefaultTimeout(1000);
            configurer.setTaskExecutor(threadPoolTaskExecutor);
            configurer.registerCallableInterceptors(timeoutCallableProcessingInterceptor());
        }

        @Bean
        public TimeoutCallableProcessingInterceptor timeoutCallableProcessingInterceptor() {
            return new TimeoutCallableProcessingInterceptor();
        }
    }

    @RequestMapping("webAsyncReq.do")
    public WebAsyncTask<String> webAsyncReq() throws Exception {
        Callable<String> result = () -> {
            return "success";
        };
        WebAsyncTask<String> wat = new WebAsyncTask<>(1000L, result);
        wat.onTimeout(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "超时";
            }
        });
        return wat;
    }

    @RequestMapping("webAsyncReq.do")
    public DeferredResult<String> deferredResultReq() {
        DeferredResult<String> result = new DeferredResult<>(60 ^ 1000L);
        result.onTimeout(new Runnable() {
            @Override
            public void run() {
                System.out.println("超时了");
            }
        });
        result.onCompletion(new Runnable() {
            @Override
            public void run() {
                System.out.println("完成了");
            }
        });

        new Thread(() -> {
            result.setResult("DeferredResult!!!");
        });

        return result;
    }

}
