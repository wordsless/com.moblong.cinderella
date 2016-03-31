package com.moblong.iwe;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.moblong.amuse.dto.GeographyAssister;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.Constants;
import com.moblong.flipped.model.Whistle;

public final class Cinderella implements IRecivedListener<Whistle<?>> {
	
	private IRecivedListener<Whistle<?>> reciver;
	
	public void init(final ApplicationContext context, final IImmediatelyWhistlerEngine iwe) {
		
		final Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
		
		reciver = new IRecivedListener<Whistle<?>>() {

			@Override
			public boolean recived(Whistle<?> whistle) {
				if(whistle.getAction().equals(Constants.ACTION_SUBMIT_LOCATION)) {
					GeographyAssister geographyDTO = context.getBean("GeographyDTO", GeographyAssister.class);
					double[] position = gson.fromJson((String) whistle.getContent(), new TypeToken<double[]>(){}.getType());
					List<Account> nearby = geographyDTO.nearby(context, whistle.getSource(), position[0], position[1], 1000);
					Whistle<List<Account>> newWhistle = new Whistle<List<Account>>();
					newWhistle.setAction(Constants.ACTION_NEIGHBORHOOD);
					newWhistle.setSource(Constants.LCN);
					newWhistle.setTarget(whistle.getSource());
					newWhistle.setContent(nearby);
					iwe.send(newWhistle);
				}
				return false;
			}			
		};
	}
	
	@Override
	public boolean recived(Whistle<?> whistle) {
		return reciver.recived(whistle);
	}

}
