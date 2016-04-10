package com.moblong.geography;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.Constants;
import com.moblong.flipped.model.Whistle;
import com.moblong.geography.dto.GeographyAssister;
import com.moblong.iwe.IWhistleServlet;
import com.moblong.iwe.ImmediatelyWhistleEngine;

public final class Launcher {

	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		launcher.lanch();
	}

	public final static int CONCURRENT_MAX_NUMBER = 100000;
	
	private Integer concurrentCounter = 0;
	
	public void lanch() {
		final Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
		final ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		final ImmediatelyWhistleEngine iwe = new ImmediatelyWhistleEngine();
		final Map<Long, IWhistleServlet> servlets = new TreeMap<Long, IWhistleServlet>();
		try {
			//添加注册消息处理器
			servlets.put(Constants.ACTION_REGISTER, new IWhistleServlet() {

				@Override
				public void recived(Whistle req, Queue<Whistle> resp) {
					String            aid = req.getInitiator();
					List<Double> position = gson.fromJson(req.getContent(), new TypeToken<List<Double>>(){}.getType());
					GeographyAssister assister = context.getBean("GeographyAssister", GeographyAssister.class);
					assister.register(context, aid, position.get(0), position.get(1));
					
					List<Account> neighbors = assister.nearby(context, aid, position.get(0), position.get(1), 1000);
					Whistle whistle = new Whistle();
					whistle.setInitiator(Constants.LCN);
					whistle.setRecipient(aid);
					whistle.setAction(Constants.ACTION_REQUEST_CONTACTS);
					whistle.setBroadcast(false);
					whistle.setConsumed(Constants.WHISTLECONTROLLER_UNCONSUMED);
					whistle.setContent(gson.toJson(neighbors));
					resp.add(whistle);
				}
				
			});
			
			//提交坐标
			servlets.put(Constants.ACTION_SUBMIT_LOCATION, new IWhistleServlet() {

				@Override
				public void recived(Whistle req, Queue<Whistle> resp) {
					String        aid = req.getInitiator();
					List<Double> position = gson.fromJson(req.getContent(), new TypeToken<List<Double>>(){}.getType());
					GeographyAssister assister = context.getBean("GeographyAssister", GeographyAssister.class);
					assister.update(context, aid, position.get(0), position.get(1));
					
					List<Account> neighbors = assister.nearby(context, aid, position.get(0), position.get(1), 1000);
					Whistle whistle = new Whistle();
					whistle.setInitiator(Constants.LCN);
					whistle.setRecipient(aid);
					whistle.setAction(Constants.ACTION_REQUEST_CONTACTS);
					whistle.setBroadcast(false);
					whistle.setConsumed(Constants.WHISTLECONTROLLER_UNCONSUMED);
					whistle.setContent(gson.toJson(neighbors));
					resp.add(whistle);
				}
				
			});
			
			iwe.init(Constants.LCN, "push.tlthsc.com", 5672);
			iwe.startup(new IWhistleServlet() {

				@Override
				public void recived(Whistle req, Queue<Whistle> resp) {
					Thread sync = new Thread(new Runnable() {

						@Override
						public void run() {
							while (concurrentCounter >= CONCURRENT_MAX_NUMBER) {
								Thread.yield();
							}
							synchronized (concurrentCounter) {
								++concurrentCounter;
							}
							IWhistleServlet servlet = servlets.get(req.getAction());
							servlet.recived(req, resp);
							synchronized (concurrentCounter) {
								--concurrentCounter;
							}
						}
					});
					sync.start();
				}
			});
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}
	
}
