# IntelligentTransaction-huobi
IntelligentTransaction-huobi

火币高频交易app

扫描流程
for(;;){
    扫描币种
    按照算法计算收益 - 发现多次大于0后 - 触发购买流程
}

购买流程
param:(symbol1,symbol12,symbol3,三种数量,三种价格,购买量?)
按照价格下单第一种币 - 成功后取得订单号
for(;;){
    订单完成后 - 查询另外两种币价看是否拥有预期收益
    有预期收益 - break
    无预期收益 - 有预期收益后break or 涨价后抛掉
}

开始进行第二波交易

2019-09-16 清库
2019-10-06 
1--eos btc eth 三个币种现货走势之间的关系 eos/btc 涨跌时是否可以买进卖出？？
2--利用websocket实时监控走势，大跌的盘入手
发生异常时、 大盘跌时增加微信通知
3--eos走势要比btc eth靠前 跟eos/btc




Tips 用历史盘去测试程序性能 编写测试类
引入股票计算工具类
分析当日几个最高点和当日几个最低点前的数据看有何相似处

