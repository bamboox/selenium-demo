/*
import groovy.util.logging.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Wait


import java.util.concurrent.TimeUnit
import java.util.function.Function

@Log
class Test {


    static PhantomJSDriver getPhantomJs() {
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", true);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        dcaps.setCapability("phantomjs.page.settings.XSSAuditingEnabled", true);
        dcaps.setCapability("phantomjs.page.settings.webSecurityEnabled", false);
        dcaps.setCapability("phantomjs.page.settings.localToRemoteUrlAccessEnabled", true);
        dcaps.setCapability("phantomjs.page.settings.XSSAuditingEnabled", true);
        dcaps.setCapability("phantomjs.page.settings.XSSAuditingEnabled", true);

        dcaps.setCapability("phantomjs.page.settings.loadImages", false);
        //js支持
        dcaps.setJavascriptEnabled(true);
        dcaps.setCapability("ignoreProtectedModeSettings", true);
        //驱动支持（第二参数表明的是你的phantomjs引擎所在的路径，which/whereis phantomjs可以查看）
        // fixme 这里写了执行， 可以考虑判断系统是否有安装，并获取对应的路径 or 开放出来指定路径
//        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/usr/local/bin/phantomjs");
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs.exe");
        //创建无界面浏览器对象

        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(120, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().deleteAllCookies();
        driver.manage().window().setSize(new Dimension(1920, 1080));

        return new PhantomJSDriver(dcaps);
    }
    */
/**
     * 打开谷歌浏览器，返回一个WebDriver，对浏览器的操作通过webDriver来执行
     *
     * @param url
     * @return
     *//*

    static WebDriver getChromeDriver() {
        //设置谷歌浏览器驱动，我放在项目的路径下，这个驱动可以帮你打开本地的谷歌浏览器
        System.setProperty("webdriver.chrome.driver", "chromedriver.2.42.exe");
        // 设置对谷歌浏览器的初始配置  开始
        HashMap<String, Object> prefs = new HashMap<String, Object>();
        //设置禁止图片
        //prefs.put("profile.managed_default_content_settings.images", 2);
        //设置禁止cookies
        //prefs.put("profile.default_content_settings.cookies", 2);
        ChromeOptions options = new ChromeOptions();
//        options.addArguments('headless')
        options.setExperimentalOption("prefs", prefs);
        DesiredCapabilities chromeCaps = DesiredCapabilities.chrome();
        chromeCaps.setCapability(ChromeOptions.CAPABILITY, options);
        // 设置对谷歌浏览器的初始配置           结束
        //新建一个谷歌浏览器对象（driver）
        return new ChromeDriver(chromeCaps);

    }

    static WebElement getWebElement(Wait<WebDriver> wait, String name) {
        WebElement webElement = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver dr) {
                return dr.findElements(By.name(name))[1]
            }
        })
        if (!webElement.isDisplayed()) {
            println("[error] getWebElement ${name}")
        }
        return webElement
    }

    static WebElement getWebElementByClass(Wait<WebDriver> wait, String name) {
        WebElement webElement = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver dr) {
                return dr.findElements(By.className(name))[1]
            }
        })
        if (!webElement.isDisplayed()) {
            println("[error] getWebElement ${name}")
        }
        return webElement
    }

    static void login(WebDriver driver, Wait<WebDriver> wait, String usernameStr, String passwordStr) {
        WebElement username = getWebElement(wait, "username")
        username.clear()
        username.sendKeys(usernameStr)

        WebElement password = getWebElement(wait, "password")
        password.clear()
        password.sendKeys(passwordStr)

        WebElement login = getWebElementByClass(wait, "btn_submit")
        click(driver, login)
    }

    // task
    static void doTask(WebDriver driver, Wait<WebDriver> wait, String level, Map<String, Map<String, Object>> initRsMap, String key) {
        Map<String, Object> build = initRsMap.get(key)
        if (!initRsMap) {
            println("[ERROR] initRsMap is null")
        }
        // (["level": level, "href": href, "isUpgrade": isUpgrade] as Map)
        if ((!build.href) || !(build.isUpgrade as Boolean) || (level && Integer.valueOf(build.level as String) >= Integer.valueOf(level))) {
            println("[ERROR] build: ${build}")
            return;
        }

        // js
        String js = "mask.loadInfo('$build.href', {})" as String
        ((JavascriptExecutor) driver).executeScript(js)
        Thread.sleep(1000 * 1) //显示等待页面加载完
        //切换弹框
        driver.switchTo().defaultContent();
        //Exception in thread "main" org.openqa.selenium.NoSuchElementException:
//          Closure<List<WebElement>> findElement={WebDriver d, Wait<WebDriver> w->
        List<WebElement> list = wait.until(new Function<WebDriver, List<WebElement>>() {
            List<WebElement> apply(WebDriver dr) {
                return dr.findElement(By.className("new_box_c_b")).findElements(By.tagName("a"))
            }
        })
        if (list.size() < 2) {
            println("[error] task: $build, is running")
        }
        click(driver, list.get(0))
        driver.switchTo().defaultContent()
        Thread.sleep(1000 * 2) //显示等待页面加载完
    }

    static void click(WebDriver driver, WebElement element) {
        try {
            element.click()
            Thread.sleep(1000 * 1) //显示等待页面加载完
        } catch (Exception e) {
            Thread.sleep(1000 * 2) //显示等待页面加载完
            println("[ERROR] click")
            driver.switchTo().defaultContent()
            click(driver, element)
        }

    }

    static void doInit(WebDriver driver, FluentWait<WebDriver> wait) {
        try {
            Thread.sleep(1000 * 2) //显示等待页面加载完
            driver.switchTo().defaultContent();
            // init
            WebElement initBtn = wait.until(new Function<WebDriver, WebElement>() {
                WebElement apply(WebDriver dr) {
                    return dr.findElement(By.className("lbAction"))
                }
            })
            click(driver, initBtn)
        } catch (Exception e) {
            println("[ERROR] doInit: ${e.message}")
            Thread.sleep(1000 * 3)
            doInit(driver, wait)
        }

    }

    static void switchUI(FluentWait<WebDriver> wait, String className) {
        try {
            Thread.sleep(1000 * 1) //显示等待页面加载完
            // init
            WebElement btn = wait.until(new Function<WebDriver, WebElement>() {
                WebElement apply(WebDriver dr) {
                    return dr.findElement(By.id(className))
                }
            })
            btn.click()
            Thread.sleep(1000 * 1) //显示等待页面加载完
        } catch (Exception e) {
            println("[ERROR] switchUI: ${e.message}")
            switchUI(wait, className)
        }
    }

    static Map<String, Map<String, Object>> initRsMap(String pageHtml) {
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


    static Map<String, Map<String, Object>> initCityMap(String pageHtml) {
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

    static <T> T invoke(WebDriver driver, Closure<T> handler) {
        String pageHtml = driver.getPageSource()
        Object resourceMap = handler(pageHtml)
        while (!resourceMap) {
            Thread.sleep(1000 * 1)
            pageHtml = driver.getPageSource()
            resourceMap = handler(pageHtml)
            if (resourceMap) {
                break
            }
        }
        return resourceMap

    }


    public static void main(String[] args) {
        WebDriver driver = getChromeDriver()
//        WebDriver driver = getPhantomJs()
        driver.get("http://wz59.sg.9wee.com/index.php?p=NoData");
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(30, TimeUnit.SECONDS)
                .pollingEvery(5, TimeUnit.SECONDS)
//                .ignoring(NoSuchElementException.class)
        //登录
        login(driver, wait, "bamboo666", "wbb123")

        if (driver.getPageSource().contains("密码错误")) {
            println("[error] 密码错误")
        }
        //初始化
        doInit(driver, wait)

        List<String> resourceList = (["0:0", "1:0", "2:0", "3:0", "4:0",
                                      "0:1", "1:1", "2:1", "3:1", "4:1",
                                      "0:2", "1:2", "2:2", "3:2", "4:2",
                                      "0:3", "1:3", "2:3", "3:3", "4:3",
                                      "3:4", "4:4",
                                      "3:5"] as List<String>)

        List<String> cityList = (["建造司",
                                  "仓库", "校场", "城墙",
                                  "粮仓", "将军府", "驿馆",
                                  "地窖", "步兵营", "行宫",
                                  "集市", "白虎节堂", "皇宫",
                                  "兵器司", "防具司", "商会", "骑兵营", "攻城武器营",
                                  "内政厅"] as List<String>)
        cityList.remove("行宫")
        while (true) {
            String level = "3"
            // init city_build_resource_a
            switchUI(wait, "city_build_resource_a")
            Map<String, Map<String, Object>> resourceMap = invoke(driver, Handler.initRsMaphandler)

            for (def key : resourceList) {
                doTask(driver, wait, level, resourceMap, key)
            }
            // init city_build_building_a
            switchUI(wait, "city_build_building_a")

            Map<String, Map<String, Object>> cityMap = invoke(driver, Handler.initCityMaphandler)

            for (def cityKey : cityList) {
                doTask(driver, wait, level, cityMap, cityKey)
            }

            Thread.sleep(1000 * 60 * 5)
        }


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

}*/
