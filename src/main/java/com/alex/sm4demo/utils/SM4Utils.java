package com.alex.sm4demo.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * Created by Alex on 2022/3/25 17:19
 */
public class SM4Utils {
    //解密
    public String decryptStr(String encryptHex){
        if(encryptHex == "K"||"K".equals(encryptHex)){
            return "K";
        }
        String key = "1234567890123456";
        SymmetricCrypto sm4 = SmUtil.sm4(key.getBytes());

        String decryptStr = sm4.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);

        return decryptStr;
    }
    //加密
    public String encryptHex(String content){
        if(content == null || content.length() <= 0){
            return "K";
        }
        String key = "1234567890123456";
        SymmetricCrypto sm4 = SmUtil.sm4(key.getBytes());

        String encryptHex = sm4.encryptHex(content);

        return encryptHex;
    }
}
