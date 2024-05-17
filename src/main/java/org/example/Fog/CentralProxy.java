package org.example.Fog;

import org.example.utils.Ip;

public class CentralProxy {
    public static void main(String[] args) throws Exception {
        Proxy proxy = new Proxy(Ip.PROXY_PRINCIPAL, 5, Ip.SC_FOG, Ip.HEALTH_CHECKER, Ip.CENTRAL_SENSOR, Ip.CLOUD);
        proxy.start();
    }
}
