package com.ziac;

import com.codahale.metrics.MetricRegistry;
import com.ziac.Utils.Config;
import com.ziac.Utils.Utils;
import org.apache.gossip.*;
import org.apache.gossip.crdt.OrSet;
import org.apache.gossip.event.GossipListener;
import org.apache.gossip.event.GossipState;
import org.apache.gossip.model.SharedGossipDataMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

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
            String cluster = "My Gossip Cluster1";
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

        } catch (UnknownHostException var10) {
            var10.printStackTrace();
        } catch (InterruptedException var11) {
            var11.printStackTrace();
        }
    }

    /**
     * 测试互相通信
     */
    @Test
    public void test2() {

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
            startupMembers.add(new RemoteGossipMember(cluster, seedUrl, "seed0", 0L, new HashMap()));
            GossipService gossipService0 = new GossipService(cluster, seedUrl, "seed0", new HashMap(), startupMembers, settings, (GossipListener) null, new MetricRegistry());
            gossipService0.start();
            URI url;
            try {
                url = new URI("udp://" + myIpAddress + ":" + 50001);
            } catch (URISyntaxException var9) {
                throw new RuntimeException(var9);
            }
            GossipService gossipService = new GossipService(cluster,
                    url, "node1", new HashMap(), startupMembers, settings,
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

            while (true) {
                System.out.println("Live: " + gossipService.getGossipManager().getLiveMembers());
                System.out.println("Dead: " + gossipService.getGossipManager().getDeadMembers());
                Thread.sleep(2000L);
            }
            //((GossipServiceImpl) clients.get(0)).getGossipManager().shutdown();
        } catch (UnknownHostException var10) {
            var10.printStackTrace();
        } catch (InterruptedException var11) {
            var11.printStackTrace();
        }

    }

    /**
     * test3-6
     * 1111作为seed节点并启动   启动50001监听1111
     */
    @Test
    public void test3() {
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
            startupMembers.add(new RemoteGossipMember(cluster, seedUrl, "seed0", 0L, new HashMap()));

            URI url;
            try {
                url = new URI("udp://" + myIpAddress + ":" + 50002);
            } catch (URISyntaxException var9) {
                throw new RuntimeException(var9);
            }
            GossipService gossipService = new GossipService(cluster,
                    url, "node2", new HashMap(), startupMembers, settings,
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

            while (true) {
                System.out.println("Live: " + gossipService.getGossipManager().getLiveMembers());
                System.out.println("Dead: " + gossipService.getGossipManager().getDeadMembers());
                Thread.sleep(2000L);
            }
            //((GossipServiceImpl) clients.get(0)).getGossipManager().shutdown();
        } catch (UnknownHostException var10) {
            var10.printStackTrace();
        } catch (InterruptedException var11) {
            var11.printStackTrace();
        }

    }

    /**
     *
     * 启动50002监听1111
     * 最后1111 50001  50002实现一致，拥有除自己以外节点的信息。
     */
    @Test
    public void test4() {
        try {
            String cluster = "My Gossip Cluster 1";
            GossipSettings s = new GossipSettings();
            s.setWindowSize(10);
            s.setConvictThreshold(1.0D);
            s.setGossipInterval(10);
            String myIpAddress = InetAddress.getLocalHost().getHostAddress();
            GossipService gossipService = new GossipService("mycluster", URI.create("udp://" + myIpAddress + ":" + 10000), "0", new HashMap(), Arrays.asList(new RemoteGossipMember("mycluster", URI.create("udp://" + myIpAddress + ":" + 10000), "0")), s, (a, b) -> {
            }, new MetricRegistry());
            gossipService.start();

            while (true) {
                System.out.println("Live: " + gossipService.getGossipManager().getLiveMembers());
                System.out.println("Dead: " + gossipService.getGossipManager().getDeadMembers());
                Thread.sleep(2000L);
            }
        } catch (UnknownHostException var10) {
            var10.printStackTrace();
        } catch (InterruptedException var11) {
            var11.printStackTrace();
        }
    }

    @Test
    public void test5() {
        try {
            String myIpAddress = InetAddress.getLocalHost().getHostAddress();
            String cluster = "My Gossip Cluster 1";
            GossipSettings s = new GossipSettings();
            s.setWindowSize(10);
            s.setConvictThreshold(1.0D);
            s.setGossipInterval(10);
            GossipService gossipService = new GossipService("mycluster", URI.create("udp://" + myIpAddress + ":" + 10001), "1", new HashMap(), Arrays.asList(new RemoteGossipMember("mycluster", URI.create("udp://" + myIpAddress + ":" + 10000), "0")), s, (a, b) -> {
            }, new MetricRegistry());
            gossipService.start();

            while (true) {
                System.out.println("Live: " + gossipService.getGossipManager().getLiveMembers());
                System.out.println("Dead: " + gossipService.getGossipManager().getDeadMembers());
                Thread.sleep(2000L);
            }
        } catch (UnknownHostException var10) {
            var10.printStackTrace();
        } catch (InterruptedException var11) {
            var11.printStackTrace();
        }
    }

    @Test
    public void test6() {
        try {
            String myIpAddress = InetAddress.getLocalHost().getHostAddress();
            String cluster = "My Gossip Cluster 1";
            GossipSettings s = new GossipSettings();
            s.setWindowSize(10);
            s.setConvictThreshold(1.0D);
            s.setGossipInterval(10);
            GossipService gossipService = new GossipService("mycluster", URI.create("udp://" + myIpAddress + ":" + 10002), "2", new HashMap(), Arrays.asList(new RemoteGossipMember("mycluster", URI.create("udp://" + myIpAddress + ":" + 10000), "0")), s, (a, b) -> {
            }, new MetricRegistry());
            gossipService.start();

            while (true) {
                System.out.println("Live: " + gossipService.getGossipManager().getLiveMembers());
                System.out.println("Dead: " + gossipService.getGossipManager().getDeadMembers());
                Thread.sleep(2000L);
            }
        } catch (UnknownHostException var10) {
            var10.printStackTrace();
        } catch (InterruptedException var11) {
            var11.printStackTrace();
        }
    }

    @Test
    public void test8() {
        try {
            String[] args = {"udp://192.168.1.3:10000", "0", "udp://192.168.1.3:10000", "0"};
            GossipService gossipService = StandAloneNodeCrdtOrSetService.testCrdtOrSetService(args,"myCluster","abc");
            StandAloneNodeCrdtOrSetService.inputData('a', "123", gossipService);
        } catch (Exception var10) {
            var10.printStackTrace();
        }
    }
    @Test
    public void test9() {
        try {
            String[] args = {"udp://192.168.1.3:10001", "1", "udp://192.168.1.3:10000", "0"};
            GossipService gossipService = StandAloneNodeCrdtOrSetService.testCrdtOrSetService(args,"myCluster","abc");
            StandAloneNodeCrdtOrSetService.inputData('a', "456", gossipService);
            try {
                Thread.sleep(6000L);
            } catch (Exception var2) {
                ;
            }
            StandAloneNodeCrdtOrSetService.inputData('g', "1", gossipService);

        } catch (Exception var10) {
            var10.printStackTrace();
        }
    }
    @Test
    public void test10() {
        try {
            String[] args = {"udp://192.168.1.3:10002", "2", "udp://192.168.1.3:10000", "0"};
            GossipService gossipService = StandAloneNodeCrdtOrSetService.testCrdtOrSetService(args,"myCluster","abc");
            StandAloneNodeCrdtOrSetService.inputData('a', "789", gossipService);
            try {
                Thread.sleep(6000L);
            } catch (Exception var2) {
                ;
            }
            StandAloneNodeCrdtOrSetService.inputData('g', "1", gossipService);

        } catch (Exception var10) {
            var10.printStackTrace();
        }
    }

    /**
     *
     * Test file transfer
     * */
    @Test
    public void test11() {
        try {
            String[] args = {"udp://192.168.1.3:30000", "30", "udp://192.168.1.3:30000", "30"};
            GossipService gossipService = StandAloneNodeCrdtOrSetService.testDataService(args,"myCluster3","test.txt");
            while (true){
                try {
                    Thread.sleep(6000L);
                    System.out.println("---------- " + (gossipService.getGossipManager().findSharedGossipData("test.txt") == null ? "" : (Utils.byte2File((byte[])gossipService.getGossipManager().findSharedGossipData("test.txt").getPayload(), Config.getConfig().getCodeDirectory(),"1.txt")).getPath()));

                } catch (Exception var2) {
                    ;
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }
    }
    @Test
    public void test12() {
        try {
            String[] args = {"udp://192.168.1.3:30002", "32", "udp://192.168.1.3:30000", "30"};
            GossipService gossipService = StandAloneNodeCrdtOrSetService.testDataService(args,"myCluster3","test.txt");
            SharedGossipDataMessage m = new SharedGossipDataMessage();
            m.setExpireAt(9223372036854775807L);
            m.setKey("test.txt");
            com.ziac.Utils.Config config = com.ziac.Utils.Config.getConfig();
            byte[] bytes = Utils.readFile(new File(config.getCodeDirectory()+"/test.txt"));
            m.setPayload(bytes);
            m.setTimestamp(System.currentTimeMillis());
            gossipService.getGossipManager().gossipSharedData(m);
            while (true){
                try {
                    Thread.sleep(6000L);
                } catch (Exception var2) {
                    ;
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }
    }
}
