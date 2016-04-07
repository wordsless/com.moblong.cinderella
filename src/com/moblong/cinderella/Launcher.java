package com.moblong.cinderella;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moblong.amuse.dto.GeographyAssister;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.Constants;
import com.moblong.flipped.model.Whistle;
import com.moblong.iwe.IRecivedListener;
import com.moblong.iwe.ImmediatelyWhistleEngine;

public final class Launcher {

	public static void main(String[] args) {
		final ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		final ImmediatelyWhistleEngine iwe = new ImmediatelyWhistleEngine();
		try {
			iwe.init(Constants.LCN, "push.tlthsc.com", 5672);
			iwe.startup(new IRecivedListener<Whistle>() {

				@Override
				public boolean recived(Whistle whistle) {
					if(whistle.getAction().equals(Constants.ACTION_SUBMIT_LOCATION)) {
						Thread update = new Thread(new Runnable() {

							@Override
							public void run() {
								Gson gson = new Gson();
								final String aid = whistle.getInitiator();
								final double[] position = gson.fromJson((String) whistle.getContent(), new TypeToken<double[]>(){}.getType());
								GeographyAssister assister = context.getBean("GeographyAssister", GeographyAssister.class);
								assister.update(context, aid, position[0], position[1]);
								
								List<Account> neighbors = assister.nearby(context, aid, position[0], position[1], 1000);
								Whistle resp = new Whistle();
								resp.setInitiator(Constants.LCN);
								resp.setRecipient(aid);
								resp.setAction(Constants.ACTION_NEIGHBORHOOD);
								resp.setBroadcast(false);
								resp.setConsumed(Constants.WHISTLECONTROLLER_UNCONSUMED);
								resp.setContent(neighbors);
								iwe.send(resp);
							}
						});
						update.start();
					}
					return true;
				}
				
			});
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}

}
