import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class KeepSessionWithOneHttpclient {
    static HttpClient httpClient
    static int socketTimeout = 30000
    static int connectTimeout = 30000
    static String apiBase


    static String httpGet(String url, Map<String, String> headerMap = null) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build()
        def httpGet = new HttpGet(url)
        if (headerMap) {
            headerMap.each { k, v ->
                httpGet.setHeader(k, v)
            }
        }


        httpGet.setConfig(requestConfig)
        def response = httpClient.execute(httpGet)
        return EntityUtils.toString(response.getEntity(), "UTF-8")
    }

    static String httpPost(String url, Map<String, String> params, Map<String, String> headers = null) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build()
        def httpPost = new HttpPost(url)
        httpPost.setConfig(requestConfig)

        if (headers) {
            headers.each { k, v ->
                httpPost.addHeader(k, v)
            }
        }

        List<NameValuePair> loginParams = new ArrayList<NameValuePair>()
        params.each { k, v ->
            loginParams.add(new BasicNameValuePair(k, v));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(loginParams, "UTF-8");
        httpPost.setEntity(entity);
        def response = httpClient.execute(httpPost)
//        def statusCode = response.getStatusLine().getStatusCode();
//        if (statusCode == 302) {
//            String redirectUrl = response.getLastHeader("Location").getValue();
//            println(redirectUrl)
//        }
        return EntityUtils.toString(response.getEntity(), "UTF-8")
    }

    static void batchRegisterAndActive(String apiBase) {
        List<NameData> dataList = MakeData.makeData(1)
        Integer startCode = 1008
        for (int i = 0; i < dataList.size(); i++) {
            String username = "bamboo${startCode + i}"
            String password = "1qaz2wsx"
            String user_truename = dataList.get(i).getName()
            String user_idcard = dataList.get(i).getIdCardNumber()
            //batch register
            httpGet("http://sg.9wee.com/index.html")
            def retStr = httpGet("http://sg.9wee.com/reg/passport_check.php?act=user&username=${username}")
            if (retStr != "OK") {
                continue
            }
            retStr = httpPost("http://passport.9wee.com/register?q=&no_message=1&from=http%3A%2F%2Fsg.9wee.com%2Findex.html%3Fq%3D%26r_flag%3D1",
                    ['username'       : username
                     , 'password'     : password
                     , 'passwordcfm'  : password
                     , 'user_truename': user_truename
                     , 'user_idcard'  : user_idcard
                     , 'actual'       : '0'
                    ], ["User-Agent"               : "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.75 Safari/537.36",
                        "Host"                     : "passport.9wee.com",
                        "Referer"                  : "http://sg.9wee.com/reg/reg.html",
                        "Upgrade-Insecure-Requests": "1",
                        "Content-Type"             : "application/x-www-form-urlencoded",
                        "Accept"                   : "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"]
            )

            httpGet("${apiBase}/index.php?p=NoData")
            retStr = httpPost("https://passport.9wee.com/login", ['username': username, 'password': password])
            //login 成功
            retStr = httpGet("${apiBase}/index.php")
            if (retStr.contains("用户登录超时或者验")) {
                println("ERROR login")
                continue
            }

            /** 激活
             *
             *  c=1 2 3 国家
             *  user_nickname =
             *  area= 1
             *  g == 11  乔倾城
             *
             */
            httpGet("${apiBase}/reg.php?user_nickname=${username}&c=3&g=11&send=1&area=1")
            //姓名：丰莩,身份证号：12022319710121314X,帐号：bamboo1000 密码：1qaz2wsx
            println("姓名：${user_truename},身份证号：${user_idcard},帐号：${username} 密码：${password}");
        }
    }

    static void upgradeRs(Map<String, Map<String, Object>> resourceMap, String key, String level = "") {
        Map<String, Object> build = resourceMap[key]
        // (["level": level, "href": href, "isUpgrade": isUpgrade] as Map)
        if ((!build.href) || !(build.isUpgrade as Boolean) || (level && Integer.valueOf(build.level as String) >= Integer.valueOf(level))) {
            println("[ERROR] build: ${build}")
            return
        }
        def params = (build.href as String).split("\\?")[1]
        def retStr = httpPost("${apiBase}/modules/build/build.php?${params}", ["ajaxId": "_${System.currentTimeMillis()}", "act": "d", "type": "e", "cache": "false", "r": "${System.currentTimeMillis()}", "_": ""])
        if (retStr.contains("upgradeStart")) {
            println("[INFO] ${key} upgradeStart")
            String pid = retStr.substring(retStr.indexOf("upgradeStart"))
            pid = pid.substring(pid.indexOf("(") + 1, pid.indexOf(", "))
            def url = "${apiBase}/modules/gateway.php?ajaxId=sj&act=upgrade_building&type=o&cache=false&pid=${pid}&r=${System.currentTimeMillis()}"
            retStr = httpGet(url)
            println(retStr)
        }
        if (retStr.contains("buildStart")) {
            ///modules/build/build_building.php?bid=9&amp;pid=0
            println("[INFO] ${key} buildStart")
            String pid = retStr.substring(retStr.indexOf("buildStart"))
            pid = pid.substring(pid.indexOf("(") + 1, pid.indexOf(", this"))
            pid = pid.replace(", ", "&pid=")
            def url = "${apiBase}/modules/build/build_building.php?ajaxId=jz&act=build_building&type=e&cache=false&bid=${pid}&r=${System.currentTimeMillis()}"
            retStr = httpGet(url)
        }
    }

    public static void main(String[] args) {
        httpClient = HttpClientBuilder.create().build()
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(10)

        String domain = "wz60.sg.9wee.com"
        apiBase = "http://${domain}"
        String username = "bamboo1001"
        String password = "1qaz2wsx"

        httpGet("${apiBase}/index.php?p=NoData")
        def retStr = httpPost("https://passport.9wee.com/login", ['username': username, 'password': password])
        if (retStr.contains("用户登录超时或者验证")) {
            println("ERROR login")
            return
        }
        retStr = httpGet("${apiBase}/index.php")
        retStr = httpPost("${apiBase}/modules/gateway.php", ['ajaxId': 'city_build_resource',
                                                             'act'   : 'city_build_resource',
                                                             'type'  : 'e',
                                                             '_'     : '',
                                                             'r'     : System.currentTimeMillis().toString()])

        def rsHandler = Handler.initRsMaphandler(retStr)

        //
        retStr = httpGet("${apiBase}/modules/gateway.php?ajaxId=city_build_building&act=city_build_building&type=e&cache=false&r=${System.currentTimeMillis()}")
        def cityHandler = Handler.initCityMaphandler(retStr)

        //异步执行保持会话
        exec.scheduleAtFixedRate({
            httpGet("${apiBase}/modules/gateway.php?ajaxId=_${System.currentTimeMillis()}&act=get_queue&type=o&cache=false&r=${System.currentTimeMillis()}")
            httpGet("${apiBase}/modules/gateway.php?ajaxId=_${System.currentTimeMillis()}&act=set_city_resource&type=o&cache=false&r=${System.currentTimeMillis()}")
        }, 3, 55, TimeUnit.SECONDS)

        List<String> resourceList = (["0:0", "1:0", "2:0", "3:0", "4:0",
                                      "0:1", "1:1", "2:1", "3:1", "4:1",
                                      "0:2", "1:2", "2:2", "3:2", "4:2",
                                      "0:3", "1:3", "2:3", "3:3", "4:3",
                                      "3:4", "4:4",
                                      "3:5"] as List<String>)
        //
        // key 3:0 -> 1
        // key 2:0 -> 1
        // key 1:0 -> 1
        // key 0:0 -> 1

        //收藏本页
        //http://wz60.sg.9wee.com/modules/new_task.php?action=task1&type=OKbuyHF

        String level = "2"
//        for (def key : resourceList) {
//            Map<String, Object> build = rsHandler[key]
        // (["level": level, "href": href, "isUpgrade": isUpgrade] as Map)
        upgradeRs(rsHandler, "3:0", "2")
        upgradeRs(rsHandler, "2:0", "2")
        upgradeRs(rsHandler, "1:0", "2")
        upgradeRs(rsHandler, "0:0", "2")
        upgradeRs(cityHandler, "建造司", "2")
        upgradeRs(cityHandler, "仓库", "2")
        upgradeRs(cityHandler, "粮仓", "2")
        upgradeRs(cityHandler, "校场", "2")
        upgradeRs(cityHandler, "城墙", "2")

        //upgrade 等级 http://wz60.sg.9wee.com/modules/military/military_general.php
        httpPost("${apiBase}/modules/military/military_general.php", ["ajaxId"  : "_${System.currentTimeMillis()}"
                                                                      , "act"   : "military_general"
                                                                      , "type"  : "e"
                                                                      , "cache" : "false"
                                                                      , "t"     : "3"
                                                                      , "action": "rank"
                                                                      , "r"     : "${System.currentTimeMillis()}"
                                                                      , "_"     : ""])

        //upgrade 政务 http://wz60.sg.9wee.com/modules/military/military_general.php
        httpPost("${apiBase}/modules/military/military_general.php", ["ajaxId" : "_${System.currentTimeMillis()}"
                                                                      , "act"  : "military_general"
                                                                      , "type" : "e"
                                                                      , "cache": "update"
                                                                      , "k"    : "2"
                                                                      , "p"    : "1"
                                                                      , "r"    : "${System.currentTimeMillis()}"
                                                                      , "_"    : ""])
        //task
        httpGet("http://wz60.sg.9wee.com/modules/gateway.php?module=task&ajaxId=_${System.currentTimeMillis()}&act=set_choice&type=e&cache=false&t_id=6&choice=%5B%221%22%2C%20%221%22%2C%20%221%22%5D&r=${System.currentTimeMillis()}")

//        }
        // press any key to exit
        System.in.read()
    }
}


class Handler {
    public static Closure initCityMaphandler = {
        String pageHtml ->
            Map<String, Map<String, Object>> cityMap = ([:] as Map)
            Document doc = Jsoup.parse(pageHtml, "UTF-8")
            Elements rxs = doc.getElementsByClass("sg_jza_bg");
            for (int i = 0; i < rxs.size(); i++) {
                Element rx = rxs.get(i)
                String level = rx.getElementsByClass("sg_jza_lv").text()
                Document msg = Jsoup.parse(rx.select('img.TS').first().attr("msg"), "UTF-8")
                String title = msg.getElementsByClass("sg_jz_a_title").text()
                title = title.substring(0, title.indexOf(" (")).trim().replace("蜀国", "")
                        .replace("吴国", "")
                        .replace("魏国", "")
                Elements js = rx.getElementsByClass("sg_jza_j").select("a[href]")
                boolean isUpgrade = js.size() == 0 ? false : true
                String href = isUpgrade ? js.first().attr("href") : "#"
                cityMap[title] = (["level": level, "href": href, "isUpgrade": isUpgrade] as Map)
            }
            return cityMap
    }

    public static Closure initRsMaphandler = {
        String pageHtml ->
            Map<String, Map<String, Object>> resourceMap = ([:] as Map)
            Document doc = Jsoup.parse(pageHtml, "UTF-8")
            Elements rxs = doc.getElementsByClass("sg_jza_box");
            rxs.addAll(doc.getElementsByClass("sg_jza_boxend"))
            for (int i = 0; i < rxs.size(); i++) {
                Element rx = rxs.get(i)
                Elements rys = rx.getElementsByClass("sg_jza_bg");
                for (int j = 0; j < rys.size(); j++) {
                    Element ry = rys.get(j)
                    String level = ry.getElementsByClass("sg_jza_lv").text()
                    Elements js = ry.getElementsByClass("sg_jza_j").select("a[href]")
                    boolean isUpgrade = js.size() == 0 ? false : true
                    String href = isUpgrade ? js.first().attr("href") : "#"
                    resourceMap["${i}:${j}" as String] = (["level": level, "href": href, "isUpgrade": isUpgrade] as Map)
                }
            }
            return resourceMap
    }

}
