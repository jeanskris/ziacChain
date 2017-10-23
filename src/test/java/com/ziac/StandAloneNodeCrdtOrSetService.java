package com.ziac;

import com.codahale.metrics.MetricRegistry;
import com.ziac.Utils.Config;
import com.ziac.Utils.Utils;
import org.apache.gossip.GossipService;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.RemoteGossipMember;
import org.apache.gossip.crdt.OrSet;
import org.apache.gossip.model.SharedGossipDataMessage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

public class StandAloneNodeCrdtOrSetService {

    private static final String INDEX_KEY_FOR_SET = "abc";

    private static final String INDEX_KEY_FOR_COUNTER = "def";
    public StandAloneNodeCrdtOrSetService() {
    }

    public static GossipService testCrdtOrSetService(String[] args,String clusterName, String crdtKey) throws InterruptedException, IOException {
        GossipSettings s = new GossipSettings();
        s.setWindowSize(10);
        s.setConvictThreshold(1.0D);
        s.setGossipInterval(10);
        GossipService gossipService = new GossipService(clusterName, URI.create(args[0]), args[1], new HashMap(), Arrays.asList(new RemoteGossipMember(clusterName, URI.create(args[2]), args[3])), s, (a, b) -> {
        }, new MetricRegistry());
        gossipService.start();
        (new Thread(() -> {
            while(true) {
                System.out.println("Live: " + gossipService.getGossipManager().getLiveMembers());
                System.out.println("Dead: " + gossipService.getGossipManager().getDeadMembers());
                System.out.println("---------- " + (gossipService.getGossipManager().findCrdt(crdtKey) == null ? "" : gossipService.getGossipManager().findCrdt(crdtKey).value()));
                System.out.println("********** " + gossipService.getGossipManager().findCrdt(crdtKey));

                try {
                    Thread.sleep(3000L);
                } catch (Exception var2) {
                    ;
                }
            }
        })).start();
        return gossipService;

    }


    public static GossipService testDataService(String[] args,String clusterName, String key) throws InterruptedException, IOException {
        GossipSettings s = new GossipSettings();
        s.setWindowSize(10);
        s.setConvictThreshold(1.0D);
        s.setGossipInterval(10);
        GossipService gossipService = new GossipService(clusterName, URI.create(args[0]), args[1], new HashMap(), Arrays.asList(new RemoteGossipMember(clusterName, URI.create(args[2]), args[3])), s, (a, b) -> {
        }, new MetricRegistry());
        gossipService.start();
        (new Thread(() -> {
            while(true) {
                System.out.println("Live: " + gossipService.getGossipManager().getLiveMembers());
                System.out.println("Dead: " + gossipService.getGossipManager().getDeadMembers());


                try {
                    Thread.sleep(3000L);
                } catch (Exception var2) {
                    ;
                }
            }
        })).start();
        return gossipService;

    }
    public  static void inputData(char op, String val, GossipService gossipService){
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

    private static void removeData(String val, GossipService gossipService) {
        OrSet<String> s = (OrSet)gossipService.getGossipManager().findCrdt("abc");
        SharedGossipDataMessage m = new SharedGossipDataMessage();
        m.setExpireAt(9223372036854775807L);
        m.setKey("abc");
        m.setPayload(new OrSet(s, (new OrSet.Builder()).remove(val)));
        m.setTimestamp(System.currentTimeMillis());
        gossipService.getGossipManager().merge(m);
    }

    private static void addData(String val, GossipService gossipService) {
        SharedGossipDataMessage m = new SharedGossipDataMessage();
        m.setExpireAt(9223372036854775807L);
        m.setKey("abc");
        m.setPayload(new OrSet(new String[]{val}));
        m.setTimestamp(System.currentTimeMillis());
        gossipService.getGossipManager().merge(m);
    }
}
