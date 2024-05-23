package org.example.Fog;

import org.example.utils.Ip;

public class CentralProxy {
    public static void main(String[] args) throws Exception {
        Proxy proxy = new Proxy(Ip.IP_FOG, 5, Ip.IP_FOG, Ip.IP_CLOUD, Ip.IP_EDGE, Ip.IP_CLOUD);
        proxy.start();
    }
}
