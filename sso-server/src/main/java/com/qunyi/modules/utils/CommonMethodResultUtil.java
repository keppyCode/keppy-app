package com.qunyi.modules.utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/**
 * 通用的实现方法返回结果结构<br>
 	status为"0"处理失败，status为"1"处理成功<br>
	本类作用是作为返回结果的示例，请使用net.sf.json.JSONObject生成该结果，结果结果如下<br/>
	
	 {<br/>
	 	&nbsp;&nbsp;&nbsp;&nbsp;head:{status:"0",message:"概要信息"}<br/>
	 	&nbsp;&nbsp;&nbsp;&nbsp;body:{<br/>
	 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;list:[<br/>
	 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{key11:"value11",key12:value12},<br/>
	 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{key21:"value21",key22:value22},<br/>
	 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{key31:"value31",key32:value32}<br/>
	 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;],<br/>
	 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;property1:property_value1,<br/>
	 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;property2:property_value2<br/>
	 	&nbsp;&nbsp;&nbsp;&nbsp;}<br/>
	 }<br/>
 * @author zhouwei
 *
 */
public class CommonMethodResultUtil{
	
	/**请求失败*/
	public static final String STATUS_FAILED = "0";
	
	/**请求成功*/
	public static final String STATUS_SUCCESS = "1";
	
	/**部分处理失败，部分处理成功*/
	public static final String STATUS_PARTLY_FAILED = "2";

	/**登陆超时*/
	public static final String STATUS_LOGIN_INVALID = "999992";
	
	/**参数个数或者名称不对*/
	public static final String STATUS_INVALID_PARAMETER = "999998";
	
	/**其余错误*/
	public static final String STATUS_BUSY = "999999";
	
	/**head里面的状态码的key值*/
	public static final String HEAD_STATUS = "status";

	/**head里面的概要信息的key值*/
	public static final String HEAD_MESSAGE = "message";
	
	/*
	本类作用是作为返回结果的示例，请使用net.sf.json.JSONObject生成该结果，结果结果如下<br/>
	
 	status为0处理失败，status为1处理成功<br>
	本类作用是作为返回结果的示例，请使用net.sf.json.JSONObject生成该结果，结果结果如下<br/>
	
	 {
	 	head:{
	 		status:"0",
	 		message:"概要信息"
	 	}
	 	body:{
	 		list:[
	 			{key11:"value11",key12:value12},
	 			{key21:"value21",key22:value22},
	 			{key31:"value31",key32:value32}
	 		],
	 		property1:property_value1,
	 		property2:property_value2
	 	}
	 
	 }
	*/
	
	/**
	 * 头部信息(方法处理成功或失败状态及概要原因)
	 */
	private JSONObject head = new JSONObject();
	
	/**
	 * 详细信息（方法返回的详细的数据项目）
	 */
	private JSONObject body = new JSONObject();

	/**
	 * 头部信息(方法处理成功或失败状态及概要原因)
	 */
	public JSONObject getHead() {
		return head;
	}

	/**
	 * 详细信息（方法返回的详细的数据项目）
	 */
	public JSONObject getBody() {
		return body;
	}

	/**
	 * 设定状态码<br/>
	 * 表示成功（对应statue="1"）<br/>
	 * 表示失败（对应statue="0"）
	 * 部分处理失败，部分处理成功（对应statue="2"）<br/>
	 * 登陆超时（对应statue="999992"）<br/>
	 * 参数个数或者名称不对（对应statue="999998"）<br/>
	 * 其余错误（对应statue="999999"）<br/>
	 */
	public CommonMethodResultUtil setStatus(int status) {
		this.getHead().put("status", ""+status);
		return this;
	}
	
	/**
	 * 获取状态码<br/>
	 * 表示成功（对应statue="1"）<br/>
	 * 表示失败（对应statue="0"）
	 * 部分处理失败，部分处理成功（对应statue="2"）<br/>
	 * 登陆超时（对应statue="999992"）<br/>
	 * 参数个数或者名称不对（对应statue="999998"）<br/>
	 * 其余错误（对应statue="999999"）<br/>
	 */
	public CommonMethodResultUtil setStatus(String status) {
		this.getHead().put("status", status);
		return this;
	}
	
	/**
	 * 取得状态码，表示成功（对应statue="1"），表示失败（对应statue="0"）
	 */
	public String getStatus() {
		return this.getHead().getString("status");
	}
	
	/**
	 * 设定头部概要信息
	 */
	public void setMessage(String message) {
		this.getHead().put("message", (null==message)?"":message.trim());
	}
	
	/**
	 * 取得头部概要信息
	 */
	public String getMessage() {
		return this.getHead().getString("message");
	}
	
	/**
	 * 给body追加一个指定keylist
	 */
	public CommonMethodResultUtil addListToBody(String key,List<JSONObject> value){
		String myKey = (null == key)?"list":key.trim();
		this.getBody().put(myKey, (null == value)?"[]":value);
		return this;
	}
	
	/**
	 * 给body添加追加一个指定key的字符串
	 */
	public CommonMethodResultUtil addPropertyToBody(String key,String value){
		String myKey = (null == key)?"property":key.trim();
		String myValue = (null == value)?"":value.trim();
		this.getBody().put(myKey, myValue);
		return this;
	}

	/**
	 * 给body添加追加一个指定key的map对象
	 */
	public CommonMethodResultUtil addMapToBody(String key,Map value){
		String myKey = (null == key)?"map":key.trim();
		this.getBody().put(myKey, value);
		return this;
	}
	
	/**
	 * Map<String,Object>
	 * key:head_status,head_messge,body_list,body_parm;keyvalue:Oject
	 * 给body添加追加一个指定key的字符串
	 * @throws Exception 
	 */
	public CommonMethodResultUtil getJsonMessge(Map<String,Object> map) throws Exception{
		if(
			(null == map)||(map.isEmpty())
			||(!map.containsKey("status"))||(!map.containsKey("message"))
			||(CommonUtil.isObjNullOrEmp(map.get("status")))
			||(CommonUtil.isObjNullOrEmp(map.get("message")))
			){
			throw new Exception("没有指定status和message值");
		}
		for (Map.Entry<String, Object> entry : map.entrySet()) {  
			if("status".equals(entry.getKey())){
				setStatus(entry.getValue().toString());
			}else if("message".equals(entry.getKey())){
				setMessage(entry.getValue().toString());
			}else{
				this.getBody().put(entry.getKey(), entry.getValue());
			}
		}
		System.out.print(this);
		return this;
	}

	/**
	 * 根据状态码和概要信息初始化
	 */
	public CommonMethodResultUtil(){
		setStatus("");
		setMessage("");
	}
	
	/**
	 * 根据状态码和概要信息初始化
	 * @param status
	 * @param message
	 */
	public CommonMethodResultUtil(int status,String message){
		setStatus(status);
		setMessage(message);
	}
	
	/**
	 * 根据状态码和概要信息初始化,并追加一个list到body
	 * @param status
	 * @param message
	 * @param key
	 * @param value
	 */
	public CommonMethodResultUtil(int status,String message,String key,List<JSONObject> value){
		setStatus(status);
		setMessage(message);
		addListToBody(key,value);
	}
	
	/**
	 * 根据状态码和概要信息初始化,并追加一个字符串到body
	 * @param status
	 * @param message
	 * @param key
	 * @param value
	 */
	public CommonMethodResultUtil(int status,String message,String key,String value){
		setStatus(status);
		setMessage(message);
		addPropertyToBody(key,value);
	}
	
	/**
	 * 判断body下的子一级是否有指定的key
	 * @param key
	 * @return
	 */
	public boolean hasKeyOfBodySubLevel(String key){
		if(null==key || "".equals(key.trim())){
			return false;
		}
		return this.getBody().has(key);
	}
	
	/**
	 * 更新body里面的指定的list
	 * @param key
	 * @param value
	 * @return
	 */
	public CommonMethodResultUtil updateListOfBody(String key,List value){
		if(hasKeyOfBodySubLevel(key)){
			String myKey = key.trim();
			this.getBody().remove(myKey);
			this.getBody().put(myKey, (null == value)?"[]":value);
		}
		return this;
	}
	
	/**
	 * 更新body里面的指定的单字符串
	 * @param key
	 * @param value
	 * @return
	 */
	public CommonMethodResultUtil updatePropertyOfBody(String key,String value){
		if(hasKeyOfBodySubLevel(key)){
			String myKey = key.trim();
			String myValue = (null == key)?"":value.trim();
			this.getBody().remove(myKey);
			this.getBody().put(myKey, myValue);
		}
		return this;
	}
	
	/**
	 * 移除body里面子一级的指定的key
	 * @param key
	 * @return
	 */
	public CommonMethodResultUtil removeByKeyOfBody(String key){
		if(hasKeyOfBodySubLevel(key)){
			this.getBody().remove(key.trim());
		}
		return this;
	}
	
	/**
	 * 获取body里面子一级的指定的key并转为map类型
	 * @param key
	 * @return
	 */
	public Map getMapByKeyOfBody(String key){
		if(!hasKeyOfBodySubLevel(key)){
			return null;
		}
		return (Map)this.getBody().get(key.trim());
	}
	
	/**
	 * 获取body里面子一级的指定的key并转为List类型
	 * @param key
	 * @return
	 */
	public List getListByKeyOfBody(String key){
		if(!hasKeyOfBodySubLevel(key)){
			return null;
		}
		return (List)this.getBody().get(key.trim());
	}
	
	/**
	 * 获取body里面子一级的指定的key并转为Iterator类型
	 * @param key
	 * @return
	 */
	public Iterator getIteratorByKeyOfBody(String key){
		if(!hasKeyOfBodySubLevel(key)){
			return null;
		}
		return (Iterator)this.getBody().get(key.trim());
	}
	
	@Override
	public String toString(){
		String str = "";
		str += "{\"head\":"+this.getHead().toString()+",\"body\":"+this.getBody().toString()+"}";
		return str;
	}
	
	public static void main(String[]args){
		//测试构造函数1
		CommonMethodResultUtil commonMethodResultUtil_1 = new CommonMethodResultUtil(1,"成功");

		System.out.println("测试构造函数1\r\n"+commonMethodResultUtil_1);

		//测试添加键值对
		commonMethodResultUtil_1.addPropertyToBody("key", "value");
		System.out.println("测试添加键值对\r\n"+commonMethodResultUtil_1);
		commonMethodResultUtil_1.addPropertyToBody("keykey", "valuevalue");
		System.out.println("测试再次添加键值对\r\n"+commonMethodResultUtil_1);

		//测试添加list
		JSONObject element1 = new JSONObject();
		element1.put("key11", "value11");
		element1.put("key12", "value12");
		element1.put("key13", "value13");
		JSONObject element2 = new JSONObject();
		element2.put("key21", "value21");
		element2.put("key22", "value22");
		element2.put("key23", "value23");
		JSONObject element3 = new JSONObject();
		element3.put("key31", "value31");
		element3.put("key32", "value32");
		element3.put("key33", "value33");
		List<JSONObject> list = new ArrayList<JSONObject>();
		list.add(element1);
		list.add(element2);
		list.add(element3);
		commonMethodResultUtil_1.addListToBody("myList", list);
		System.out.println("测试添加list\r\n"+commonMethodResultUtil_1);

		list.remove(0);
		commonMethodResultUtil_1.updateListOfBody("myList", list);
		System.out.println("测试更新list\r\n"+commonMethodResultUtil_1);

		commonMethodResultUtil_1.updatePropertyOfBody("keykey", "3value");
		System.out.println("测试更新键值对\r\n"+commonMethodResultUtil_1);

		//移除键值对测试
		commonMethodResultUtil_1.removeByKeyOfBody("keykey");
		System.out.println("测试移除键值对\r\n"+commonMethodResultUtil_1);

		//移除list测试
		commonMethodResultUtil_1.removeByKeyOfBody("myList");
		System.out.println("测试移除list\r\n"+commonMethodResultUtil_1);

		System.out.println("body里面有key为\"key\"的键值对或列表吗？"+commonMethodResultUtil_1.hasKeyOfBodySubLevel("key"));
		System.out.println("body里面有key为\"mylist\"的键值对或列表吗？"+commonMethodResultUtil_1.hasKeyOfBodySubLevel("mylist"));
		System.out.println("head里面的状态码statue为"+commonMethodResultUtil_1.getStatus());
		System.out.println("head里面的message为"+commonMethodResultUtil_1.getMessage());

		//测试构造函数2
		CommonMethodResultUtil commonMethodResultUtil_2 = new CommonMethodResultUtil(1,"成功","name","张三");
		System.out.println("测试构造函数2\r\n"+commonMethodResultUtil_2);

		//测试构造函数3
		CommonMethodResultUtil commonMethodResultUtil_3 = new CommonMethodResultUtil(1,"成功","yourList",list);
		System.out.println("测试构造函数3\r\n"+commonMethodResultUtil_3);

		Map<String,Object>map = new HashMap<String,Object>();
		map.put("ss", "11");
		try {
			commonMethodResultUtil_3.getJsonMessge(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String,String> mapmap = new HashMap<String,String>();
		mapmap.put("ss", "ssvalue");
		mapmap.put("ss1", "ss1value");
		CommonMethodResultUtil CommonMethodResultUtil_4 = new CommonMethodResultUtil();
		CommonMethodResultUtil_4.addMapToBody("ww", mapmap);
		System.out.println("测试空构造函数4\r\n"+CommonMethodResultUtil_4);
	}
}
