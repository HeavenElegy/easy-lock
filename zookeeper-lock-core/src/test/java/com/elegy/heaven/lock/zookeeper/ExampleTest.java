package com.elegy.heaven.lock.zookeeper;

import org.junit.Test;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * @author lixiaoxi_v
 * @date 2020-11-23 18:04:29
 */
public class ExampleTest {

    @Test
    public void test01() throws SocketException {
        Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (interfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = interfaceEnumeration.nextElement();
            System.out.println(Arrays.toString(networkInterface.getHardwareAddress()));
        }
    }
}
