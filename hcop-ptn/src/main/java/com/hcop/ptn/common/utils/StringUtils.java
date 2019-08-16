package com.hcop.ptn.common.utils;

import java.util.*;

public abstract class StringUtils {
    private static final String FOLDER_SEPARATOR = "/";

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private static final String CURRENT_PATH = ".";

    private static final char EXTENSION_SEPARATOR = '.';

    public static final String DEFAULT_NUMBER_FORMAT = "%.2f%%";

    public static boolean isEmpty( String str) {
        return (str == null || "".equals(str));
    }

    public static boolean isTrimEmpty( String str) {
        return (str == null || "".equals(str.trim()));
    }

    public static boolean hasLength( String str) {
        return !isEmpty(str);
    }

    /**
     * 去掉空格
     * @param str
     * @return
     */
    public static boolean hasText( String str) {
        return !isTrimEmpty(str);
    }

    public static boolean startsWithIgnoreCase( String str,  String prefix) {
        return (str != null && prefix != null && str.length() >= prefix.length() &&
                str.regionMatches(true, 0, prefix, 0, prefix.length()));
    }

    public static boolean endsWithIgnoreCase( String str,  String suffix) {
        return (str != null && suffix != null && str.length() >= suffix.length() &&
                str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length()));
    }

    /**
     * 数字格式转换
     * @param format
     * @param number
     * @return
     */
    public static String getNumberFormatString(String format, Number number) {
        return String.format(format, number);
    }

    public static String getNumberFormatString(Number number) {
        return String.format(DEFAULT_NUMBER_FORMAT, number);
    }

    public static String nullSafeReplace(String inString, String oldPattern,  String newPattern) {
        if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
            return inString;
        }
        return inString.replaceAll(oldPattern, newPattern);
    }

    /**
     * 字符串按逗号拆分（拆分后数组会保留空字符串）
     * @param str
     * @return
     */
    public static String[] commaDelimitedListToStringArray(String str) {
        return delimitedListToStringArray(str, ",");
    }

    public static String[] delimitedListToStringArray(String str, String delimiter) {
        return delimitedListToStringArray(str, delimiter, (String) null);
    }

    /**
     * 字符串按分割符拆分,并去掉指定字符
     * @param str
     * @param delimiter
     * @param charsToDelete
     * @return
     */
    public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
        if(str == null) {
            return new String[0];
        } else if(delimiter == null) {
            return new String[]{str};
        } else {
            ArrayList result = new ArrayList();
            int pos;
            if("".equals(delimiter)) {
                for(pos = 0; pos < str.length(); ++pos) {
                    result.add(deleteAny(str.substring(pos, pos + 1), charsToDelete));
                }
            } else {
                int delPos;
                for(pos = 0; (delPos = str.indexOf(delimiter, pos)) != -1; pos = delPos + delimiter.length()) {
                    result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                }

                if(str.length() > 0 && pos <= str.length()) {
                    result.add(deleteAny(str.substring(pos), charsToDelete));
                }
            }

            return toStringArray((Collection)result);
        }
    }

    /**
     * 按分隔符拆分（结果数组中不包含空字符串）
     * @param str
     * @param delimiters
     * @return
     */
    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if(str == null) {
            return null;
        } else {
            StringTokenizer st = new StringTokenizer(str, delimiters);
            ArrayList tokens = new ArrayList();
            while(true) {
                String token;
                do {
                    if(!st.hasMoreTokens()) {
                        return toStringArray(tokens);
                    }
                    token = st.nextToken();
                    if(trimTokens) {
                        token = token.trim();
                    }
                } while(ignoreEmptyTokens && token.length() <= 0);

                tokens.add(token);
            }
        }
    }

    /**
     * 字符串按逗号分隔成set
     * @param str
     * @return
     */
    public static Set<String> commaDelimitedListToSet( String str) {
        Set<String> set = new LinkedHashSet<>();
        String[] tokens = commaDelimitedListToStringArray(str);
        for (String token : tokens) {
            set.add(token);
        }
        return set;
    }

    /**
     * 集合按指定分隔符合并成字符串
     * @param coll
     * @param delim
     * @param prefix
     * @param suffix
     * @return
     */
    public static String collectionToDelimitedString(
             Collection<?> coll, String delim, String prefix, String suffix) {

        if (CollectionUtils.isEmpty(coll)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    public static String collectionToDelimitedString( Collection<?> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    public static String collectionToCommaDelimitedString(Collection<?> coll) {
        return collectionToDelimitedString(coll, ",");
    }

    /**
     * 删除子字符串
     * @param inString
     * @param pattern
     * @return
     */
    public static String delete(String inString, String pattern) {
        return inString.replaceAll(pattern, "");
    }

    /**
     * 删除指定字符
     * @param inString
     * @param charsToDelete
     * @return
     */
    public static String deleteAny(String inString, String charsToDelete) {
        if(hasLength(inString) && hasLength(charsToDelete)) {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < inString.length(); ++i) {
                char c = inString.charAt(i);
                if(charsToDelete.indexOf(c) == -1) {
                    sb.append(c);
                }
            }

            return sb.toString();
        } else {
            return inString;
        }
    }

    /**
     * 转换成"'str'"
     * @param str
     * @return
     */
    
    public static String quote( String str) {
        return (str != null ? "'" + str + "'" : null);
    }

    
    public static Object quoteIfString( Object obj) {
        return (obj instanceof String ? quote((String) obj) : obj);
    }

    /**
     * 获取后缀名
     * @param qualifiedName
     * @return
     */
    public static String unqualify(String qualifiedName) {
        return unqualify(qualifiedName, '.');
    }

    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (!hasLength(str)) {
            return str;
        }

        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        }
        else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars, 0, chars.length);
    }

    /**
     * 从路径中获取文件名
     * @param path
     * @return
     */
    
    public static String getFilename( String path) {
        if (path == null) {
            return null;
        }

        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
    }

    /**
     * 获取文件扩展名
     * @param path
     * @return
     */
    
    public static String getFilenameExtension( String path) {
        if (path == null) {
            return null;
        }

        int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
        if (extIndex == -1) {
            return null;
        }

        int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (folderIndex > extIndex) {
            return null;
        }

        return path.substring(extIndex + 1);
    }

    public static String[] toStringArray(Collection<String> collection) {
        return collection == null?null:(String[])collection.toArray(new String[collection.size()]);
    }

    public static void main(String[] args) {
        String s = "123abc".replaceAll("12", "");
        String[] split = "123,abc".split(null);
        String[] strings = delimitedListToStringArray(",,a,2,3,", ",");
        String[] strings1 = tokenizeToStringArray(",,a,2,3,", ",");
    }
}
