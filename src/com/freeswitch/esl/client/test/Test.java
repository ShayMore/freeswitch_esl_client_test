package com.freeswitch.esl.client.test;

import java.util.Map.Entry;

import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.freeswitch.esl.client.transport.message.EslHeaders.Name;
import org.freeswitch.esl.client.transport.message.EslMessage;

/**
 * 内联测试
 * 
 * @author pengzhenjin
 *
 */
public class Test {
	// private String host = "localhost";
	private String host = "192.168.0.125";
	private int port = 8021;
	private String password = "ClueCon";

	public static void main(String[] args) {
		try {
			Test test = new Test();
			test.do_multi_connects();
			System.out.println("-----------------------------------------------分割线------------------------------------------------------------");
			test.do_connect();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void do_connect() throws InterruptedException {
		System.out.println("******do_connect******");
		Client client = new Client();

		client.addEventListener(new IEslEventListener() {
			public void eventReceived(EslEvent event) {
				System.out.println("Event received [{}]" + event);
			}

			public void backgroundJobResultReceived(EslEvent event) {
				System.out.println("Background job result received [{}]" + event);
			}

		});
		System.out.println("Client connecting ..");
		try {
			client.connect(host, port, password, 2);
		} catch (InboundConnectionFailure e) {
			System.out.println("Connect failed" + e);
			return;
		}

		System.out.println("Client connected ..");

		// client.setEventSubscriptions( "plain",
		// "heartbeat CHANNEL_CREATE CHANNEL_DESTROY BACKGROUND_JOB" );
		client.setEventSubscriptions("plain", "all");
		client.addEventFilter("Event-Name", "heartbeat");
		client.cancelEventSubscriptions();
		client.setEventSubscriptions("plain", "all");
		client.addEventFilter("Event-Name", "heartbeat");
		client.addEventFilter("Event-Name", "channel_create");
		client.addEventFilter("Event-Name", "background_job");
		// client.sendSyncCommand( "echo", "Foo foo bar" );
		// client.sendSyncCommand( "originate",
		// "sofia/internal/101@192.168.100.201! sofia/internal/102@192.168.100.201!"
		// );

//		String status = client.sendAsyncApiCommand("status", "");
//		System.out.println("status [{}] " + status);
		
		EslMessage status = client.sendSyncApiCommand("status", "");
		System.out.println("status [{}] " + status.getBodyLines());
		
//		String version = client.sendAsyncApiCommand("version", "");
//		System.err.println("version = [{}] " + version);
		
		EslMessage version = client.sendSyncApiCommand("version", "");
		System.out.println("version [{}] " + version.getBodyLines());
		
		EslMessage res = client.sendSyncApiCommand("sofia status", "");
		System.out.println("sofia status [{}] " + res.getBodyLines().get(3));

		EslMessage response = client.sendSyncApiCommand("sofia xmlstatus", "profile internal reg 1002");
		System.out.println("sofia xmlstatus [{}] " + response);

		for (Entry<Name, String> header : response.getHeaders().entrySet()) {
			System.out.println(" * header [{}] " + header);
		}
		for (String bodyLine : response.getBodyLines()) {
			System.out.println(" * body [{}] " + bodyLine);
		}

		Thread.sleep(5000);
		client.close();
	}

	public void do_multi_connects() throws InterruptedException {
		Client client = new Client();

		System.out.println("Client connecting ..");

		try {
			client.connect(host, port, password, 2);
		} catch (InboundConnectionFailure e) {
			System.out.println("Connect failed" + e);
			return;
		}

		System.out.println("Client connected ..");

		System.out.println("Client connecting ..");

		try {
			client.connect(host, port, password, 2);
		} catch (InboundConnectionFailure e) {
			System.out.println("Connect failed" + e);
			return;
		}

		System.out.println("Client connected ..");
	}
}
