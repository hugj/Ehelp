package com.ehelp.user.contactlist;

import com.ehelp.user.sort.HashList;
import com.ehelp.user.sort.KeySort;
import net.sourceforge.pinyin4j.PinyinHelper;

public class AssortPinyinList {
    private HashList<String,String> hashList=new HashList<String,String>(new KeySort<String,String>(){
        public String getKey(String value) {
            return getFirstChar(value);
        }});

    public  String getFirstChar(String value) {
        char firstChar = value.charAt(0);
        String first = null;
        //汉语转化拼音
        String[] print = PinyinHelper.toHanyuPinyinStringArray(firstChar);
        if (print == null) {
            //小写字母改为大写字母
            if ((firstChar >= 97 && firstChar <= 122)) {
                firstChar -= 32;
            }
            if (firstChar >= 65 && firstChar <= 90) {
                first = String.valueOf((char) firstChar);
            } else {
                //其它的
                first = "#";
            }
        } else {
            first = String.valueOf((char)(print[0].charAt(0) -32));
        }
        return first;
    }
    public HashList<String, String> getHashList() {
        return hashList;
    }
}
