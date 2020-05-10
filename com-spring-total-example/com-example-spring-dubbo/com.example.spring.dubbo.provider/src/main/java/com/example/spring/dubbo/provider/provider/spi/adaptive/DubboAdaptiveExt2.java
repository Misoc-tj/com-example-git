package com.example.spring.dubbo.provider.provider.spi.adaptive;

import com.alibaba.dubbo.common.URL;
import com.example.spring.dubbo.provider.provider.spi.adaptive.AdaptiveExt2;

public class DubboAdaptiveExt2 implements AdaptiveExt2 {

    @Override
    public String echo(String msg, URL url) {
        System.out.println("dubbo");
        return "dubbo";
    }
}
