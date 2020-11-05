import requests
import time
import execjs
import csv
import json
import sys
from requests_html import HTMLSession


headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/'
                  '537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36',
    'Referer': "http://fund.eastmoney.com/data/fundranking.html",
}
def getUrl(fscode):
    head = 'http://fund.eastmoney.com/pingzhongdata/'
    tail = '.js?v=' + time.strftime("%Y%m%d%H%M%S", time.localtime())
    return head + fscode + tail


# 根据基金代码获取基金信息
def getWorth(fscode):
    fundFile = open('./fundsdata.csv', 'a', encoding='utf-8', newline='')
    writer = csv.writer(fundFile)
    content = requests.get(getUrl(fscode))
    jsContent = execjs.compile(content.text)

    # 基金名称+代码
    name = jsContent.eval('fS_name')
    code = jsContent.eval('fS_code')
    detailUrl = 'http://fundf10.eastmoney.com/jbgk_'+code+'.html';

    # 收益率
    #近一年收益率
    syl_1n = jsContent.eval('syl_1n')
    # 近六月收益率
    syl_6y = jsContent.eval('syl_6y')
    # 近三月收益率
    syl_3y = jsContent.eval('syl_3y')
    # 近一月收益率
    syl_1y = jsContent.eval('syl_1y')
    currentFundManager = jsContent.eval('Data_currentFundManager')
    fund = {}
    managers = []
    company = {}
    #处理基金经理列表
    for manager in currentFundManager:
        url = 'http://fund.eastmoney.com/manager/' + manager['id'] + '.html'
        #获取基金经理说明
        fullXPath = '/html/body/div[6]/div[2]/div[1]/div/div[1]/p/text()'
        desc = ''
        session = HTMLSession()
        result = session.get(url, headers=headers, timeout=15)
        if result.status_code == 200:
            manager_desc = result.html.xpath('/html/body/div[6]/div[2]/div[1]/div/div[1]/p/text()')
            if len(manager_desc) > 1:
                desc = manager_desc[1]
        managers.append({'name': manager['name'], 'pic': manager['pic'],
                         'url': url,'desc':desc,
                         'workTime': manager['workTime'], 'fundSize': manager['fundSize']})


    session = HTMLSession()
    #获取基金类型、基金公司页面、基金成立日期
    result = session.get(detailUrl, headers=headers, timeout=15)
    if result.status_code == 200:
      fundDate = result.html.xpath('/html/body/div[2]/div[8]/div[3]/div[1]/div[2]/p/label[1]/span')[0].text
      fundType = result.html.xpath('/html/body/div[2]/div[8]/div[3]/div[1]/div[2]/p/label[3]/span')[0].text
      company = result.html.xpath('/html/body/div[2]/div[8]/div[3]/div[1]/div[2]/p/label[4]/a')[0].text
      url = result.html.xpath('/html/body/div[2]/div[8]/div[3]/div[1]/div[2]/p/label[4]/a')[0].attrs['href']
      cid = url[url.rindex('/') + 1:url.rindex('.')]
      gkurl = 'http://fund.eastmoney.com/Company/f10/jbgk_' + cid + '.html'
      result = session.get(gkurl, headers=headers, timeout=15)
      if result.status_code == 200:
          scale = result.html.xpath('/html/body/div[1]/div[1]/div[5]/div[1]/div[2]/ul/li[1]/label/text()')[0]
          fundNum = result.html.xpath('/html/body/div[1]/div[1]/div[5]/div[1]/div[2]/ul/li[2]/label/a/text()')[0]
          managerNum = result.html.xpath('/html/body/div[1]/div[1]/div[5]/div[1]/div[2]/ul/li[3]/label/a/text()')[0]
          fundDate = result.html.xpath('/html/body/div[1]/div[1]/div[5]/div[1]/div[2]/ul/li[5]/label/text()')[0]
          nature = result.html.xpath('/html/body/div[1]/div[1]/div[5]/div[1]/div[2]/ul/li[6]/label/text()')[0]
          trs = result.html.xpath('/html/body/div[1]/div[1]/div[5]/div[3]/div[2]/table/tr')
          fullName = trs[0].xpath('.//td[2]//text()')[0]
          representative = trs[4].xpath('.//td[2]//text()')[0]
          netaddr = trs[7].xpath('.//td[2]//text()')[0]
          businessScope = trs[10].xpath('.//td[2]//text()')[0]
      fund = {'name':name, 'fundCode':code, 'fundType': fundType, 'fundDate':fundDate,'syl_1n':syl_1n, 'syl_6y':syl_6y, 'syl_3y':syl_3y, 'syl_1y':syl_1y};
      company = {'name':fullName, 'url':url, 'scale':scale,'fundNum':fundNum,'managerNum':managerNum,'fundDate':fundDate,
                 'nature':nature,'representative':representative,'netaddr':netaddr,'businessScope':businessScope}

    data = [json.dumps(fund, ensure_ascii=False),json.dumps(managers, ensure_ascii=False),json.dumps(company, ensure_ascii=False)];
    print(data)
    writer.writerow(data)
    fundFile.close()

#获取全部基金代码
def getAllCode():
    url = 'http://fund.eastmoney.com/js/fundcode_search.js'
    content = requests.get(url)
    jsContent = execjs.compile(content.text)
    rawData = jsContent.eval('r')
    allCode = []
    for code in rawData:
        allCode.append(code[0])
    return allCode

allCode = getAllCode()


for code in allCode:
    try:
        getWorth(code)
    except Exception:
        info = sys.exc_info()
        print(info)



