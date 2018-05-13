 package com.qunyi.modules.utils;

 import org.apache.log4j.Logger;
 import org.springframework.core.io.DefaultResourceLoader;
 import org.springframework.core.io.ResourceLoader;

 import javax.servlet.http.HttpServletRequest;
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.text.DateFormat;
 import java.text.DecimalFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.*;
 import java.util.regex.Pattern;

 import static java.util.regex.Pattern.*;


/**
 * @author liuqiuping
 * 工具管理类
 */

 /**
  * @author HouLi
  *
  */
 public  class CommonUtil {

     private static ResourceLoader resourceLoader = new DefaultResourceLoader();
     //日志
     private static Logger logger = Logger.getLogger(CommonUtil.class);
     /** 合同收支款计划类型: 1--收款；2--付款 */
     public static final Integer CONT_RECV_TYPE = 1;  //收款计划
     public static final Integer CONT_PAY_TYPE = 2;   //付款计划

     /* Start 通用工具类方法 */
     /** 判断日期是否为闰年 */
     public static boolean isLeapYear(Date date){
         GregorianCalendar cal = new GregorianCalendar();
         cal.setTime(date);
         int year = (int) cal.get(Calendar.YEAR);
         if((year%4 == 0)&&((year%100 != 0)|(year%400 == 0))) {
             return true;
         }
         return false;
     }



     /** 计算两个日期间总天数【afDate - bfDate】（日期格式：yyyy-MM-dd） */
     public static long getBetweenDateDays(Date bfDate, Date afDate){
         long gap = 0;
         if(bfDate == null || afDate == null) {
             return gap;
         }
         GregorianCalendar bfCal = new GregorianCalendar();
         GregorianCalendar afCal = new GregorianCalendar();
         bfCal.setTime(bfDate);
         afCal.setTime(afDate);
         gap = (afCal.getTimeInMillis() - bfCal.getTimeInMillis()) / (1000 * 3600 * 24);  //从间隔毫秒变成间隔天数
         return (gap + 1);
     }



     /** 获取加N天后的日期 */
     public static Date getAddDaysDate(Date date, int addDays){
         GregorianCalendar gc = new GregorianCalendar();
         gc.setTime(date);
         gc.add(5, addDays);  // 1--年份；2--月份；5--天；
         return gc.getTime();
     }












     /** 获取两个日期相差多少年多少月多少天
      * @return ret[0]--相差年；ret[1]--相差月；ret[2]--相差天 */
     public static int[] getNeturalAge(Calendar calendarBirth, Calendar calendarNow) {
         int diffYears = 0, diffMonths, diffDays;
         int dayOfBirth = calendarBirth.get(Calendar.DAY_OF_MONTH);
         int dayOfNow = calendarNow.get(Calendar.DAY_OF_MONTH);
         if (dayOfBirth <= dayOfNow) {
             diffMonths = getMonthsOfAge(calendarBirth, calendarNow);
             diffDays = dayOfNow - dayOfBirth;
             //if (diffMonths == 0) 	diffDays++;   //同月勿需添加天
         } else {
             if (isEndOfMonth(calendarBirth)) {
                 if (isEndOfMonth(calendarNow)) {
                     diffMonths = getMonthsOfAge(calendarBirth, calendarNow);
                     diffDays = 0;
                 } else {
                     calendarNow.add(Calendar.MONTH, -1);
                     diffMonths = getMonthsOfAge(calendarBirth, calendarNow);
                     diffDays = dayOfNow + 1;
                     diffDays = dayOfNow;
                 }
             } else {
                 if (isEndOfMonth(calendarNow)) {
                     diffMonths = getMonthsOfAge(calendarBirth, calendarNow);
                     diffDays = 0;
                 } else {
                     calendarNow.add(Calendar.MONTH, -1);// 上个月
                     diffMonths = getMonthsOfAge(calendarBirth, calendarNow);
                     // 获取上个月最大的一天
                     int maxDayOfLastMonth = calendarNow.getActualMaximum(Calendar.DAY_OF_MONTH);
                     if (maxDayOfLastMonth > dayOfBirth) {
                         diffDays = maxDayOfLastMonth - dayOfBirth + dayOfNow;
                     } else {
                         diffDays = dayOfNow;
                     }
                 }
             }
         }
         // 计算月份时，没有考虑年
         diffYears = diffMonths / 12;
         diffMonths = diffMonths % 12;
         return new int[] { diffYears, diffMonths, diffDays };
     }

     /** 获取两个日历的月份之差  */
     public static int getMonthsOfAge(Calendar calendarBirth, Calendar calendarNow) {
         return (calendarNow.get(Calendar.YEAR) - calendarBirth.get(Calendar.YEAR))* 12
                 + calendarNow.get(Calendar.MONTH)- calendarBirth.get(Calendar.MONTH);
     }

     /** 判断这一天是否是月底  */
     public static boolean isEndOfMonth(Calendar calendar) {
         int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
         if (dayOfMonth == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
             return true;
         }
         return false;
     }

     /** 判断对象是否为空 */
     @SuppressWarnings("rawtypes")
     public static boolean isObjNullOrEmp(Object obj){
         boolean ret = false;
         if(obj == null || "".equals(obj)){
             ret = true;
             return ret;
         }
         if (obj instanceof String) {
             if("".equals(obj.toString().trim()) || "null".equalsIgnoreCase(obj.toString().trim())){
                 ret = true;
             }
         } else if(obj instanceof Object[]) {
             if(((Object[]) obj).length <= 0){
                 ret = true;
             }
         } else if(obj instanceof Map) {
             if(((Map) obj).isEmpty()){
                 ret = true;
             }
         } else if(obj instanceof List) {
             if(((List) obj).isEmpty()){
                 ret = true;
             }
         }
         return ret;
     }


     /** 获取字符串值（对象转字符串，空处理） */
     public static String converToStr(Object obj){
         String ret = "";
         if(!isObjNullOrEmp(obj)){
             ret = obj.toString();
         }
         return ret;
     }

     /** 精确浮点数后小数位 */
     public static double fixDouble(double val, int fix) {
         if (val == 0) {
             return val;
         }
         int p = (int) Math.pow(10, fix);
         return (double) ((int) (val * p)) / p;
      }

     /** 获取租赁年限指定格式的数字 */
     public static String getTermDecimal(Double number, String format){
         if(isObjNullOrEmp(number)){
             return "";
         }
         DecimalFormat df = new DecimalFormat(format);
         return df.format(number);
     }

     /** 获取指定格式的数字 */
     public static String getDecimal(Double number, String format){
         if(isObjNullOrEmp(number)){
             return "0";
         }
         DecimalFormat df = new DecimalFormat(format);
         return df.format(number);
     }

     /** 获取指定格式的数字 */
     public static String getDecimalPlus(Double number, String format){
         if(isObjNullOrEmp(number)){
             return "";
         }
         DecimalFormat df = new DecimalFormat(format);
         return df.format(number);
     }

     /** 把数字转换成###,## 格式（支持自定义格式） */
     public static String getThousandVal(Double summj, String format){
         if(summj==null || summj.equals("")) {
             return null;
         }
         String temp="";
         //String value = getNokxjs(summj);
         String value = getDecimal(summj, format);
         String IntegerValue = "";
         String xsValue = "";
         if(value.indexOf(".")!=-1){
             IntegerValue = value.substring(0, value.indexOf("."));
             xsValue = value.substring(value.indexOf("."),value.length() );
         }else{
             IntegerValue = value;
         }
         int begin =0;
         if(IntegerValue.length()<=3){
             temp=temp+IntegerValue;
         }else{
             if(IntegerValue.length()%3!=0){
                 temp=temp+IntegerValue.substring(0, IntegerValue.length()%3)+",";
                 begin=IntegerValue.length()%3;
             }else{
                 temp=temp+IntegerValue.substring(0, 3)+",";
                 begin = 3;
             }
             for (int i = begin,j=0; i <IntegerValue.length(); i++,j++) {
                 if(j==3){
                     temp = temp+",";
                     j=0;
                 }
                 temp = temp +IntegerValue.charAt(i);
             }
         }
         if(!"".equals(xsValue)){
             temp+=xsValue;
         }
         return temp;
     }

     /** 把数字转换成###,##.##格式 */
     public static String getInteger(Double summj){
         if(summj==null || summj.equals("")) {
             return null;
         }
         String temp="";
         //String value = getNokxjs(summj);
         String value = getDecimal(summj, "0.00");
         String IntegerValue = "";
         String xsValue = "";
         if(value.indexOf(".")!=-1){
             IntegerValue = value.substring(0, value.indexOf("."));
             xsValue = value.substring(value.indexOf("."),value.length() );
         }else{
             IntegerValue = value;
         }
         int begin =0;
         if(IntegerValue.length()<=3){
             temp=temp+IntegerValue;
         }else{
             if(IntegerValue.length()%3!=0){
                 temp=temp+IntegerValue.substring(0, IntegerValue.length()%3)+",";
                 begin=IntegerValue.length()%3;
             }else{
                 temp=temp+IntegerValue.substring(0, 3)+",";
                 begin = 3;
             }
             for (int i = begin,j=0; i <IntegerValue.length(); i++,j++) {
                 if(j==3){
                     temp = temp+",";
                     j=0;
                 }
                 temp = temp +IntegerValue.charAt(i);
             }
         }
         if(!"".equals(xsValue)){
             temp+=xsValue;
         }
         return temp;
     }

     /** 去掉浮点对象的科学计数法 */
     public static String getNokxjs(Double db){
         DecimalFormat format = new DecimalFormat("0.################################################################################");
         String str="";
         if(db!=null){
             str=""+format.format(db);
         }
         return str;
     }

     /**
      * 把字符串转化为整数（如果为非数字字符串时返回值为0）
      * @Method parseInt
      * @param  @param value【需要转换的字符串】
      * @param  @return
      * @return int【如果为非数字字符串时返回值为0】
      * @throws
      */
     public static int parseInt(String value){
         int num=0;
         if(value!=null && !"".equals(value.trim())){
             try{
                 num=Integer.parseInt(value);
             }catch(Exception e){
                 e.printStackTrace();
             }
         }
         return num;
     }

     /**
      * 把字符转化为双精度的浮点小数（如果为非数字字符串时返回值为0）
      * @Method parseDouble
      * @param  @param value【需要转换的字符串】
      * @param  @return
      * @return double
      */
     public static double parseDouble(String value){
         double num=0;
         if(value!=null && !"".equals(value.trim())){
             try{
                 num=Double.parseDouble(value);
             }catch(Exception e){
                 e.printStackTrace();
             }
         }
         return num;
     }

     /**
      * 把字符转化为双精度的浮点小数（如果为非数字字符串时返回值为0）
      * @Method parseDouble
      * @param  @param value【需要转换的字符串】
      * @param  @return
      * @return double
      * @throws
      */
     public static double parseDoubleVal(Double value){
         double num=0.00;
         if(value!=null){
             num = value.doubleValue();
         }
         return num;
     }

     /**
      * 把浮点对象四舍五入保留2位小数并转换成浮点数
      * @Method round
      * @param  @param db【需要保留2位小数的浮点对象】
      * @param  @return
      * @return double
      * @throws
      */
     public static double round(Double db){
         double str=0;
         if(db!=null){
             str=(Math.round(db*100)/100.0);
         }
         return str;
     }

     /** 排除字符串中重复的项目 */
     public static String filterRepeatItem(String str){
         String ret = "";
         if(!isObjNullOrEmp(str)){
             if(str.indexOf(",") == -1) {
                 return str;
             }
             String[] strSplit = str.split(",");
             if(!isObjNullOrEmp(strSplit)){
                 Set<String> set = new HashSet<String>();
                 for(String item : strSplit)  { set.add(item); }
                 Iterator<String> it = set.iterator();
                 while(it.hasNext()){
                     ret += it.next() + ",";
                 }
                 if(!isObjNullOrEmp(ret))  { ret = ret.substring(0, ret.length() - 1); }
             }
         }
         return ret;
     }





     /* Start Excel 校验规则方法 */
     /** 如果存在值：是否为正浮点数 */
     public static boolean isExcelCellNull(String cellvalue) {
         if(!isObjNullOrEmp(cellvalue)) {
             return false;
         }
         return true;
     }

     /** 如果存在值：是否为正浮点数 */
     public static boolean isPositiveNumber(Double number) {
         if(number < 0) {
             return false;
         }
         return true;
     }

     /** 如果存在浮点数值：长度校验：整数部分不超过 6/10/12 位，小数部分不超过 2/3/4 位 */
     public static boolean chkNumberLength(Double number, int intLength, int decimalLength) {
         String numberStr = getDecimal(number, "0.######");
         if(isObjNullOrEmp(numberStr)) {
             return true;
         }
         String[] numberSplit =  numberStr.split("\\.");
         if(numberSplit != null && numberSplit.length > 0){
             String intPart = numberSplit[0];  //整数部分
             String decimalPart = null;
             if(numberSplit.length > 1)  { decimalPart = numberSplit[1]; }  //小数部分
             if(intPart != null){
                 if(intPart.indexOf("+") != -1 || intPart.indexOf("-") != -1){
                     //存在符号情况
                     if(intPart.length() - 1 > intLength)  return false;
                 }else{
                     if(intPart.length() > intLength)  return false;
                 }
             }
             if(decimalPart != null && decimalPart.length() > decimalLength) {
                 return false;
             }
         }
         return true;
     }

     /** 如果存在值：是否为正整数 */
     public static boolean isPositiveInteger(Integer integer) {
         if(integer < 0) {
             return false;
         }
         return true;
     }

     /** 如果存在整数值：长度校验：长度不超过 3 位 */
     public static boolean chkIntegerLength(Integer integer, int intLength) {
         String integerStr = String.valueOf(integer);
         if(isObjNullOrEmp(integerStr)) {
             return true;
         }
         if(integerStr.indexOf("+") != -1 || integerStr.indexOf("-") != -1){
             //存在符号情况
             if(integerStr.length() - 1 > intLength)  return false;
         }else{
             if(integerStr.length() > intLength)  return false;
         }
         return true;
     }

     /** 如果【合同开始日期】和【合同结束日期】都存在值：是否早于【合同开始日期】 */
     public static boolean chkDateRange(String startDate, String endDate) {
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
         try {
             Date startdate = df.parse(startDate);
             Date enddate = df.parse(endDate);
             if (startdate.getTime() > enddate.getTime()) {
                 return false;
             }
         } catch (ParseException e) {
             e.printStackTrace();
         }
         return true;
     }
     /* End */





     /**
      * 获取起始租赁价格单位
      * @param tradekind 付款方式
      * @return
      * @author 颜刚  2014/5/8
      */
     public static String getLeasespriceUnit(int tradekind){
         if(tradekind==1){
             return " 元";
         }else if(tradekind==2){
             return " 元/年";
         }else if(tradekind==3){
             return " 元/半年";
         }else if(tradekind==4){
             return " 元/季";
         }else if(tradekind==5){
             return " 元/月";
         }
         return "";
     }


     /** 页面空值显示状态 */
     public static Object convertUINull(Object value, String nullStr){
         return isObjNullOrEmp(value) ? nullStr : value;
     }

     /** 页面空值显示状态（数值） */
     public static String convertUINumNull(Double number, String format, String nullStr){
         return isObjNullOrEmp(number) ? nullStr : getDecimal(number, format);
     }

     /**将毫秒数换算成x天x时x分x秒x毫秒 */
     public static String formatMsTime(long ms) {
         int ss = 1000;
         int mi = ss * 60;
         int hh = mi * 60;
         int dd = hh * 24;
         long day = ms / dd;
         long hour = (ms - day * dd) / hh;
         long minute = (ms - day * dd - hour * hh) / mi;
         long second = (ms - day * dd - hour * hh - minute * mi) / ss;
         long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
         String strDay = day < 10 ? "0" + day : "" + day;
         String strHour = hour < 10 ? "0" + hour : "" + hour;
         String strMinute = minute < 10 ? "0" + minute : "" + minute;
         String strSecond = second < 10 ? "0" + second : "" + second;
         String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;
         strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
         return strDay + " " + strHour + ":" + strMinute + ":" + strSecond + " " + strMilliSecond;
     }

     /** 将字符串数组拼接成 Insql括号内容 */
     public static String spellInsql(List<String> strlist){
         String ret = null;
         if(!isObjNullOrEmp(strlist)){
             ret = strlist.toString();
             ret = "'" + ret.substring(1, ret.length() - 1).replace(", ", "', '") + "'";
         }else{
             ret = "''";
         }
         return ret;
     }

     /** 计算两个日期间总天数【endTime - startTime】（日期格式：yyyy-MM-dd HH:mm:ss） */
     public static Long dateDiff(String startTime, String endTime, String format, String str) {
         // 按照传入的格式生成一个simpledateformate对象
         SimpleDateFormat sd = new SimpleDateFormat(format);
         long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
         long nh = 1000 * 60 * 60;// 一小时的毫秒数
         long nm = 1000 * 60;// 一分钟的毫秒数
         long ns = 1000;// 一秒钟的毫秒数
         long diff;
         long day = 0;
         long hour = 0;
         long min = 0;
         long sec = 0;
         // 获得两个时间的毫秒时间差异
         try {
             diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
             day = diff / nd;// 计算差多少天
             hour = diff % nd / nh + day * 24;// 计算差多少小时
             min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
             sec = diff % nd % nh % nm / ns;// 计算差多少秒
             // 输出结果
             System.out.println("时间相差：" + day + "天" + (hour - day * 24) + "小时"
                     + (min - day * 24 * 60) + "分钟" + sec + "秒。");
             System.out.println("hour=" + hour + ",min=" + min);
             if (str.equalsIgnoreCase("h")) {
                 return hour;
             } else {
                 return min;
             }
         } catch (ParseException e) {
             e.printStackTrace();
         }
         if (str.equalsIgnoreCase("h")){
             return hour;
         } else {
             return min;
         }
     }

     /**
      *
      //* @param total 递增值
      //* @param type 递增方式
      * @return 递增比例
      * @author 司大江 2016/2/26
      */
     public static float getincrpercent(String incrementval,String pricestr)
     {
         float incrementpercent=Float.valueOf(incrementval);
         float price=Float.valueOf(pricestr);
         if (incrementpercent<1)
         {
             return incrementpercent;
         }
         return Math.round(incrementpercent/price*10000)/100;
     }


     /**
      * 获取付款方式字符串
      * @param tradekind 付款方式
      * @return
      * @author 司大江  2016/2/26
      */
     public static String getpaytypestr(int tradekind){
         if(tradekind==1){
             return " 天";
         }else if(tradekind==2){
             return " 年";
         }else if(tradekind==3){
             return " 半年";
         }else if(tradekind==4){
             return " 季";
         }else if(tradekind==5){
             return " 月";
         }
         return "";
     }


     /**
      * 将java.util.Date 格式转换为字符串格式'yyyy-MM-dd HH:mm:ss'(24小时制)
      * format 为null时，默认值为yyyy-MM-dd HH:mm:ss
     // * @param Time     date
      * @param format  日期格式：yyyy-MM-dd HH:mm:ss
      * @return String
      * @throws ParseException
      */
     public static  String convertDate( Date time, String format) throws ParseException{
         if(isObjNullOrEmp(time)){
             return null;
         }
         if("".equals(format)|null==format){
             format = "yyyy-MM-dd HH:mm:ss";
         }
          SimpleDateFormat formatter;
             formatter = new SimpleDateFormat (format);
             String ctime = formatter.format(time);

        return  ctime;

     }

     /**
      *
      * 获取指定格式日期的方法；默认格式"yyyy-MM-dd HH:mm:ss"
      //* @param format
      * @return
      */
     public static String  getDate(){
         Date time = new Date();
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

         String ctime = sdf.format(time);

        return  ctime;
     }
     /**
      *
      * 获取指定格式日期的方法；默认格式"yyyy-MM-dd HH:mm:ss"
      * @param format
      * @return
      */
     public static String  getDate(String format){
         Date time = new Date();
         if(isObjNullOrEmp(format)){
             format = "yyyy-MM-dd HH:mm:ss";
         }
          SimpleDateFormat formatter;
             formatter = new SimpleDateFormat (format);
             String ctime = formatter.format(time);

        return  ctime;
     }



     /**
      * day 获取N天前或N天后日期
      * type为空或null时候，获取指定N天前或N天后0时0分0秒时间，否则获取指定天的当前时分秒信息
      * @return
      */
     public static Date getSpecifyDate(int day,String type){
           Calendar calendar = Calendar.getInstance();
           calendar.setTime(new Date());
           calendar.add(Calendar.DAY_OF_MONTH, day);

           if(CommonUtil.isObjNullOrEmp(type)){
               calendar.set(Calendar.HOUR_OF_DAY, 0);
               calendar.set(Calendar.MINUTE, 0);
               calendar.set(Calendar.SECOND, 0);
           }
           Date time = calendar.getTime();
           return time;
     }



     /**
      * 按照时间生成指定格式的编号
      * @param startCode 编号开头
      * @param format   编号中间日期格式
      * @return
      */
     public static String getBookCode(String startCode,String format,Long sequence){
         Date time = new Date();
         if(CommonUtil.isObjNullOrEmp(startCode)){
             startCode = "SP-";
         }
         if(CommonUtil.isObjNullOrEmp(format)){
             format = "yyyyMMdd";
         }

         SimpleDateFormat formatter;
         formatter = new SimpleDateFormat (format);
         String ctime = formatter.format(time);

         return startCode+ctime+sequence.toString();
     }





         /**
          * 获得一个UUID
          * @param istrue boolean  true为原生uuid，false为去掉“-”符号
          * @return UUID  String   返回32位的字符串
          */
         public static String getUUID(boolean istrue){
             String uuId = UUID.randomUUID().toString();
             if(istrue){
                 return uuId.toString();
             }else{

                 //去掉“-”符号
                 return uuId.substring(0,8)+uuId.substring(9,13)+uuId.substring(14,18)+uuId.substring(19,23)+uuId.substring(24);

             }
         }



         /**
          * 通用http的请求
          * @param urlStr  请求的url
          * @return
          */
         public static String httpSeverConnectionExtend(String urlStr){

             // 定义返回发送结果
             String inputline = "";
             try {
                 StringBuffer sb = new StringBuffer(urlStr);
                 URL url = new URL(sb.toString()); // 创建url对象搜索
                 HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // 打开url连接
                 //connection.setRequestMethod("GET"); // 设置url请求方式 ‘get’ 或者 ‘post’
                 connection.setRequestMethod("POST");
                 // 发送POST请求必须设置如下两行
                 connection.setDoOutput(true);
                 connection.setDoInput(true);
 //				connection.setDoInput(true);
 //		        connection.setDoOutput(true);
 //		        connection.setUseCaches(false);
 //		        connection.setInstanceFollowRedirects(true);
                 connection.addRequestProperty("Accept","application/json,text/javascript, */*;q=0.01");
 //		        connection.addRequestProperty("Referer","http://sso.gxnews.com.cn/login.php");
                 connection.addRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                 connection.addRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
                 connection.addRequestProperty("Content-Type","application/x-www-form-urlencoded");
                 connection.addRequestProperty("Accept-Encoding","gzip, deflate");
 //		        connection.addRequestProperty("Host","sso.gxnews.com.cn");
                 connection.addRequestProperty("Connection","Keep-Alive");
 //		        connection.addRequestProperty("Cache-Control","no-cache");

                 connection.connect();

                 BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),"utf-8")); //设置编码,否则中文乱码
                 String tempStr = in.readLine();
                 while (tempStr != null) {
                 inputline = inputline + tempStr;
                 tempStr = in.readLine();
                 }

                 in.close();

             } catch (IOException e){
                 e.printStackTrace();
                 logger.error(getDate()+"httpconnect连接异常："+e.getStackTrace().toString());
                  inputline = "{\"rtnCode\":\"999999\",\"rtnMsg\":\"连接超时!\",\"head\":{\"rtnMsg\":\"连接超时!\",\"rtnCode\":\"999999\"}}";
                 return inputline;
             }

             return inputline;


         }


         /**
          * 通用http的请求
          * @param urlStr  请求的url
          * @return
          */
         public static String httpSeverConnection(String urlStr){

             // 定义返回发送结果
             String inputline = "";
             try {
                 StringBuffer sb = new StringBuffer(urlStr);
                 URL url = new URL(sb.toString()); // 创建url对象搜索
                 HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // 打开url连接
                 //connection.setRequestMethod("GET"); // 设置url请求方式 ‘get’ 或者 ‘post’
                 connection.setRequestMethod("POST");
                 // 发送POST请求必须设置如下两行
                 connection.setDoOutput(true);
                 connection.setDoInput(true);
 //				connection.setDoInput(true);
 //		        connection.setDoOutput(true);
 //		        connection.setUseCaches(false);
 //		        connection.setInstanceFollowRedirects(true);
                 connection.addRequestProperty("Accept","application/json,text/javascript, */*;q=0.01");
 //		        connection.addRequestProperty("Referer","http://sso.gxnews.com.cn/login.php");
                 connection.addRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                 connection.addRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
                 connection.addRequestProperty("Content-Type","application/x-www-form-urlencoded");
                 connection.addRequestProperty("Accept-Encoding","gzip, deflate");
 //		        connection.addRequestProperty("Host","sso.gxnews.com.cn");
                 connection.addRequestProperty("Connection","Keep-Alive");
 //		        connection.addRequestProperty("Cache-Control","no-cache");

                 connection.connect();

                 BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),"utf-8")); //设置编码,否则中文乱码
                 String tempStr = in.readLine();
                 while (tempStr != null) {
                 inputline = inputline + tempStr;
                 tempStr = in.readLine();
                 }

                 in.close();

             } catch (IOException e){
                 e.printStackTrace();
                 logger.error(getDate()+"httpconnect连接异常："+e.getStackTrace().toString());
                  inputline = "{\"rtnCode\":\"999999\",\"rtnMsg\":\"连接超时!\",\"head\":{\"rtnMsg\":\"连接超时!\",\"rtnCode\":\"999999\"}}";
                 return inputline;
             }

             return inputline;


         }





     /*方法二：推荐，速度最快
       * 判断是否为整数
       * @param str 传入的字符串
       * @return 是整数返回true,否则返回false
     */

       public static boolean isInteger(String str) {
             Pattern pattern = compile("^[-\\+]?[\\d]*$");
             return pattern.matcher(str).matches();
       }

     /**
      * 获取客户端浏览器版本和IP地址
      * @param request
      * @return
      */
     public static Map<String,String> getClientInfo(HttpServletRequest request)  {
             Map<String,String> map = new HashMap<String,String>();
             String userAgent =request.getHeader("user-agent");
             map.put("userAgent", userAgent);//浏览器信息

             String ip = request.getHeader("x-forwarded-for");

             if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                 // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                 if( ip.indexOf(",")!=-1 ){
                     ip = ip.split(",")[0];
                 }
             }
             if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                 ip = request.getHeader("Proxy-Client-IP");
                 //System.out.println("Proxy-Client-IP ip: " + ip);
             }
             if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                 ip = request.getHeader("WL-Proxy-Client-IP");
                 //System.out.println("WL-Proxy-Client-IP ip: " + ip);
             }
             if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                 ip = request.getHeader("HTTP_CLIENT_IP");
                // System.out.println("HTTP_CLIENT_IP ip: " + ip);
             }
             if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                 ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                 //System.out.println("HTTP_X_FORWARDED_FOR ip: " + ip);
             }
             if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                 ip = request.getHeader("X-Real-IP");
                // System.out.println("X-Real-IP ip: " + ip);
             }
             if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                 ip = request.getRemoteAddr();
                 //System.out.println("getRemoteAddr ip: " + ip);
             }
            // System.out.println("获取客户端ip: " + ip);
             map.put("ip", ip);//客户端IP
             return map;
     }


     /**
      * 判断ajax请求
      * @param request
      * @return
      */
     public static boolean isAjax(HttpServletRequest request){

         return  (request.getHeader("X-Requested-With") != null  && "XMLHttpRequest".equals( request.getHeader("X-Requested-With").toString())) ;
     }

 }
