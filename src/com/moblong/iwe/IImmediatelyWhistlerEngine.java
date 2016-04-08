package com.moblong.iwe;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.moblong.flipped.model.Whistle;

public interface IImmediatelyWhistlerEngine {

	public void send(Whistle whistle);

	public void init(String id, String host, int port) throws IOException, TimeoutException;

	public void startup(IWhistleServlet observer);

	public void shutdown();

	public boolean isAlive();

}
