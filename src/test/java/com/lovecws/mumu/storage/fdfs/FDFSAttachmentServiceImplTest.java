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

        String url=fdfsAttachmentService.upload(new File("D:\\徐细慧\\xiaomoSite\\images\\4.png"));
        System.out.print(url);
        applicationContext.stop();
    }
}
