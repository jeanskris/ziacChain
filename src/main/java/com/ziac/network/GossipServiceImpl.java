package com.ziac.network;

import com.codahale.metrics.MetricRegistry;
import com.ziac.Utils.Config;
import org.apache.gossip.GossipService;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.RemoteGossipMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

@Service
public class GossipServiceImpl {
    public static Logger log = LoggerFactory.getLogger(GossipServiceImpl.class);
    private Config config = Config.getConfig();

    public GossipService getGossipService(String localIP, String port, String nodeId) throws InterruptedException, IOException
    {
        GossipSettings s = new GossipSettings();
        s.setWindowSize(10);
        s.setConvictThreshold(1.0D);
        s.setGossipInterval(10);
        GossipService gossipService = new GossipService(config.getClusterName(), URI.create("udp://" + config.getSeedIp() + ":" + config.getSeedPort()), config.getSeedId(), new HashMap(), Arrays.asList(new RemoteGossipMember(config.getClusterName(), URI.create("udp://" + localIP + ":" + port), nodeId)), s, (a, b) -> {
        }, new MetricRegistry());
        gossipService.start();
        return gossipService;
    }


}
