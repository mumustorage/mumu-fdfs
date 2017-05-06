package com.lovecws.mumu.storage.fdfs;

import com.lovecws.mumu.storage.fdfs.service.FDFSAttachmentService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * Created by Administrator on 2017/3/7.
 */
public class FDFSAttachmentServiceImplTest {

    public static void main(String[] args){
        ClassPathXmlApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath:spring-storage-fdfs.xml");
        applicationContext.start();
        FDFSAttachmentService fdfsAttachmentService=applicationContext.getBean(FDFSAttachmentService.class);

        //String url=fdfsAttachmentService.upload(new File("C:\\unintall.log"));
        //String url=fdfsAttachmentService.uploadWithUrl(new File("C:\\header.jpg"));
        //String url=fdfsAttachmentService.uploadWithUrl(new File("C:\\ERWin 7.3.zip"));
        //String url=fdfsAttachmentService.uploadWithUrl(new File("C:\\8月4日应用答疑（上）_知识讲解.avi"));
        String url=fdfsAttachmentService.uploadWithUrl(new File("C:\\4.jdbc_resultset-new.mp4"));
        System.out.print(url);
        applicationContext.stop();
    }
}
