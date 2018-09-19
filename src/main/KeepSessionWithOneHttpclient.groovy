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

class KeepSessionWithOneHttpclient {
    static HttpClient httpClient
    static int socketTimeout = 30000
    static int connectTimeout = 30000

    static String httpGet(String url) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build()
        def httpGet = new HttpGet(url)
        httpGet.setConfig(requestConfig)
        def response = httpClient.execute(httpGet)
        return EntityUtils.toString(response.getEntity(), "UTF-8")
    }

    static String httpGet(String url, Map<String, String> headerMap) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build()
        def httpGet = new HttpGet(url)
        headerMap.each { k, v ->
            httpGet.setHeader(k, v)

        }

        httpGet.setConfig(requestConfig)
        def response = httpClient.execute(httpGet)
        return EntityUtils.toString(response.getEntity(), "UTF-8")
    }

    static String httpPost(String url, Map<String, String> params) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build()
        def httpPost = new HttpPost(url)
        httpPost.setConfig(requestConfig)

        List<NameValuePair> loginParams = new ArrayList<NameValuePair>()
        params.each { k, v ->
            loginParams.add(new BasicNameValuePair(k, v));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(loginParams, "UTF-8");
        httpPost.setEntity(entity);
        def response = httpClient.execute(httpPost)
        /*def statusCode = loginResponse.getStatusLine().getStatusCode();
        if(statusCode == 302){
            String redirectUrl = loginResponse.getLastHeader("Location").getValue();
        }*/
        return EntityUtils.toString(response.getEntity(), "UTF-8")
    }

    public static void main(String[] args) {
        httpClient = HttpClientBuilder.create().build();

        httpGet("http://wz59.sg.9wee.com/index.php?p=NoData")

        def retStr = httpPost("https://passport.9wee.com/login", ['username': 'bamboo666', 'password': 'wbb123'])

        //login 成功
        retStr = httpGet("http://wz59.sg.9wee.com/index.php")

        //用户登录超时或者验 index.php?p=NoData
        //http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=_1537285654280&act=get_queue&type=o&cache=false&r=1537287859694

        // 左边状态栏
        // http://wz59.sg.9wee.com/main.php

        retStr = httpPost("http://wz59.sg.9wee.com/modules/gateway.php", ['ajaxId': 'city_build_resource',
                                                                          'act'   : 'city_build_resource',
                                                                          'type'  : 'e',
                                                                          '_'     : '',
                                                                          'r'     : System.currentTimeMillis().toString()])

        def rshandler = Handler.initRsMaphandler(retStr)
        print(rshandler)

        //http://wz59.sg.9wee.com/modules/build/build.php?rid=1
        // "/modules/build/build.php?rid=1"

//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=_${System.currentTimeMillis()}&act=get_queue&type=o&cache=false&r=${System.currentTimeMillis()}")
//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=_${System.currentTimeMillis()}&act=set_city_resource&type=o&cache=false&r=${System.currentTimeMillis()}")
//        retStr = httpPost("http://wz59.sg.9wee.com/modules/gateway.php", ["ajaxId": "updateUserLoginTime", "act": "updateUserLoginTime", "type": "o", "cache": "0", "cla": "Build_City", "operateType": "class", "r": "${System.currentTimeMillis()}", "_": ""])
//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=_${System.currentTimeMillis()}&act=resource_add&type=o&cache=false&r=${System.currentTimeMillis()}")
//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=city_relationship&act=city_relationship&type=e&cache=false&r=${System.currentTimeMillis()}")
//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=_${System.currentTimeMillis()}&act=get_money&type=o&cache=false&r=${System.currentTimeMillis()}")
//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?module=task&ajaxId=_${System.currentTimeMillis()}&act=check_task&type=o&cache=false&r=${System.currentTimeMillis()}")
//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=_${System.currentTimeMillis()}&act=set_city_resource&type=o&cache=false&r=${System.currentTimeMillis()}")
//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?module=task&ajaxId=_${System.currentTimeMillis()}&act=get_task_container&type=e&cache=0&r=${System.currentTimeMillis()}")
//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?module=task&ajaxId=_${System.currentTimeMillis()}&act=get_task&type=e&cache=false&task_id=14&task_level=1&r=${System.currentTimeMillis()}")
//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=_${System.currentTimeMillis()}&act=get_queue&type=o&cache=false&r=${System.currentTimeMillis()}")
//        retStr = httpGet("http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=_${System.currentTimeMillis()}&act=set_city_resource&type=o&cache=false&r=${System.currentTimeMillis()}")
//        retStr = httpPost("http://wz59.sg.9wee.com/modules/gateway.php", ["ajaxId": "city_build_resource", "act": "city_build_resource", "type": "e", "cache": "false", "r": "${System.currentTimeMillis()}", "": "_"])
//        retStr = httpPost("http://wz59.sg.9wee.com/modules/gateway.php?module=im", ["ajaxId": "_${System.currentTimeMillis()}", "act": "getLoginKey", "type": "e", "cache": "false", "nickname": "万邦波", "r": "${System.currentTimeMillis()}", "": "_"])

        List<String> resourceList = (["0:0", "1:0", "2:0", "3:0", "4:0",
                                      "0:1", "1:1", "2:1", "3:1", "4:1",
                                      "0:2", "1:2", "2:2", "3:2", "4:2",
                                      "0:3", "1:3", "2:3", "3:3", "4:3",
                                      "3:4", "4:4",
                                      "3:5"] as List<String>)
        String level = "5"
        for (def key : resourceList) {
            Map<String, Object> build = rshandler[key]

            // (["level": level, "href": href, "isUpgrade": isUpgrade] as Map)
            if ((!build.href) || !(build.isUpgrade as Boolean) || (level && Integer.valueOf(build.level as String) >= Integer.valueOf(level))) {
                println("[ERROR] build: ${build}")
                continue
            }
            def params = (build.href as String).split("\\?")[1]
            retStr = httpPost("http://wz59.sg.9wee.com/modules/build/build.php?${params}", ["ajaxId": "_${System.currentTimeMillis()}", "act": "d", "type": "e", "cache": "false", "r": "${System.currentTimeMillis()}", "_": ""])
            if (retStr.contains("upgradeStart")) {
                String pid = retStr.substring(retStr.indexOf("upgradeStart"))
                pid = pid.substring(pid.indexOf("(") + 1, pid.indexOf(", "))

                def map = (["User-Agent": "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.75 Safari/537.36"])
                def url = "http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=sj&act=upgrade_building&type=o&cache=false&pid=${pid}&r=${System.currentTimeMillis()}"
                print(url)
//                def url="http://wz59.sg.9wee.com/modules/gateway.php?ajaxId=sj&act=upgrade_building&type=o&cache=false&pid=13&r=1537291128903"
                retStr = httpGet(url, map)
                println(retStr)
            }
        }
        println(retStr)
    }
}


class Handler {
    public static Closure initCityMaphandler = {
        String pageHtml ->
            Map<String, Map<String, Object>> cityMap = ([:] as Map)
            Document doc = Jsoup.parse(pageHtml, "UTF-8")
            Elements rxs = doc.getElementsByClass("F");
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
