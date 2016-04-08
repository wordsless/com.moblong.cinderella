package com.moblong.iwe;

import java.util.Queue;

import org.springframework.context.ApplicationContext;

import com.moblong.flipped.model.Whistle;

public interface IWhistleServlet {

	public void recived(final Whistle req, final Queue<Whistle> resp);
	
}
