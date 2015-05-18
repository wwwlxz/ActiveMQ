package com.lxz.activemq.util;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActivemqUtil {
	private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;
    private boolean IsSend;
    private String strQueueName;

    public String getInfo() {
        return "str of connection: "+String.valueOf(connection)+"\n"+
               "str of session: "+String.valueOf(session)+"\n"+
               "str of IsSend: "+String.valueOf(IsSend)+"\n"+
               "str of producer: "+String.valueOf(producer)+"\n"+
               "str of consumer: "+String.valueOf(consumer)+"\n";
    }
    
    protected void createConnection() throws JMSException{
        ActiveMQConnectionFactory connectionFactory=new ActiveMQConnectionFactory("admin","vhuatu_2013","tcp://192.168.11.14:61616");
//        ActiveMQConnectionFactory connectionFactory=new ActiveMQConnectionFactory("admin","vhuatu_2013","tcp://211.151.160.102:61616");
//        ActiveMQConnectionFactory connectionFactory=new ActiveMQConnectionFactory("admin","vhuatu_2013","tcp://192.168.11.185:61616");
//        ActiveMQConnectionFactory connectionFactory=new ActiveMQConnectionFactory("admin","vhuatu_2013","tcp://localhost:61616");
        connection= connectionFactory.createConnection();
        connection.start();   
    }
    
    protected void createSession() throws JMSException{
        session=connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
    }
    
    protected void createProducer(String QueueName) throws JMSException{
        Queue destination = session.createQueue(QueueName);   
        producer = session.createProducer(destination);   
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);   
    }
    
    protected void createConsumer(String QueueName) throws JMSException{
        Queue destination =session.createQueue(QueueName);
        consumer=session.createConsumer(destination);
    }
    
    public void Create(String QueueName,boolean Flag) throws JMSException{
        IsSend=Flag;
        strQueueName=QueueName;
        createConnection();
        createSession();
        if(IsSend)
        {
            createProducer(QueueName);
        }
        else
        {
            createConsumer(QueueName);
        }
    }
    
    public void CreateAgain() throws JMSException{
        CloseSession();
        createSession();
        if(IsSend)
        {
            createProducer(strQueueName);
        }
        else
        {
            createConsumer(strQueueName);
        }
    }
    
    public void SendMessage(String Message) throws JMSException{
        TextMessage message=session.createTextMessage(Message);
        producer.send(message);
        session.commit();
    }
    
    public String ReceiveMessage(long timeout) throws JMSException{
        String result=null;
        Message message=null;
        TextMessage tmessage = null;
        try {
            message = consumer.receive(timeout);
            tmessage = (TextMessage) message;
            session.commit();
            result = tmessage.getText();
        } catch (JMSException ex) {
            throw ex;
        } finally {
            return result;
        }
    }
    
    public void Close(){
        try{
            if(IsSend){
                if(producer!=null)producer.close();
            }else{
                if(consumer!=null)consumer.close();
            }
            if(session!=null)session.close();
            if(connection!=null)connection.close(); 
        }catch (JMSException ex) {
//            Logger.getLogger(TestTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    public void CloseSession(){
        try{
            if(IsSend){
               if(producer!=null)producer.close();
            }else{
                if(consumer!=null)consumer.close();
            }
            if(session!=null)session.close();
        }catch (JMSException ex) {
//            Logger.getLogger(TestTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
