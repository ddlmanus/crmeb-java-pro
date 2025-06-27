package com.zbkj.common.utils;

import cn.hutool.json.JSONUtil;
import com.zbkj.common.dto.ThirdLogin;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.util.Base64;

public class AESEncryptUtils {
    private final static String secret="9b0fe37da784d19ac31268829a5999b3";
    private final static String iv="fde66913e2f6ecb2b65631d86d3fc050";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    public static String encrypt(String secret, String iv, ThirdLogin user) throws Exception {
        byte[] keyBytes =  Hex.decodeHex(secret);
        byte[] ivBytes =  Hex.decodeHex(iv);// 用户信息
        String plainText = JSONUtil.toJsonStr(user);


        Key keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipherEncrypt = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] encrypted = cipherEncrypt.doFinal(plainText.getBytes("UTF-8"));
        String encodedEncrypted = Base64.getEncoder().encodeToString(encrypted);
        System.out.println("Encrypted: " + encodedEncrypted);
        return encodedEncrypted;
    }
 
    public static String decrypt(String secret, String iv,String encodedEncrypted) throws Exception {
        byte[] keyBytes =  Hex.decodeHex(secret);
        byte[] ivBytes =  Hex.decodeHex(iv);
        byte[] dataBytes =  Base64.getDecoder().decode(encodedEncrypted);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        Key keySpec = new SecretKeySpec(keyBytes, "AES");
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(ivBytes));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, params);

        byte[] result = cipher.doFinal(dataBytes);
        String resultStr = new String(result);
        return resultStr;
    }
 
    public static void main(String[] args) {
        try {
           ThirdLogin thirdLogin = new ThirdLogin();
           thirdLogin.setUserId(12);
           thirdLogin.setMobile("182027731111");
           thirdLogin.setRealName("182027731111");
            AESEncryptUtils.encrypt(secret,iv,thirdLogin);
            String str="6LIoU+7KC5ltUlCBmwQiDwfiIoaQNQvHJgwQllEUUDwedwj/yLEz1PR2RHxn6kiyPTTwNvz6F0JWGeAWCO5vUyNQAFmIIjPB2vQN7potWyucCKS78b+779l56COPSQ+N";
            String decrypt = AESEncryptUtils.decrypt(secret, iv, str);
            System.out.println(decrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}