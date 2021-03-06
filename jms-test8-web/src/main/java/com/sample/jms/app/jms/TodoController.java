package com.sample.jms.app.jms;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sample.jms.domain.model.Todo;
import com.sample.jms.domain.service.JmsFileUtils;
import com.sample.jms.domain.service.TodoService;



@Controller
@RequestMapping("todo")
//@Transactional("jmsTransactionManager")
public class TodoController {
    @Inject // (1)
    TodoService todoService;

    @Inject
    private JmsTemplate jmsQueueTemplate;
    
    @Inject
    private JmsTemplate jmsQueuePurgeTemplate;
    
    @Inject
    private JmsTemplate jmsTopicTemplate;
    
    @Inject
    private JmsTemplate jmsQueueNonTranTemplate;
    
    @Inject
    private TransactionTemplate transactionTemplateRequired;
    
    @ModelAttribute // (2)
    public TodoForm setUpForm() {
        TodoForm form = new TodoForm();
        return form;
    }
    

    // 同期受信パターン（初期表示）
    @RequestMapping(value = "init")
    public String init(Model model) {
    	Todo todo = new Todo();
    	model.addAttribute("todo", todo);
    	model.addAttribute("link","jms1_1");
    	
    	// こういう感じで試験の最初と最後にパージさせるか？
    	// そうでもしないと残ってしまう。
    	while (true) {
    		Object obj = jmsQueuePurgeTemplate.receive("TestQueue1");
    		if (obj == null) {
    			break;
    		} else {
    			System.out.println("パージしました");
    		}
    	}
    	
    	
    	
        return "todo/jmsSend";
    }

    
   
    // 同期受信パターン（初期表示）
    @RequestMapping(value = "jms1_1")
    public String initJms1_1(Model model) {
    	Todo todo = new Todo();
    	model.addAttribute("todo", todo);
    	model.addAttribute("link","jms1_2");
        return "todo/jmsSend";
    }

    // 同期受信パターン（受信）    
    @RequestMapping(value = "jms1_2")
    public String initJms1_2(Model model) throws InterruptedException {
        
    	Todo todo = new Todo();
    	todo.setTodoId("11111");
    	// 別スレッドにしないと受け取れない。なんで？（同じスレッドだと受け取れない）
    	SubThread sub = new SubThread();
        sub.start();

    	TimeUnit.SECONDS.sleep(1L);
        
    	//jmsQueueTemplate.convertAndSend("TestQueue1", todo);
    	
    	Todo retTodo =(Todo)jmsQueueTemplate.receiveAndConvert("TestQueue1");
    	
    	System.out.println("TestQueue1から受信しました。" + retTodo.getTodoId());
    	
    	
//    	Todo retTodo = todo;
    	retTodo.setFinished(true);
    	
    	model.addAttribute("todo", retTodo);
    	
        return "todo/jmsRecieve";
    }
    
    
    class SubThread extends Thread{
    	  public void run(){
    	    // サブ側で実行するプログラム処理を記述
    		  Todo todo = new Todo();
    	    	todo.setTodoId("111-111");
    		  jmsQueueTemplate.convertAndSend("TestQueue1", todo);
    	  }
    	}
    
    
    // 非同期受信パターン（初期表示）
    @RequestMapping(value = "jms2_1")
    public String initJms2_1(Model model) {
        
    	Todo todo = new Todo();
    	
    	model.addAttribute("todo", todo);
    	model.addAttribute("link","jms2_2");
    	
        return "todo/jmsSend";
    }

    // 非同期受信パターン（受信）
    @RequestMapping(value = "jms2_2")
    public String initJms2_2(Model model) throws InterruptedException {
        
    	// ランダム値を作成
    	UUID id = UUID.randomUUID();
    	
    	Todo todo = new Todo();
    	todo.setTodoId(id.toString());
    	
    	
    	    	
    	// 送信
    	jmsQueueTemplate.convertAndSend("TestQueue2", todo);
  
    	
    	Todo retTodo =(Todo)jmsQueueTemplate.receiveAndConvert("ReplyTestQueue2");
    	retTodo.setFinished(true);
    	model.addAttribute("todo", retTodo);
    	
        return "todo/jmsRecieve";
    }
    
    // 非同期受信かつSendToレスポンスパターン（初期表示）    
    @RequestMapping(value = "jms3_1")
    public String initJms3_1(Model model) {
        
    	Todo todo = new Todo();
    	
    	model.addAttribute("todo", todo);
    	model.addAttribute("link","jms3_2");
        return "todo/jmsSend";
    }

    
    // 非同期受信かつSendToレスポンスパターン（受信）
    @RequestMapping(value = "jms3_2")
    public String initJms3_2(Model model) throws InterruptedException {
        
    	Todo todo = new Todo();
    	todo.setTodoId("33333");
    	    	
    	jmsQueueTemplate.convertAndSend("TestQueue3", todo);
    	
    	//TimeUnit.SECONDS.sleep(5L);
    	
    	//String s =(String)jmsTemplate.receiveAndConvert("ReplyTestQueue3");
    	Todo retTodo = new Todo();
    	retTodo.setFinished(true);
    	//retTodo.setDescription(s);
    	model.addAttribute("todo", todo);
    	
        return "todo/jmsRecieve";
    }
    
    // トピック非同期受信パターン（初期表示）
    @RequestMapping(value = "jms4_1")
    public String initJms4_1(Model model) {
        
    	Todo todo = new Todo();
    	
    	model.addAttribute("todo", todo);
    	model.addAttribute("link","jms4_2");
    	
        return "todo/jmsSend";
    }

    // トピック非同期受信パターン（受信）
    @RequestMapping(value = "jms4_2")
    public String initJms4_2(Model model) throws InterruptedException {
        
    	Todo todo = new Todo();
    	todo.setTodoId("44444");
    	    	
    	jmsTopicTemplate.convertAndSend("TestQueue4", todo);
    	
    	TimeUnit.SECONDS.sleep(3L);
    	    	
        return "todo/jmsRecieve";
    }
    
    
    // ProducerCallback非同期受信パターン（初期表示）
    @RequestMapping(value = "jms5_1")
    public String initJms5_1(Model model) {
        
    	Todo todo = new Todo();
    	
    	model.addAttribute("todo", todo);
    	model.addAttribute("link","jms5_2");
    	
        return "todo/jmsSend";
    }

    // ProducerCallback非同期受信パターン（受信）
    @RequestMapping(value = "jms5_2")
    public String initJms5_2(Model model) throws InterruptedException {
        
    	Todo todo = new Todo();
    	todo.setTodoId("55555");
    	
    	// TransactionTemplateを使用
    	transactionTemplateRequired.execute(new TransactionCallbackWithoutResult() {
    		
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				// TODO Auto-generated method stub
				
	        	jmsQueueNonTranTemplate.execute("TestQueue5", new ProducerCallback<Object>() {
	                @Override
	                public Object doInJms(Session session, MessageProducer producer)
	                                throws JMSException {
	                        for (int i = 0; i < 10; i++) {
	                                TextMessage msg = session.createTextMessage("Message" + i);
	                                
	                                producer.send(msg);     
	                        }
	                        return null;
	                }
	        	  });

				
			}

    	});
    	TimeUnit.SECONDS.sleep(3L);
    	
    	Todo retTodo = todo;
    	retTodo.setFinished(true);
    	model.addAttribute("todo", retTodo);
    	
        return "todo/jmsRecieve";
    	
    }
    
    
    // 大きなメッセージ非同期受信パターン（初期表示）
    @RequestMapping(value = "jms6_1")
    public String initJms6_1(Model model) {
        
    	Todo todo = new Todo();
    	
    	model.addAttribute("todo", todo);
    	model.addAttribute("link","jms6_2");
    	
        return "todo/jmsSend";
    }

    // 大きなメッセージ非同期受信パターン（受信）
    @RequestMapping(value = "jms6_2")
    public String initJms6_2(Model model) throws InterruptedException {
        
    	Todo todo = new Todo();
    	todo.setTodoId("66666");
    	
    	jmsQueueTemplate.execute("TestQueue6", new ProducerCallback<Object>() {
            @Override
            public Object doInJms(Session session, MessageProducer producer)
                            throws JMSException {
            	
            	ActiveMQSession session2 = (ActiveMQSession)session;
            	
            	File file = new File("D:\\apache-activemq-5.13-20151023.032547-52-bin.zip");
                BlobMessage msg = session2.createBlobMessage(file);
                producer.send(msg);     
                return null;
            }
    	  });
    	
    	Todo retTodo = todo;
    	retTodo.setFinished(true);
    	model.addAttribute("todo", retTodo);
    	
        return "todo/jmsRecieve";
    }
    
    
    // 非同期受信時ロールバックパターン（初期表示）
    @RequestMapping(value = "jms7_1")
    public String initJms7_1(Model model) {
        
    	Todo todo = new Todo();
    	
    	model.addAttribute("todo", todo);
    	model.addAttribute("link","jms7_2");
    	
        return "todo/jmsSend";
    }

    // 非同期受信時ロールバックパターン（受信）
    @RequestMapping(value = "jms7_2")
    public String initJms7_2(Model model) throws InterruptedException {
        
    	Todo todo = new Todo();
    	todo.setTodoId("77777");
    	    	
    	jmsQueueTemplate.convertAndSend("TestQueue7", todo);
    	
    	TimeUnit.SECONDS.sleep(3L);
    	
    	Todo retTodo = todo;
    	retTodo.setFinished(true);
    	model.addAttribute("todo", retTodo);
    	
        return "todo/jmsRecieve";
    }
    
    // 非同期受信パターン（初期表示）
    @RequestMapping(value = "jms8_1")
    public String initJms8_1(Model model) {
        
    	Todo todo = new Todo();
    	
    	model.addAttribute("todo", todo);
    	model.addAttribute("link","jms8_2");
    	
        return "todo/jmsSend";
    }

    // 非同期受信パターン（受信）
    @RequestMapping(value = "jms8_2")
    public String initJms8_2(Model model) throws InterruptedException, IOException {
        
    	// ランダム値を作成
    	UUID id = UUID.randomUUID();
    	
    	Todo todo = new Todo();
    	todo.setTodoId(id.toString());
    	
    	
    	    	
    	// 送信
    	jmsQueueTemplate.convertAndSend("TestQueue8", todo);
    	
    	// 1秒待つ
    	System.out.println("1秒俟ちます");
    	TimeUnit.SECONDS.sleep(1L);
    	
    	String filePath = "D:\\work\\" + id +".dat";
    	
    	Path path = FileSystems.getDefault().getPath(filePath);
    	List<String> result = null;
    	
    	for (int i=0; i<5; i++) {
    		
    		if (JmsFileUtils.existsFile(filePath) == true) {
    			result = JmsFileUtils.readFileToList(path);
    		}
    		
    		TimeUnit.SECONDS.sleep(1L);
    		
    	}
    	
    	System.out.println("結果出します。" +  result);
    	
    	
    	
  
    	
    	Todo retTodo = todo;
    	retTodo.setFinished(true);
    	model.addAttribute("todo", retTodo);
    	
        return "todo/jmsRecieve";
    }
    
    
}

