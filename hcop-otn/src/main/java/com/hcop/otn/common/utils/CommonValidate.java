package com.hcop.otn.common.utils;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CommonValidate {
    private final static String EMAIL_PATTERN             = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final static String MOBILE_PATTERN            = "^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$";
    private final static String TELEPHONE_PATTERN         = "^(\\d{2,5}-\\d{7,8}(-\\d{1,})?)|(13\\d{9})|(159\\d{8})$";                                                             // 带区号
    private final static String TELEPHONE_WITHOUT_PATTERN = "^[1-9]{1}[0-9]{5,8}$";                                                                    // 不带区号
    private final static String TELEPHONE                 = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$";                                    //手机
    private final static String NUMBER_PATTERN            = "[0-9]+";
    private final static String CHINESE_PATTERN           = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\s]+";
    private final static String POSITIVE_NUMBE_PATTERN   = "^[0-9]*[1-9][0-9]*$";
    private final static String PASSWORD_PATTERN          = "^[A-Za-z0-9]{6,15}$";                                                    //密码6到15位长度的数字和字母
    private final static String USER_NAME_PATTERN         = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{2,12}$";                       //验证用户名
    private final static String LOGIN_ID_PATTERN          = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]{2,20}$";                       //请输入2-20位数字、字母、下划线
    private final static String ID_CARD_PATTERN           = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[A-Z])$";                       //身份证
    private final static String QQ_PATTERN                 = "^[1-9]\\d{4,}$";                                             //QQ
    private final static String DOUBLE                    = "^-?[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
    private final static String GRID_NAME_PATTERN         = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\(\\)（）]{2,100}$";                       //验证网格名称
    private final static String SITE_PATTERN        = "^((([hH][tT][tT][pP][sS]?|[fF][tT][pP])\\:\\/\\/)?([\\w\\.\\-]+(\\:[\\w\\.\\&%\\$\\-]+)*@)?((([^\\s\\(\\)\\<\\>\\\\\\\"\\.\\[\\]\\,@;:]+)" +
            "(\\.[^\\s\\(\\)\\<\\>\\\\\\\"\\.\\[\\]\\,@;:]+)*(\\.[a-zA-Z]{2,4}))|((([01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d{1,2}|2[0-4]\\d|25[0-5])))" +
            "(\\b\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)\\b)?((\\/[^\\/][\\w\\.\\,\\?\\'\\\\\\/\\+&%\\$#\\=~_\\-@]*)*[^\\.\\,\\?\\\"\\'\\(\\)\\[\\]!;<>{}\\s\\x7F-\\xFF])?)$";//验证网址

    /**
     * 验证qq
     */
    public static boolean isQq(String qq) {
        Pattern pattern = Pattern.compile(QQ_PATTERN);
        return pattern.matcher(qq).matches();
    }

    /**
     * 验证qq
     */
    public static boolean isDouble(String d) {
        Pattern pattern = Pattern.compile(DOUBLE);
        return pattern.matcher(d).matches();
    }

    /**
     * 验证身份证
     */
    public static boolean isIdCard(String idCard) {
        Pattern pattern = Pattern.compile(ID_CARD_PATTERN);
        return pattern.matcher(idCard).matches();
    }

    /**
     * 验证用户姓名
     */
    public static boolean isUserName(String userName) {
        Pattern pattern = Pattern.compile(USER_NAME_PATTERN);
        return pattern.matcher(userName).matches();
    }

    /**
     * 验证登录名
     */
    public static boolean isLoginId(String loginId) {
        Pattern pattern = Pattern.compile(LOGIN_ID_PATTERN);
        return pattern.matcher(loginId).matches();
    }

    /**
     * 验证是否为中文字符
     */
    public static boolean isChineseCharacters(String characters) {
        Pattern pattern = Pattern.compile(CHINESE_PATTERN);
        return pattern.matcher(characters).matches();
    }


    /**
     * 验证是否为电子邮件
     */
    public static boolean isEmail(String email) {
        Matcher matcher = Pattern.compile(EMAIL_PATTERN).matcher(email);
        return matcher.matches();
    }

    /**
     * 验证网址
     * @param site
     * @return
     */
    public static boolean isSite(String site) {
        Matcher matcher = Pattern.compile(SITE_PATTERN).matcher(site);
        return matcher.matches();
    }

    /**
     * 验证单个手机
     */
    public static boolean isphon(String phons) {
        Matcher matcher = Pattern.compile(TELEPHONE).matcher(phons);
        return matcher.matches();
    }

    /**
     * 验证多个手机
     */
    public static boolean isphons(String phons) {
        String[] array = phons.split(";");
        for(int i = 0 ; i < array.length ; i++){
            if(!isphon(array[i])){
                return false;
            }
        }
        return array.length > 0 ? true : false;
    }
    /**
     * 验证是否为移动电话
     */
    public static boolean isMobile(String mobile) {
        Matcher matcher = Pattern.compile(MOBILE_PATTERN).matcher(mobile);
        return matcher.matches();
    }

    /**
     * 验证是否为电话
     */
    public static boolean isPhone(String phone) {
        if(isEmpty(phone)) {
            return false;
        }
        Matcher matcher;
        boolean result;
        Pattern p1 = Pattern.compile(TELEPHONE_PATTERN); // 验证带区号的
        Pattern p2 = Pattern.compile(TELEPHONE_WITHOUT_PATTERN); // 验证没有区号的
        if (phone.length() > 9) {
            matcher = p1.matcher(phone);
            result = matcher.matches();
        } else {
            matcher = p2.matcher(phone);
            result = matcher.matches();
        }
        return result;
    }

    public static boolean isMobileOrPhone(String str) {
        return isMobile(str) || isPhone(str);
    }

    /**
     * 验证是否为数字
     */
    public static boolean isNumber(String number) {
        if (isEmpty(number)) {
            return false;
        }
        Matcher matcher = Pattern.compile(NUMBER_PATTERN).matcher(number);
        return matcher.matches();
    }

    /**
     * 验证网格名
     */
    public static boolean isGridName(String gridName) {
        Pattern pattern = Pattern.compile(GRID_NAME_PATTERN);
        return pattern.matcher(gridName).matches();
    }

    public static boolean isEmpty(Object obj) {
        if(obj == null) {
            return true;
        }
        if(obj instanceof String) {
            String str = (String) obj;
            return str.length() == 0;
        }
        if(obj instanceof Collection) {
            Collection collection = (Collection) obj;
            return collection.isEmpty();
        }
        if (obj instanceof Map) {
            Map map = (Map)obj;
            return map.isEmpty();
        }
        if(obj instanceof Object[]) {
            Object[] arr = (Object[]) obj;
            return arr.length == 0;
        }
        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 验证数据是否在某个区间
     */
    public static boolean isInRange(String data, int minLength, int maxLength) {
        if (data.length() < minLength || data.length() > maxLength) {
            return false;
        }
        return true;
    }

}
