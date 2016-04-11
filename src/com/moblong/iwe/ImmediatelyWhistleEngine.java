package com.moblong.iwe;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.flipped.model.Whistle;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public final class ImmediatelyWhistleEngine implements IImmediatelyWhistlerEngine {

	private boolean alive = false;

	private IWhistleServlet observer;
	
	private BlockingQueue<Whistle> queue;

	private IDetegater<IWhistleServlet> starter, closer; 
	
	private Thread reciver, sender;

	@Override
	public void send(final Whistle whistle) {
		try {
			queue.put(whistle);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init(final String id, final String host, final int port) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setAutomaticRecoveryEnabled(false);
		factory.setHost(host);
		factory.setPort(port);
		queue = new ArrayBlockingQueue<Whistle>(128);
		
		final Connection con = factory.newConnection();
		final Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
		
		reciver = new Thread(new Runnable() {

			@Override
			public void run() {
				Channel channel = null;
				try {
					channel = con.createChannel();
					QueueingConsumer consumer = new QueueingConsumer(channel);
					channel.queueDeclare(id, false, false, false, null);
					channel.basicConsume(id, true, consumer);
					Gson gson = new Gson();
					while (alive) {
						Delivery delivery = consumer.nextDelivery();
						byte[] body = delivery.getBody();
						Whistle whistle = new Whistle(body);
						if(observer != null)
							observer.recived(whistle, queue);
						Thread.yield();
					}
					channel.close();
					channel = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
		sender = new Thread(new Runnable() {

			@Override
			public void run() {
				Channel channel = null;
				try {
					channel = con.createChannel();
					while(alive) {
						Whistle whistle = queue.poll();
						if(whistle != null) {
							channel.queueDeclare(whistle.getRecipient(), false, false, false, null);
							channel.basicPublish("", whistle.getRecipient(), MessageProperties.TEXT_PLAIN, whistle.serialize());
						}
						Thread.yield();
					}
					channel.close();
					channel = null;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		starter = new IDetegater<IWhistleServlet>() {

			@Override
			public void detegate(IWhistleServlet observer) {
				
				alive = true;
				
				ImmediatelyWhistleEngine.this.observer = observer;
				
				if(reciver != null && !reciver.isAlive()) {
					reciver.start();
				}
				
				if(sender != null && !sender.isAlive()) {
					sender.start();
				}
			}
			
		};
		
		closer = new IDetegater<IWhistleServlet>() {

			@Override
			public void detegate(IWhistleServlet observer) {
				
				alive = false;
				
				ImmediatelyWhistleEngine.this.observer = null;
				
				while(reciver != null && reciver.isAlive()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				while(sender != null && sender.isAlive()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
			
		};
	}
	
	@Override
	public void startup(IWhistleServlet listener) {
		starter.detegate(listener);
	}
	
	@Override
	public void shutdown() {
		closer.detegate(this.observer);
	}
	
	@Override
	public final boolean isAlive() {
		return alive;
	}
}
