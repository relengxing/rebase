package com.relengxing.rebase.utils;

import sun.net.util.IPAddressUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;

/**
 * IpUtil
 *
 *
 */
public class IpUtil {

    /**
     * 获取外网地址
     * @param strUrl
     * @return
     */
    public static String getWebIP(String strUrl) {
        BufferedReader br = null;
        try {
            //连接网页
            URL url = new URL(strUrl);
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = "";
            StringBuffer sb = new StringBuffer("");
            String webContent = "";
            //读取网页信息
            while ((s = br.readLine()) != null) {
                sb.append(s + "\r\n");
            }
            //网页信息
            webContent = sb.toString();
            int start = webContent.indexOf("[")+1;
            int end = webContent.indexOf("]");
            //获取网页中  当前 的 外网IP
            webContent = webContent.substring(start,end);
            return webContent;

        } catch (Exception e) {
            e.printStackTrace();
            return "error open url:" + strUrl;
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println("外网IP地址获取失败" + e.toString());
                }
            }
        }
    }

    /**
     * 获取内网ip
     * @Return java.lang.String
     */
    public static String getLocalIP()  {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface =  allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }


    /**
     * 判断是否是内网ip
     * @param ip
     * @return
     */
    public static boolean internalIp(String ip) {
        if("127.0.0.1".equals(ip)) {
            return true;
        }
        byte[] addr = IPAddressUtil.textToNumericFormatV4(ip);
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        //10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        //172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        //192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }

}
