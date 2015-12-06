package com.sample.jms.domain.service;

import org.apache.activemq.BlobMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.sample.jms.domain.model.Todo;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@Transactional("jmsTransactionManager")
public class TodoService {


    @JmsListener(destination = "TestQueue2")
    @SendTo("ReplyTestQueue2")
    public Todo receive1(Todo todo, @Headers MessageHeaders headers) throws InterruptedException {
    	System.out.println("Received TodoID : " + todo.getTodoId());
    	System.out.println("Received headers : " + headers);
      
    	todo.setTodoId("2");
    	todo.setFinished(true);
    	
        return todo;

    }

    
    @JmsListener(destination = "TestQueue3")
    //@SendTo("ReplyTestQueue3")
    public void receive2(@Validated Todo todo, @Headers MessageHeaders headers) throws InterruptedException {

    	System.out.println("Received TodoID : " + todo.getTodoId());
    	System.out.println("Received headers : " + headers);              

    }


    @JmsListener(destination = "ReplyTestQueue3")
    public void receive3(Todo todo, @Headers MessageHeaders headers) throws InterruptedException {

    	System.out.println("ReplyTestQueue3を受信しました。");
    	System.out.println("Received TodoID : " + todo.getTodoId());
    	System.out.println("Received headers : " + headers);

    }
    
    @JmsListener(containerFactory="myContainerFactory", destination = "TestQueue4")
    public void receive4_1(Todo todo, @Headers MessageHeaders headers) throws InterruptedException {
    	System.out.println("Topic4_1を受信しました。");
    	System.out.println("Received TodoID : " + todo.getTodoId());
    	System.out.println("Received headers : " + headers);
      
    	todo.setTodoId("4_1");
    	todo.setFinished(true);
    	
        //return todo;

    }

    @JmsListener(containerFactory="myContainerFactory", destination = "TestQueue4")
    public void receive4_2(Todo todo, @Headers MessageHeaders headers) throws InterruptedException {
    	System.out.println("Topic4_2を受信しました。");
    	System.out.println("Received TodoID : " + todo.getTodoId());
    	System.out.println("Received headers : " + headers);
      
    	todo.setTodoId("4_2");
    	todo.setFinished(true);
    	
        //return todo;

    }

    @JmsListener(destination = "TestQueue5")
    public void receive5(javax.jms.TextMessage message) throws InterruptedException {
    	System.out.println("TestQueue5を受信しました。");
    	System.out.println("Received message : " + message);
    	
    }
    
    @JmsListener(destination = "TestQueue6")
    public void receive6(BlobMessage message) throws InterruptedException {
    	System.out.println("TestQueue6を受信しました。");
    	//System.out.println("Received message : " + message.get);
    	
    }
    
    @JmsListener(destination = "TestQueue7")
    public Todo receive7(Todo todo, @Headers MessageHeaders headers) throws InterruptedException {
    	System.out.println("TestQueue7を受信しました。");
    	System.out.println("Received TodoID : " + todo.getTodoId());
    	System.out.println("Received headers : " + headers);
      
    	int i = 1/0;
    	
    	todo.setTodoId("7");
    	todo.setFinished(true);
    	
        return todo;

    }
    
    @JmsListener(destination = "TestQueue8")
    public void receive8(Todo todo, @Headers MessageHeaders headers) throws InterruptedException, IOException {

    	String id = todo.getTodoId();
    	List<String> list = new ArrayList<String>();
    	list.add(id);
    	Path path = FileSystems.getDefault().getPath("D:\\work\\" + id +".dat");
    	JmsFileUtils.writeListToFile(path, list);
    	
    	System.out.println("Received TodoID : " + todo.getTodoId());
    	System.out.println("Received headers : " + headers);              

    }

    
}