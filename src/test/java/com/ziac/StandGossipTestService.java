package com.ziac;

import org.apache.gossip.GossipSettings;
import org.apache.gossip.RemoteMember;
import org.apache.gossip.crdt.OrSet;
import org.apache.gossip.manager.GossipManager;
import org.apache.gossip.manager.GossipManagerBuilder;
import org.apache.gossip.model.SharedDataMessage;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class StandGossipTestService {

    private static final String INDEX_KEY_FOR_SET = "abc";

    private static final String INDEX_KEY_FOR_COUNTER = "def";
    public StandGossipTestService() {
    }

    public static GossipManager testCrdtOrSetService(String[] args,String clusterName, String crdtKey) throws InterruptedException, IOException {
        GossipSettings s = new GossipSettings();
        s.setWindowSize(10);
        s.setConvictThreshold(1.0D);
        s.setGossipInterval(10);
        GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(clusterName).uri(URI.create(args[0]))
                .id(args[1]).gossipMembers(Arrays.asList(new RemoteMember(clusterName, URI.create(args[2]), args[3]))).gossipSettings(s).build();
        gossipService.init();

        (new Thread(() -> {
            while(true) {
                System.out.println("Live: " + gossipService.getLiveMembers());
                System.out.println("Dead: " + gossipService.getDeadMembers());
                System.out.println("---------- " + (gossipService.findCrdt(crdtKey) == null ? "" : gossipService.findCrdt(crdtKey).value()));
                System.out.println("********** " + gossipService.findCrdt(crdtKey));

                try {
                    Thread.sleep(3000L);
                } catch (Exception var2) {
                    ;
                }
            }
        })).start();
        return gossipService;

    }
    public static GossipManager getTestGossipService(String[] args,String clusterName, String key) throws InterruptedException, IOException {
        GossipSettings s = new GossipSettings();
        s.setWindowSize(10);
        s.setConvictThreshold(1.0D);
        s.setGossipInterval(10);
        GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(clusterName).uri(URI.create(args[0]))
                .id(args[1]).gossipMembers(Arrays.asList(new RemoteMember(clusterName, URI.create(args[2]), args[3]))).gossipSettings(s).build();
        gossipService.init();
        return gossipService;

    }

    public static GossipManager testDataService(String[] args,String clusterName, String key) throws InterruptedException, IOException {
        GossipSettings s = new GossipSettings();
        s.setWindowSize(10);
        s.setConvictThreshold(1.0D);
        s.setGossipInterval(10);
        GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(clusterName).uri(URI.create(args[0]))
                .id(args[1]).gossipMembers(Arrays.asList(new RemoteMember(clusterName, URI.create(args[2]), args[3]))).gossipSettings(s).build();
        gossipService.init();

        (new Thread(() -> {
            while(true) {
                System.out.println("Live: " + gossipService.getLiveMembers());
                System.out.println("Dead: " + gossipService.getDeadMembers());
                try {
                    Thread.sleep(3000L);
                } catch (Exception var2) {
                    ;
                }
            }
        })).start();
        return gossipService;

    }
    public  static void inputData(char op, String val, GossipManager gossipService){
        Throwable var5 = null;
        try {
            while(true) {
                try {
                    Thread.sleep(6000L);
                } catch (Exception var2) {
                    ;
                }
                System.out.println(op);
                if (op == 'a') {
                    addData(val, gossipService);
                } else {
                    removeData(val, gossipService);
                }
            }
        } catch (Throwable var15) {
            var5 = var15;
            throw var15;
        }
    }

    private static void removeData(String val, GossipManager gossipService) {
        @SuppressWarnings("unchecked")
        OrSet<String> s = (OrSet<String>) gossipService.findCrdt(INDEX_KEY_FOR_SET);
        SharedDataMessage m = new SharedDataMessage();
        m.setExpireAt(Long.MAX_VALUE);
        m.setKey(INDEX_KEY_FOR_SET);
        m.setPayload(new OrSet<String>(s, new OrSet.Builder<String>().remove(val)));
        m.setTimestamp(System.currentTimeMillis());
        gossipService.merge(m);
    }

    private static void addData(String val, GossipManager gossipService) {
        SharedDataMessage m = new SharedDataMessage();
        m.setExpireAt(Long.MAX_VALUE);
        m.setKey(INDEX_KEY_FOR_SET);
        m.setPayload(new OrSet<String>(val));
        m.setTimestamp(System.currentTimeMillis());
        gossipService.merge(m);
    }
}
