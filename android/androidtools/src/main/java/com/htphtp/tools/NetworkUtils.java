package com.htphtp.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;


public class NetworkUtils {


    private NetworkUtils() {

    }

    private static String[] NET_WORK_OPERATOR_NAME = {"移动", "联通", "电信"};

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NetworkOperators.NONE, NetworkOperators.CHINA_MOBILE, NetworkOperators.CHINA_UNICOM, NetworkOperators.CHINA_TELECOM})
    public @interface NetworkOperators {
        int NONE = 3;
        int CHINA_MOBILE = 0; //移动
        int CHINA_UNICOM = 1; //联通
        int CHINA_TELECOM = 2;//电信
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({NetworkType.UNKNOW, NetworkType.INVALID, NetworkType.WIFI, NetworkType.G2, NetworkType.G3, NetworkType.G4})
    public @interface NetworkType {
        String UNKNOW = "NET_WORK_UNKNOWN";
        String INVALID = "NET_WORK_INVALID";
        String WIFI = "WIFI";
        String G2 = "2G";
        String G3 = "3G";
        String G4 = "4G";
    }


    public static String getNetworkType(Context context) {
        @NetworkType String strNetworkType = NetworkType.UNKNOW;

        if (context == null) {
            return strNetworkType;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return strNetworkType;
        }

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = NetworkType.WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
//                L.d("_strSubTypeName : " + _strSubTypeName);
                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = NetworkType.G2;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = NetworkType.G3;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                    case 19: // TelephonyManager.NETWORK_TYPE_LTE_CA
                        strNetworkType = NetworkType.G4;
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase(
                                "TD-SCDMA") || _strSubTypeName.equalsIgnoreCase(
                                "WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = NetworkType.G3;
                        } else {
//                            strNetworkType = _strSubTypeName;
                            strNetworkType = NetworkType.UNKNOW;
                        }

                        break;
                }

            }
        } else {
            strNetworkType = NetworkType.INVALID;
        }


        return strNetworkType;
    }

    public static boolean hasNetwork(Context context) {
        String type = getNetworkType(context);
        return !(NetworkType.UNKNOW.equals(type) || NetworkType.INVALID.equals(type));
    }

    public static String getNetworkOperatorNameAndType(Context context) {
        String oName = getNetworkOperatorName(getNetworkOperator(context));
        String netWorkType = getNetworkType(context);
        if (!oName.equals("")) {
            return oName + netWorkType;
        }
        return netWorkType;
    }

    public static String getNetworkOperatorName(@NetworkOperators int netWorkOperator) {
        if (netWorkOperator == NetworkOperators.NONE) {
            return "";
        }

        return NET_WORK_OPERATOR_NAME[netWorkOperator];
    }

    public static int getNetworkOperator(Context context) {
        @NetworkOperators int netWorkOperators = NetworkOperators.NONE;

        if (context == null) {
            return netWorkOperators;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return netWorkOperators;
        }

        String net = null;

        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getTypeName().equals("WIFI")) {
                net = info.getTypeName();
            } else {
                net = info.getExtraInfo();// cmwap/cmnet/wifi/uniwap/uninet
            }
        }

        List<String> infos = getNetworkList(cm);
        if (net == null || net.equals("WIFI")) {
            if (infos.size() > 1) {
                infos.remove("WIFI");
                net = infos.get(0);
                netWorkOperators = getNetworkOperatorType(net);
            } else {
            }
        } else {
            netWorkOperators = getNetworkOperatorType(net);
        }
        return netWorkOperators;
    }

    private static int getNetworkOperatorType(String net) {
        @NetworkOperators int netWorkOperators = NetworkOperators.NONE;
        if (net.equals("3gwap") || net.equals("uniwap")
                || net.equals("3gnet") || net.equals("uninet")) {
            netWorkOperators = NetworkOperators.CHINA_UNICOM;
        } else if (net.equals("cmnet") || net.equals("cmwap")) {
            netWorkOperators = NetworkOperators.CHINA_MOBILE;
        } else if (net.equals("ctnet") || net.equals("ctwap")) {
            netWorkOperators = NetworkOperators.CHINA_TELECOM;
        }
        return netWorkOperators;
    }

    private static List<String> getNetworkList(ConnectivityManager cm) {
        NetworkInfo[] infos = cm.getAllNetworkInfo();
        List<String> list = new ArrayList();
        if (infos != null) {
            for (int i = 0; i < infos.length; i++) {
                NetworkInfo info = infos[i];
                String name = "";
                if (info.getTypeName().equals("WIFI")) {
                    name = info.getTypeName();
                } else {
                    name = info.getExtraInfo();
                }
                if (name != null && list.contains(name) == false) {
                    list.add(name);
                    // System.out.println(name);
                }
            }
        }
        return list;
    }
}
