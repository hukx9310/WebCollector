package com.hukx.webcollect.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hkx on 17-6-8.
 */

public class UrlUtil {

    public static final Pattern[] ICON_PATTERNS = new Pattern[] {
            Pattern.compile( "rel=[\"']shortcut icon[\"'][^\r\n>]+?((?<=href=[\"'])[^\"']+?(?=[\"']))", Pattern.CASE_INSENSITIVE),
            Pattern.compile( "((?<=href=[\"'])[^\"']+?(?=[\"']))[^\r\n<]+?rel=[\"']shortcut icon[\"']", Pattern.CASE_INSENSITIVE),
    };

    public static Pattern VALID_URL_PATTERN = Pattern.compile("^(https?://)?(\\w+\\.)+(top|com|cn|org|net|cc|gov|int|mil|tel|cd|travel|hk|tw|us|uk|mo|uk|asia)(/[\\S]+)*/?$");

    public static boolean isValidUrlPattern(String url){
        return VALID_URL_PATTERN.matcher(url).matches();
    }

    public static String getHead(String urlString){
        HttpURLConnection conn = null;
        try {
            URL Url = new URL(urlString);
            conn = (HttpURLConnection) Url.openConnection();
            String contentType = conn.getContentType();
            conn.setConnectTimeout(10000);
            String charset = "UTF-8";
            if(contentType != null) {
                int charsetIndex = contentType.indexOf("charset=");
                if(charsetIndex >= 0){
                    charset = contentType.substring(charsetIndex + 8);
                    if(charset.contains(";")){
                        charset = charset.substring(0, charset.indexOf(";"));
                    }
                    charset = charset.trim();
                }
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),charset));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line=reader.readLine())!=null) {
                sb.append(line);
                if(line.contains("</head>")){
                    break;
                }
            }
            int headrStart = sb.indexOf("<head>");
            int headrEnd = sb.indexOf("</head>");
            if(headrStart < 0 || headrEnd < 0){
                return null;
            }
            return sb.substring(headrStart + 6, headrEnd);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }
        return null;
    }

    public static String getHeadFromReponse(String response){
        try {
            return response.substring(response.indexOf("<head>") + 6, response.indexOf("</head>"));
        } catch (Exception e){
            return null;
        }

    }

    public static String getIconUrlString(String targetUrl, String headerString){
        URL url = null;
        String iconUrl = null;

        try {
            for (Pattern iconPattern : ICON_PATTERNS) {
                Matcher matcher = iconPattern.matcher(headerString);

                if (matcher.find()) {
                    iconUrl = matcher.group(1);
                    if (iconUrl.startsWith("http")){
                        return iconUrl;
                    } else if (iconUrl.startsWith("//")){
                        url = new URL(targetUrl);
                        iconUrl = url.getProtocol() + ":" +iconUrl;
                        return iconUrl;
                    } else if(iconUrl.charAt(0) == '/'){
                        url = new URL(targetUrl);
                        iconUrl = url.getProtocol() + "://" + url.getHost() + iconUrl;
                        return iconUrl;
                    } else {
                        iconUrl = targetUrl + "/" + iconUrl;
                        return iconUrl;
                    }
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            url = new URL(targetUrl);
            iconUrl = url.getProtocol() + "://" + url.getHost() + "/favicon.ico";// 保证从域名根路径搜索
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        if(HttpURLConnection.HTTP_OK == getResponseCode(iconUrl)) {
            return iconUrl;
        }

        return null;
    }

    public static String getTitleFromHeader(String s){
        int startIndex = s.indexOf("<title>") + "<title>".length();
        int endIndex = s.indexOf("</title>");
        if(startIndex < 0 || endIndex <0) {
            return null;
        }
        String title = s.substring(startIndex, endIndex);
        return title;
    }

    public static int getResponseCode(String urlString) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(urlString).openConnection();
            return connection.getResponseCode();
        } catch (MalformedURLException e ) {
            e.printStackTrace();
            return 0;
        } catch (IOException e ) {
            e.printStackTrace();
            return 0;
        }
        finally {
            if ( connection != null )
                connection.disconnect();
        }
    }

    public static void test(){}

    //return a latest url that the target url moved to, or return origin url instead;
    public static String autoCompleteUrl(String targetUrl) {

        HttpURLConnection connection = null;
        int responCode = 0;
        String workingUrl;
        workingUrl = getRedirectUrl(targetUrl);
        if(StringUtil.isNotNull(workingUrl)){
            return workingUrl;
        }
        String autoCompUrl;
        if (targetUrl.startsWith("https://")){
            int length = targetUrl.length();
            if(length < 12){
                return targetUrl;
            }
            String s = targetUrl.substring(8,12);
            if(s.equals("www.")){
                return targetUrl;
            }
            autoCompUrl = targetUrl.substring(0,8) + "www." + targetUrl.substring(9, length);
            workingUrl = getRedirectUrl(autoCompUrl);
            if(StringUtil.isNotNull(workingUrl)){
                return workingUrl;
            }
        } else if(targetUrl.startsWith("http://")){
            int length = targetUrl.length();
            if(length < 11){
                return targetUrl;
            }
            String s = targetUrl.substring(7,11);
            if(s.equals("www.")){
                return targetUrl;
            }
            autoCompUrl = targetUrl.substring(0,8) + "www." + targetUrl.substring(8, length);
            workingUrl = getRedirectUrl(autoCompUrl);
            if(StringUtil.isNotNull(workingUrl)){
                return workingUrl;
            }
        } else if(targetUrl.startsWith("www.")){
            autoCompUrl = "https://" + targetUrl.trim();
            workingUrl = getRedirectUrl(autoCompUrl);
            if(StringUtil.isNotNull(workingUrl)){
                return workingUrl;
            }
            autoCompUrl = "http://" + targetUrl.trim();
            workingUrl = getRedirectUrl(autoCompUrl);
            if(StringUtil.isNotNull(workingUrl)){
                return workingUrl;
            }
        } else {
            autoCompUrl = "https://" + targetUrl.trim();
            workingUrl = getRedirectUrl(autoCompUrl);
            if(StringUtil.isNotNull(workingUrl)){
                return workingUrl;
            }
            autoCompUrl = "http://" + targetUrl.trim();
            workingUrl = getRedirectUrl(autoCompUrl);
            if(StringUtil.isNotNull(workingUrl)){
                return workingUrl;
            }
            autoCompUrl = "https://www." + targetUrl.trim();
            workingUrl = getRedirectUrl(autoCompUrl);
            if(StringUtil.isNotNull(workingUrl)){
                return workingUrl;
            }
            autoCompUrl = "http://www." + targetUrl.trim();
            workingUrl = getRedirectUrl(autoCompUrl);
            if(StringUtil.isNotNull(workingUrl)){
                return workingUrl;
            }
        }
        return null;
    }

    //in case of url redirect
    static public String getRedirectUrl(String targetUrl){
        HttpURLConnection connection = null;
        int responCode = 0;
        int redirectTimes = 0;
        String redirectUrl = null;
        while (redirectTimes < 10) {
            try {
                connection = (HttpURLConnection) new URL(targetUrl).openConnection();
                connection.setConnectTimeout(3000);
                responCode = connection.getResponseCode();
                if (HttpURLConnection.HTTP_OK == responCode) {
                    return targetUrl;
                } else if (HttpURLConnection.HTTP_MOVED_TEMP == responCode || HttpURLConnection.HTTP_MOVED_PERM == responCode) {
                    redirectUrl = connection.getHeaderField("Location");
                    if (StringUtil.isNotNull(redirectUrl)) {
                        targetUrl = redirectUrl;
                        redirectTimes = 1;
                    }
                } else {
                    return redirectUrl;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return redirectUrl;
            } catch (IOException e) {
                e.printStackTrace();
                return redirectUrl;
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
        }
        return null;
    }

    public static boolean isInernetAvailable(){
        return getResponseCode("https://www.baidu.com") != 0;
    }

}
