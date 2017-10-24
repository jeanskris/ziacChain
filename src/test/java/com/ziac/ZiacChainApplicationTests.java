package com.ziac;

import com.ziac.Utils.Config;
import com.ziac.Utils.Utils;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.Member;
import org.apache.gossip.RemoteMember;
import org.apache.gossip.manager.GossipManager;
import org.apache.gossip.manager.GossipManagerBuilder;
import org.apache.gossip.model.SharedDataMessage;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZiacChainApplicationTests {
    private Logger logger = LoggerFactory.getLogger(ZiacChainApplicationTests.class);

    @Test
    public void contextLoads() {
    }
    private static final List<GossipManager> clients = new ArrayList<>();

 
    public static void initializeMembers() throws InterruptedException, UnknownHostException, URISyntaxException{
        final int clusterMembers = 2;

        GossipSettings settings = new GossipSettings();
        settings.setPersistRingState(false);
        settings.setPersistDataState(false);
        String cluster = UUID.randomUUID().toString();
        List<Member> startupMembers = new ArrayList<>();
        for (int i = 0; i < clusterMembers; ++i){
            int id = i + 1;
            URI uri = new URI("udp://" + "127.0.0.1" + ":" + (50000 + id));
            startupMembers.add(new RemoteMember(cluster, uri, id + ""));
        }

        for (Member member : startupMembers){
            GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(cluster).uri(member.getUri())
                    .id(member.getId()).gossipMembers(startupMembers).gossipSettings(settings).build();
            clients.add(gossipService);
            gossipService.init();
        }
    }
    
    public static void shutdownMembers(){
        for (final GossipManager client : clients){
            client.shutdown();
        }
    }
    

    /**
     * test3-6
     * 1111作为seed节点并启动
     */
    @Test
    public void test3() {
        try {
            GossipSettings settings = new GossipSettings();
            settings.setGossipInterval(1);
            settings.setCleanupInterval(1000);
            List<GossipManager> clients = new ArrayList();
            String myIpAddress = InetAddress.getLocalHost().getHostAddress();
            String cluster = "My Gossip Cluster 1";
            List<Member> startupMembers = new ArrayList();

            URI seedUrl;
            try {
                seedUrl = new URI("udp://" + myIpAddress + ":" + 11111);
            } catch (URISyntaxException var9) {
                throw new RuntimeException(var9);
            }
            startupMembers.add(new Member(cluster, seedUrl, "0", 0L, new HashMap()) {});

            URI url;
            try {
                url = new URI("udp://" + myIpAddress + ":" + 11111);
            } catch (URISyntaxException var9) {
                throw new RuntimeException(var9);
            }
            GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(cluster).uri(url)
                    .id("0").gossipMembers(startupMembers).gossipSettings(settings).build();
            
            clients.add(gossipService);
            gossipService.init();

            while (true) {
                System.out.println("Live: " + gossipService.getLiveMembers());
                System.out.println("Dead: " + gossipService.getDeadMembers());
                Thread.sleep(2000L);
            }
            //((GossipManagerImpl) clients.get(0)).shutdown();
        } catch (UnknownHostException var10) {
            var10.printStackTrace();
        } catch (InterruptedException var11) {
            var11.printStackTrace();
        }

    }
    /**
     *。
     */
    @Test
    public void test31() {
        try {
            String cluster = "My Gossip Cluster 1";
            GossipSettings s = new GossipSettings();
            s.setWindowSize(10);
            s.setConvictThreshold(1.0D);
            s.setGossipInterval(10);
            String myIpAddress = InetAddress.getLocalHost().getHostAddress();
            GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(cluster).uri(URI.create("udp://" + myIpAddress + ":" + 50002))
                    .id("node2").gossipMembers(Arrays.asList(new RemoteMember(cluster, URI.create("udp://" + myIpAddress + ":" + 11111), "0"))).gossipSettings(s).build();
            gossipService.init();
            while (true) {
                System.out.println("Live: " + gossipService.getLiveMembers());
                System.out.println("Dead: " + gossipService.getDeadMembers());
                Thread.sleep(2000L);
            }

        } catch (UnknownHostException var10) {
        var10.printStackTrace();
        }catch (InterruptedException var11) {
            var11.printStackTrace();
        }
    }
    /**
     *。
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
            GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(cluster).uri(URI.create("udp://" + myIpAddress + ":" + 10000))
                    .id("0").gossipMembers(Arrays.asList(new RemoteMember(cluster, URI.create("udp://" + myIpAddress + ":" + 10000), "0"))).gossipSettings(s).build();
            gossipService.init();

            while (true) {
                System.out.println("Live: " + gossipService.getLiveMembers());
                System.out.println("Dead: " + gossipService.getDeadMembers());
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
            GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(cluster).uri(URI.create("udp://" + myIpAddress + ":" + 10001))
                    .id("1").gossipMembers(Arrays.asList(new RemoteMember(cluster, URI.create("udp://" + myIpAddress + ":" + 10000), "0"))).gossipSettings(s).build();
            gossipService.init();

            while (true) {
                System.out.println("Live: " + gossipService.getLiveMembers());
                System.out.println("Dead: " + gossipService.getDeadMembers());
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
            GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(cluster).uri(URI.create("udp://" + myIpAddress + ":" + 10002))
                    .id("2").gossipMembers(Arrays.asList(new RemoteMember(cluster, URI.create("udp://" + myIpAddress + ":" + 10000), "0"))).gossipSettings(s).build();
            gossipService.init();
            while (true) {
                System.out.println("Live: " + gossipService.getLiveMembers());
                System.out.println("Dead: " + gossipService.getDeadMembers());
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
            String[] args = {"udp://192.168.1.103:10000", "0", "udp://192.168.1.103:10000", "0"};
            GossipManager gossipService = StandGossipTestService.testCrdtOrSetService(args,"myCluster","abc");
            StandGossipTestService.inputData('a', "123", gossipService);
        } catch (Exception var10) {
            var10.printStackTrace();
        }
    }
    @Test
    public void test9() {
        try {
            String[] args = {"udp://192.168.1.103:10001", "1", "udp://192.168.1.103:10000", "0"};
            GossipManager gossipService = StandGossipTestService.testCrdtOrSetService(args,"myCluster","abc");
            StandGossipTestService.inputData('a', "456", gossipService);
            try {
                Thread.sleep(6000L);
            } catch (Exception var2) {
                ;
            }
            StandGossipTestService.inputData('g', "1", gossipService);

        } catch (Exception var10) {
            var10.printStackTrace();
        }
    }
    @Test
    public void test10() {
        try {
            String[] args = {"udp://192.168.1.103:10002", "2", "udp://192.168.1.103:10000", "0"};
            GossipManager gossipService = StandGossipTestService.testCrdtOrSetService(args,"myCluster","abc");
            StandGossipTestService.inputData('a', "789", gossipService);
            try {
                Thread.sleep(6000L);
            } catch (Exception var2) {
                ;
            }
            StandGossipTestService.inputData('g', "1", gossipService);

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
            String[] args = {"udp://192.168.1.103:30000", "30", "udp://192.168.1.103:30000", "30"};
            GossipManager gossipService = StandGossipTestService.testDataService(args,"myCluster3","test.txt");
            while (true){
                try {
                    Thread.sleep(6000L);
                    System.out.println("---------- " + (gossipService.findSharedGossipData("test.txt") == null ? "" : (Utils.byte2File((byte[])gossipService.findSharedGossipData("test.txt").getPayload(), Config.getConfig().getCodeDirectory(),"1.txt")).getPath()));

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
            String[] args = {"udp://192.168.1.103:30002", "32", "udp://192.168.1.103:30000", "30"};
            GossipManager gossipService = StandGossipTestService.testDataService(args,"myCluster3","test.txt");
            SharedDataMessage m = new SharedDataMessage();
            m.setExpireAt(9223372036854775807L);
            m.setKey("test.txt");
            com.ziac.Utils.Config config = com.ziac.Utils.Config.getConfig();
            byte[] bytes = Utils.readFile(new File(config.getCodeDirectory()+"/test.txt"));
            m.setPayload(bytes);
            m.setTimestamp(System.currentTimeMillis());
            gossipService.gossipSharedData(m);
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
