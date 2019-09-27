package com.mpm.foundation.authserver.security.core.userdetails;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mpm.foundation.authserver.security.bean.DeployProfile;
import com.mpm.foundation.authserver.security.bean.MpmUser;
import com.mpm.foundation.authserver.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mpm.foundation.authserver.cfg.Constants;

@Component
public class JdbcUserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private MessageSource messageSource;

	private String getMessage(String code) {
	    try {
            return I18nUtils.getMessage(messageSource, code);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
	}

	public static final String LOAD_USER = "SELECT * from uc_user_profile t where t.id = ?";

	public static final String LOAD_LOCAL_USER = "select * from uc_user_profile t where t.mobile = ? or t.mpm_number=? or t.login_name=? or t.email =?";

	public static final String LOAD_ENTERPRISE_USER = "select * from uc_enterprise_authenticate t where t.company_code=? and t.login_name = ? and state = 1";

	public static final String LOAD_WORKWEIXIN = "select * from uc_third_party_oauth_authenticate t where t.oauth_id=? and oauth_provider=? and company_code=?";

	public static final String LOAD_COMPANY = "select t.*,d.url_config from company t,deploy_profile d where t.profile_id=d.id and t.company_code=?";

	/**
	 * 查询用户
	 * 
	 * @param username 用户名称 企业模式enterprise:${companycode}:${loginname}
	 *                 企业微信模式workweixin:${companycode}:${loginname}
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String[] arr = username.split(":");
		String type = arr[0];
		if (Constants.LOGIN_TYPE_ENTERPRISE.equals(type)) {
			String cmpcode = arr[1];
			String loginname = arr[2];
			return loadEmpUserByUsername(cmpcode, loginname);
		} else if (Constants.LOGIN_TYPE_WORKWEIXIN.equals(type)) {
			String cmpcode = arr[1];
			String loginname = arr[2];
			return loadWorkWeixinByUsername(cmpcode, loginname);
		} else if (Constants.LOGIN_TYPE_LOCAL.equals(type)) {
			String loginname = arr[1];
			return loadLocalUserByUsername(loginname);
		} else {
			throw new UsernameNotFoundException(getMessage("login.error.illegal.type") + ":" + username);
		}

	}

	/**
	 * 本地通用登录模式
	 * @param loginname
	 * @return
	 */
	public UserDetails loadLocalUserByUsername(String loginname) {
		List<Map<String, Object>> list = jdbcTemplate.queryForList(LOAD_LOCAL_USER, loginname,loginname,loginname,loginname);
		if (list == null || list.isEmpty()) {
			throw new UsernameNotFoundException(getMessage("login.error.account.unfound") + ":" + loginname);
		}
		if (list.size() > 1) {
			throw new UsernameNotFoundException(getMessage("login.error.found.multipleuser") + ":" + loginname);
		}
		Map<String, Object> map = list.get(0);
		Integer state = Integer.parseInt(map.get("state").toString());
		if (state !=Constants.COMPANY_STATE_AVAILABLE) {
			throw new UsernameNotFoundException(getMessage("login.error.found.unfound") + ":" + loginname);
		}
		String id = map.get("id").toString();
		String password = map.get("password").toString();
		String name = (null==map.get("username")?null:map.get("username").toString());
		String mobile = (null==map.get("mobile")?null:map.get("mobile").toString());
		Integer isValidated = null==map.get("is_validated")?1:Integer.valueOf(map.get("is_validated").toString());

		MpmUser user = buildUser(Constants.LOGIN_TYPE_LOCAL + ":" + loginname, name, null, null, id,
				Constants.LOGIN_TYPE_LOCAL, loginname, password, null,mobile,isValidated,1);
		user.setBelongto(Constants.BELONGTO_COMMON);
		return user;
	}

	/**
	 * 企业微信用户
	 * 
	 * @param companycode 企业编码
	 * @param username    loginname
	 * @return
	 */
	public UserDetails loadWorkWeixinByUsername(String companycode, String username) {
		DeployProfile dp = checkCompanyAvailable(companycode);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(LOAD_WORKWEIXIN, username,
				Constants.LOGIN_TYPE_WORKWEIXIN, companycode);
		if (list == null || list.isEmpty()) {
			throw new UsernameNotFoundException(
					getMessage("login.error.account.unfound") + ":" + username + ":" + companycode);
		}
		if (list.size() > 1) {
			throw new UsernameNotFoundException(
					getMessage("login.error.found.multipleuser") + ":" + username + ":" + companycode);
		}
		String uid = list.get(0).get("user_id").toString();
		String companyCode = list.get(0).get("company_code").toString();
		Integer isRelated = null== list.get(0).get("is_related")?1:Integer.valueOf( list.get(0).get("is_related").toString());
		List<Map<String, Object>> list2 = jdbcTemplate.queryForList(LOAD_USER, uid);
		if (list2 == null || list2.isEmpty()) {
			throw new UsernameNotFoundException(getMessage("login.error.account.unfound") + ":" + uid);
		}
		Map<String, Object> m = list2.get(0);
		Integer state = Integer.parseInt(m.get("state").toString());
		if (state !=Constants.COMPANY_STATE_AVAILABLE) {
			throw new UsernameNotFoundException(getMessage("login.error.found.unfound") + ":" + username);
		}

		String name = m.get("username").toString();
		String mobile = (null==m.get("mobile")?null:m.get("mobile").toString());
		Integer isValidated = null==m.get("is_validated")?1:Integer.valueOf(m.get("is_validated").toString());
		MpmUser user = buildUser(Constants.LOGIN_TYPE_WORKWEIXIN + ":" + companyCode + ":" + username, name,
				companyCode, null, uid, Constants.LOGIN_TYPE_WORKWEIXIN, username, "", dp,mobile,isValidated,isRelated);
		user.setBelongto(Constants.BELONGTO_ENTERPRISE);
		return user;
	}

	/**
	 * 企业用户
	 * 
	 * @param companycode 企业编码
	 * @param username    企业微信的用户id
	 * @return
	 */
	public UserDetails loadEmpUserByUsername(String companycode, String username) {
		DeployProfile dp = checkCompanyAvailable(companycode);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(LOAD_ENTERPRISE_USER, companycode, username);
		if (list == null || list.isEmpty()) {
			throw new UsernameNotFoundException(
					getMessage("login.error.account.unfound") + ":" + username + ":" + companycode);
		}
		if (list.size() > 1) {
			throw new UsernameNotFoundException(
					getMessage("login.error.found.multipleuser") + ":" + username + ":" + companycode);
		}
		Map<String, Object> map = list.get(0);
		String id = map.get("user_id").toString();
		String companyCode = map.get("company_code").toString();
		Integer isRelated = null==map.get("is_related")?1:Integer.valueOf(map.get("is_related").toString());
		Integer enterPriseState = Integer.parseInt(map.get("state").toString());
		if (enterPriseState !=Constants.COMPANY_STATE_AVAILABLE) {
			throw new UsernameNotFoundException(getMessage("login.error.found.unfound") + ":" + username);
		}
		List<Map<String, Object>> list2 = jdbcTemplate.queryForList(LOAD_USER, id);
		if (list2 == null || list2.isEmpty()) {
			throw new UsernameNotFoundException(getMessage("login.error.account.unfound") + ":" + id);
		}
		Map<String, Object> m = list2.get(0);
		Integer state = Integer.parseInt(m.get("state").toString());
		if (state !=Constants.COMPANY_STATE_AVAILABLE) {
			throw new UsernameNotFoundException(getMessage("login.error.found.unfound") + ":" + username);
		}
		String name = m.get("username").toString();
		String password = m.get("password").toString();
		String mobile = (null==m.get("mobile")?null:m.get("mobile").toString());
		Integer isValidated = null==m.get("is_validated")?1:Integer.valueOf(m.get("is_validated").toString());

		MpmUser user = buildUser(Constants.LOGIN_TYPE_ENTERPRISE + ":" + companyCode + ":" + username, name,
				companyCode, null, id, Constants.LOGIN_TYPE_ENTERPRISE, username, password,dp,mobile,isValidated,isRelated);
		user.setBelongto(Constants.BELONGTO_ENTERPRISE);
		return user;
	}

	/**
	 * 检查公司可用
	 * 
	 * @param companycode
	 */
	public DeployProfile checkCompanyAvailable(String companycode) {
		List<Map<String, Object>> list = jdbcTemplate.queryForList(LOAD_COMPANY, companycode);
		if (list == null || list.isEmpty()) {
			throw new CompanyNotFoundException(getMessage("login.error.company.unfound") + ":" + companycode);
		}
		Map<String, Object> map = list.get(0);
		Object state = map.get("STATE");
		if (state != null) {
			int s = Integer.parseInt(state.toString());
			if (Constants.COMPANY_STATE_AVAILABLE != s) {
				throw new CompanyUnavailableException(
						getMessage("login.error.company.unavailable") + ":" + companycode + ":" + state);
			} else {// 可用
				Object obj = map.get("EXPIRE_IN");
				if (obj != null) {
					Timestamp time = (Timestamp) obj;
					if (time.before(new Date())) {
						throw new CompanyExpiredException(
								getMessage("login.error.company.expired") + ":" + companycode + ":" + time);
					}
				}
				return getDeployProfile(map);
			}
		}
		throw new CompanyUnavailableException(
				getMessage("login.error.company.unavailable") + ":" + companycode + ":" + state);
	}

	private DeployProfile getDeployProfile(Map<String, Object> map) {
		DeployProfile dp = new DeployProfile();
		Object id = map.get("PROFILE_ID");
		Object obj = map.get("URL_CONFIG");
		String url = obj == null ? null : obj.toString();
		dp.setId(id == null ? null : id.toString());
		if (url != null) {
			JSONArray a = JSON.parseArray(url);
			for (Object o : a) {
				JSONObject jsonobject = (JSONObject) o;
				Object jfz = jsonobject.get("jifenzhi");
				if (jfz != null) {
					dp.setUrl(jfz.toString());
				}
			}
		}
		return dp;
	}

	public static void main(String[] args) {
		String url = "[{\"jifenzhi\":\"http://server2.jifenzhi.group\"}]";
		JSONArray a = JSON.parseArray(url);
		for (Object o : a) {
			JSONObject obj = (JSONObject) o;
			String jifenzhi = obj.get("jifenzhi").toString();
			System.out.println(jifenzhi);
		}
	}

	/**
	 * 
	 * @param username    logintype:loginname
	 * @param name        用户名中文
	 * @param companycode 公司代码
	 * @param face        头像
	 * @param id          用户id
	 * @param loginType   登录类型
	 * @param loginName   登录名
	 * @param password    密码
	 * @param profile     片区
	 * @return
	 */
	MpmUser buildUser(String username, String name, String companycode, String face, String id, String loginType,
			String loginName, String password, DeployProfile profile,String mobile,Integer isValidated,Integer isRelated) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		MpmUser user = new MpmUser(username, password, name, authorities);
		user.setCompanycode(companycode);
		user.setFace(face);
		user.setId(id);
		user.setLogintype(loginType);
		user.setLoginname(loginName);
		user.setProfile(profile);
		user.setMobile(mobile);
		user.setIsRelated(isRelated);
		user.setIsValidated(isValidated);
		return user;
	}

}
