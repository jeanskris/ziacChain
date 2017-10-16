package com.ziac;

import com.codahale.metrics.MetricRegistry;
import org.apache.gossip.*;
import org.apache.gossip.event.GossipListener;
import org.apache.gossip.event.GossipState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static java.lang.Thread.sleep;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZiacChainApplicationTests {
	private Logger logger = LoggerFactory.getLogger(ZiacChainApplicationTests.class);
	@Test
	public void contextLoads() {
	}
	@Test
	public void gossipTest() {
	try {
			GossipSettings settings = new GossipSettings();
			List<GossipMember> startupMembers = new ArrayList<>();
			List<GossipService> clients = new ArrayList<>();
			String myIpAddress = InetAddress.getLocalHost().getHostAddress();
			String cluster = "My Gossip Cluster";
			for (int i = 0; i < 4; ++i) {
				URI u;
				try {
					u = new URI("udp://" + myIpAddress + ":" + (2000 + i));
					System.out.println(u.toString());
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}

				startupMembers.add(new RemoteGossipMember(cluster, u, "", 0L, new HashMap()));
			}

			Iterator iterator = startupMembers.iterator();

			while (iterator.hasNext()) {
				GossipMember member = (GossipMember) iterator.next();
				GossipService gossipService = new GossipService(cluster, member.getUri(), "", new HashMap(), startupMembers, settings, (GossipListener) null, new MetricRegistry());
				clients.add(gossipService);
				gossipService.start();
				sleep((long) (settings.getCleanupInterval() + 1000));
			}

			((GossipService) clients.get(0)).shutdown();

		}  catch (UnknownHostException var10) {
		var10.printStackTrace();
	} catch (InterruptedException var11) {
		var11.printStackTrace();
	}
	}
	@Test
	public  void  test2(){

		try {
			GossipSettings settings = new GossipSettings();
			settings.setGossipInterval(1);
			settings.setCleanupInterval(1000);
			List<GossipService> clients = new ArrayList();
			String myIpAddress = InetAddress.getLocalHost().getHostAddress();
			String cluster = "My Gossip Cluster 1";
			List<GossipMember> startupMembers = new ArrayList();

			URI seedUrl;
			try {
				seedUrl = new URI("udp://" + myIpAddress + ":" + 11111);
			} catch (URISyntaxException var9) {
				throw new RuntimeException(var9);
			}
			startupMembers.add(new RemoteGossipMember(cluster, seedUrl, "seed", 0L, new HashMap()));
			GossipService gossipService0 = new GossipService(cluster, seedUrl, "", new HashMap(), startupMembers, settings, (GossipListener) null, new MetricRegistry());
			gossipService0.start();
			URI url;
			try {
				url = new URI("udp://" + myIpAddress + ":" + 50003);
			} catch (URISyntaxException var9) {
				throw new RuntimeException(var9);
			}
			GossipService gossipService = new GossipService(cluster,
					url, "seed3", new HashMap(), startupMembers, settings,
					new GossipListener() {
						@Override
						public void gossipEvent(GossipMember member, GossipState state) {
							System.out.println(System.currentTimeMillis() + " "
									+ member + " " + state);
						}
					},
					new MetricRegistry());
			clients.add(gossipService);
			gossipService.start();

			while(true) {
				sleep(2000L);
				if (!clients.isEmpty()) {
					if (!clients.isEmpty()) {
						List<LocalGossipMember> localGossipMemberList1 = clients.get(0).getGossipManager().getLiveMembers();
						for (LocalGossipMember localGossipMember : localGossipMemberList1) {
							System.out.print(" " + localGossipMember.getUri());

						}
					}
				}
			}
			//((GossipService) clients.get(0)).getGossipManager().shutdown();
		} catch (UnknownHostException var10) {
			var10.printStackTrace();
		} catch (InterruptedException var11) {
			var11.printStackTrace();
		}

	}

	@Test
	public void test3(){
		try {
			GossipSettings settings = new GossipSettings();
			settings.setGossipInterval(1);
			settings.setCleanupInterval(1000);
			List<GossipService> clients = new ArrayList();
			String myIpAddress = InetAddress.getLocalHost().getHostAddress();
			String cluster = "My Gossip Cluster 1";
			List<GossipMember> startupMembers = new ArrayList();

			URI url;
			try {
				url = new URI("udp://" + myIpAddress + ":" + 50004);
			} catch (URISyntaxException var9) {
				throw new RuntimeException(var9);
			}
			GossipService gossipService = new GossipService(cluster,
					url, "seed4", new HashMap(), startupMembers, settings,
					new GossipListener() {
						@Override
						public void gossipEvent(GossipMember member, GossipState state) {
							System.out.println(System.currentTimeMillis() + " "
									+ member + " " + state);
						}
					},
					new MetricRegistry());
			clients.add(gossipService);
			gossipService.start();

			while(true) {
				sleep(2000L);
				if(!clients.isEmpty()) {
					List<LocalGossipMember> localGossipMemberList1 = clients.get(0).getGossipManager().getLiveMembers();
					for (LocalGossipMember localGossipMember : localGossipMemberList1) {
						System.out.print(" " + localGossipMember.getUri());
					}
				}
			}

		} catch (UnknownHostException var10) {
			var10.printStackTrace();
		} catch (InterruptedException var11) {
			var11.printStackTrace();
		}
	}

}
