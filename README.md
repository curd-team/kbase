### 基金数据抓取说明
python 版本3.8
相关工具库：requests_html、execjs、csv、json
数据爬取流程：
获取基金列表->获取基金基本数据->获取基金经理数据->获取基金公司数据

获取基金列表：
http://fund.eastmoney.com/js/fundcode_search.js

获取基金基本数据：
http://fund.eastmoney.com/pingzhongdata/基金代码.js?v=20201105192831
http://fundf10.eastmoney.com/jbgk_基金代码.html

获取基金经理数据：
http://fund.eastmoney.com/manager/基金经理ID.html

基金公司数据:
http://fund.eastmoney.com/Company/f10/jbgk_基金公司ID.html