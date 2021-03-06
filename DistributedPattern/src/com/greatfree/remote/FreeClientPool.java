package com.greatfree.remote;

import java.io.IOException;
import java.util.Set;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.reuse.RetrievablePool;
import com.greatfree.util.UtilConfig;

/*
 * The pool, RetrievablePool, is mainly used for the resource of FreeClient. Some problems exist when instances of FreeClient are exposed outside since they might be disposed inside in the pool.
 * It is a better solution to wrap the instances of FreeClient and the management on them. The stuffs should be invisible to outside. For that, a new pool, FreeClientPool, is proposed. 11/19/2014, Bing Li
 */

public class FreeClientPool
{
	// Declare an instance of Retrievable to hide instances of FreeClient from outside. 11/19/2014, Bing Li
	private RetrievablePool<IPPort, FreeClient, FreeClientCreator, FreeClientDisposer> pool;

	/*
	 * Initialize the pool. 11/19/2014, Bing Li
	 */
	public FreeClientPool(int poolSize)
	{
		this.pool = new RetrievablePool<IPPort, FreeClient, FreeClientCreator, FreeClientDisposer>(poolSize, new FreeClientCreator(), new FreeClientDisposer());
	}

	/*
	 * Dispose the resource, i.e., the pool in the case. 11/20/2014, Bing Li
	 */
	public void dispose() throws IOException
	{
		this.pool.shutdown();
	}

	/*
	 * Set the idle checking. 11/20/2014, Bing Li
	 */
	public void setIdleChecker(long delay, long period, long maxIdleTime)
	{
		this.pool.setIdleChecker(delay, period, maxIdleTime);
	}

	/*
	 * Send a message to an IP/port which is enclosed in the instance of IPPort. The method wraps three steps as below. Thus, those stuffs are invisible to users such that it avoids possible conflicts to share instances of FreeClient. 11/20/2014, Bing Li
	 * 
	 * 		Getting an instance of FreeClient
	 * 		
	 * 		Sending the message
	 * 		
	 * 		Collecting the instance of FreeClient.
	 */
	public void send(IPPort ipPort, ServerMessage msg) throws IOException
	{
		// Get an instance of FreeClient by the instance of IPPort. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(ipPort);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message. 11/20/2014, Bing Li
			client.send(msg);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
		}
	}

	/*
	 * Send a message to an IP/port. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public void send(String ip, int port, ServerMessage msg) throws IOException
	{
		// Get an instance of FreeClient by the IP/port. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(new IPPort(ip, port));
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message. 11/20/2014, Bing Li
			client.send(msg);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
		}
	}

	/*
	 * Send a message to a remote node by its key. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public void send(String clientKey, ServerMessage msg) throws IOException
	{
		// Get an instance of FreeClient by the client key. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(clientKey);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message. 11/20/2014, Bing Li
			client.send(msg);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
		}
	}

	/*
	 * Send a request message to a remote node in the form of IPPort and then wait until its corresponding response is received. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public ServerMessage request(IPPort ipPort, ServerMessage req) throws IOException, ClassNotFoundException
	{
		// Get an instance of FreeClient by the instance of IPPort. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(ipPort);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}

	/*
	 * Send a request message to a remote node in the form of IP/port and then wait until its corresponding response is received. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public ServerMessage request(String ip, int port, ServerMessage req) throws IOException, ClassNotFoundException
	{
		// Get an instance of FreeClient by the instance of IPPort. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(new IPPort(ip, port));
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}
	
	/*
	 * Send a request message to a remote node by its client key and then wait until its corresponding response is received. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public ServerMessage request(String clientKey, ServerMessage req) throws IOException, ClassNotFoundException
	{
		// Get an instance of FreeClient by the client key. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(clientKey);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}

	/*
	 * Check whether the client is existed. 11/20/2014, Bing Li
	 */
	public boolean isClientExisted(String ip, int port)
	{
		// Check whether the source to create the instance of FreeClient is existed. 11/20/2014, Bing Li
		return this.pool.isSourceExisted(new IPPort(ip, port));
	}

	/*
	 * Get the IP of a client key. 11/20/2014, Bing Li
	 */
	public String getIP(String clientKey)
	{
		return this.pool.getSource(clientKey).getIP();
	}

	/*
	 * Get the client count in the pool. 11/20/2014, Bing Li
	 */
	public int getClientSize()
	{
		return this.pool.getSourceSize();
	}

	/*
	 * Get the instance of IPPort by the client key. 11/20/2014, Bing Li
	 */
	public IPPort getIPPort(String clientKey)
	{
		return this.pool.getSource(clientKey);
	}

	/*
	 * Get the client keys in the pool. 11/20/2014, Bing Li
	 */
	public Set<String> getNodeKeys()
	{
		return this.pool.getAllObjectKeys();
	}

	/*
	 * Get the count of the clients which are working. 11/20/2014, Bing Li
	 */
	public int getBusyClientCount()
	{
		return this.pool.getBusyResourceSize();
	}
	
	/*
	 * Get the count of the clients which are idle. 11/20/2014, Bing Li
	 */
	public int getIdleClientCount()
	{
		return this.pool.getIdleResourceSize();
	}

	/*
	 * Remote a client by its client key. 11/20/2014, Bing Li
	 */
	public void removeClient(String clientKey) throws IOException
	{
		this.pool.removeResource(clientKey);
	}
}
