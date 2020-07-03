package com.soft.support;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 公共方法
 */
public class TransCommon {

    /**
     * 从symbol获取要消耗的币种
     * @param symbol
     * @param orderType
     * @return
     */
    //todo 逻辑优化
    public static String getCostBi(String symbol,String orderType){
        if(symbol == null){
            return symbol;
        }
        if(symbol.contains(SymbolEnum.usdt.name())){
            if(orderType.contains("BUY")){
                return SymbolEnum.usdt.name();
            }else{
                return symbol.replaceAll(SymbolEnum.usdt.name(),"");
            }
        }
        if(symbol.contains(SymbolEnum.btc.name())){
            if(orderType.contains("BUY")){
                return SymbolEnum.btc.name();
            }else{
                return symbol.replaceAll(SymbolEnum.btc.name(),"");
            }
        }
        if(symbol.contains(SymbolEnum.eth.name())){
            if(orderType.contains("BUY")){
                return SymbolEnum.eth.name();
            }else{
                return symbol.replaceAll(SymbolEnum.eth.name(),"");
            }
        }
        return symbol;
    }


    /**
     * 和当前时间相差距离多少天多少小时多少分多少秒
     * @param one 时间参数 1 格式：1990-01-01 12:00:00
     * @return long 返回值为： 分
     */
    public static long getDistanceTimes(Date one) {
        Date two = new Date();
        long min = 0;
        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff ;
        if(time1<time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        min = diff / (1000 * 60);
        return min;
    }

    public static Date getDateByTimeStamp(long timeStamp){
        if(timeStamp == 0){
            return null;
        }else{
            //因时区不同需要加上它8个小时
            Date date = new Date(timeStamp);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR, 8);// 24小时制
            date = cal.getTime();
            return date;
        }
    }


    //根据timestamp获取小时
    public static String getHourByByTimeStamp(long timestamp){
        Date d = getDateByTimeStamp(timestamp);
        SimpleDateFormat df = new SimpleDateFormat("HH");
        return df.format(d);
    }

    //根据timestamp获取天
    public static String getDayByByTimeStamp(long timestamp){
        Date d = getDateByTimeStamp(timestamp);
        SimpleDateFormat df = new SimpleDateFormat("dd");
        return df.format(d);
    }

    /**
     * 去除重复元素
     * @param shu  元素
     * @param targ  要选多少个元素
     * @param has   当前有多少个元素
     */
    private static void ann(String[] shu, int targ, int has) {
        if(has == targ) {
            //System.out.println(stack);
            Stack<String> sc = new Stack<>();
            sc.addAll(stack);
            an.add(sc);
            return;
        }

        for(int i=0;i<shu.length;i++) {
            if(!stack.contains(shu[i])) {
                stack.add(shu[i]);
                ann(shu, targ, has+1);
                stack.pop();
            }
        }
    }

    /**
     * 包含重复元素
     * @param shu  元素
     * @param targ  要选多少个元素
     * @param has   当前有多少个元素
     */
    private static void annPlus(String[] shu, int targ, int has) {
        if(has == targ) {
            Stack<String> sc = new Stack<>();
            sc.addAll(stack);
            anpuls.add(sc);
            //System.out.println(stack);
            return;
        }

        for(int i=0;i<shu.length;i++) {
            stack.add(shu[i]);
            annPlus(shu, targ, has+1);
            stack.pop();
        }
    }
    static List<Stack> an = new ArrayList();
    static List<Stack> anpuls = new ArrayList();
    static Stack<String> stack = new Stack<String>();

    /**
     * 获取条件的排列组合
     * @param method
     * @return
     */
    public static List<Stack> getConditions(String method[]){
        String logic[] = {"&&","||"};
        List<Stack> result = new ArrayList<>();
        stack = new Stack<String>();
        anpuls = new ArrayList();
        an = new ArrayList();

        for(int i=1;i<=method.length;i++){
            an = new ArrayList<>();
            anpuls = new ArrayList<>();
            ann(method,i,0);
            annPlus(logic,i-1,0);
            for(Stack<String> scan :an){
                for(Stack<String> scanPlus :anpuls){
                    Stack<String> sc = new Stack<>();
                    for(int j=0;j<scan.size();j++){
                        sc.push(scan.get(j));
                        if(j<scanPlus.size()){
                            sc.push(scanPlus.get(j));
                        }
                        if(j==scanPlus.size()){
                            result.add(sc);
                        }
                    }
                }
            }
        }

        //String buyMethod[] = {"getIsOverDown","getIsOverUp","getIsOverTodayLow","getIsOverDownCount","getIsOverUpCount"};
        //String sellMethod[] = {"getIsOverDown","getIsOverUp","getIsOverTodayHigh","getIsOverDownCount","getIsOverUpCount","getIsOverUpPrice","getIsOverDownPrice"};
        //排除不可能存在的情况
        String []notExists = {
                //既跌又涨情况
                "getIsOverDown&&getIsOverUp",
                "getIsOverTodayLow&&getIsOverTodayHigh",
                "getIsOverDownCount&&getIsOverUpCount",
                "getIsOverDownPrice&&getIsOverUpPrice",

                "getIsOverDown&&getIsOverUpCount",
                "getIsOverDown&&getIsOverTodayHigh",
                "getIsOverTodayLow&&getIsOverUpCount",
        };
        Iterator it = result.iterator();
        while(it.hasNext()) {
            Stack<String> st = (Stack) it.next();
            String ss = "";
            for(String s:st){
                ss = s+ss;
            }
            for(String notExist:notExists){
                String reverts[] = notExist.split("&&");
                String r= reverts[1]+"&&"+reverts[0];
                if(ss.contains(notExist) || ss.contains(r)){
                    it.remove();
                    break;
                }
            }

        }
        return result;
    }


}
