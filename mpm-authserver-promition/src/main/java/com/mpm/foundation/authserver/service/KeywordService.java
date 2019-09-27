package com.mpm.foundation.authserver.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class KeywordService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static final String LOAD_CMP_BYKEYWORD_SQL = "select code from sys_company t where t.STATUS = 1 and t.code like ? LIMIT 0,10";
	public static final String LOAD_LOGINNAME_BYKEYWORD_SQL = "SELECT a.login_name FROM sys_user a "
			+ "LEFT JOIN sys_company company ON company.id = a.company_id "
			+ "LEFT JOIN sys_employee e ON e.id = a.employee_id "
			+ "WHERE e.STATUS = 0 AND a.del_flag = 0 AND a.login_name like ? AND company.CODE = ? AND company.STATUS = 1 LIMIT 0,10";

	public List<Map<String, Object>> companycode(String keyword) {
		return jdbcTemplate.queryForList(LOAD_CMP_BYKEYWORD_SQL, keyword + "%");
	}

	public List<Map<String, Object>> loginname(String keyword, String companycode) {
		return jdbcTemplate.queryForList(LOAD_LOGINNAME_BYKEYWORD_SQL, keyword + "%", companycode);
	}

}
