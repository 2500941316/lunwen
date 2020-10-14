package com.shu.hbase.controller;


import org.springframework.util.Base64Utils;

import java.util.Base64;

public class Test {
    public static void main(String[] args) {

        String url1 = "/shuwebfs/19721631/关于开展疫情1-防控工作的通知.doc";

        String url2 = "group1/M00/00/00/CgoAXF-GmoWADq0QAA8z6G67Y1U43.pptx";

        String url = new String(Base64.getEncoder().encode(url1.getBytes()));
        String urlq = new String(Base64.getEncoder().encode(url2.getBytes()));
        System.out.println(url);
        System.out.println(urlq);
        byte[] decode = Base64.getDecoder().decode(url);
        System.out.println(new String(decode));
        byte[] decode2 = Base64.getDecoder().decode(urlq);
        System.out.println(new String(decode2));
    }
}
