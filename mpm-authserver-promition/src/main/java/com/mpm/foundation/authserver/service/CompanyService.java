package com.mpm.foundation.authserver.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.mpm.foundation.authserver.security.bean.Company;


/**
 * 查询公司服务
 * 
 * @author zzm
 */
@Component
public class CompanyService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private String sql = "select * from company t where t.company_code=?";

	private String sql_corip = "select * from company t where  t.corpid=?";

	/**
	 * 公司查询 code和corpid二选一
	 * 
	 * @param code   公司代码
	 * @param corpid 企业微信corpid
	 * @return
	 */
	public Company getCompany(String code, String corpid) {
		List<Company> list = null;
		if (StringUtils.isNotBlank(code)) {
			list = jdbcTemplate.query(sql, new Object[] { code }, new CompanyRowMapper());
		} else {
			Assert.notNull(corpid, "corip为空companycode为空");
			list = jdbcTemplate.query(sql_corip, new Object[] { corpid }, new CompanyRowMapper());
		}
		Assert.notEmpty(list, code + ":" + corpid + "找不到公司");
		Assert.isTrue(list.size() == 1, code + ":" + corpid + "找到公司不唯一");
		Company c = list.get(0);
		Assert.hasLength(c.getCorpid(), code + "没配置企业微信Corpid");
		Assert.hasLength(c.getSecret(), code + "没配置企业微信Secret");
		return c;
	}

	/**
	 * 公司的rowmapper
	 * 
	 * @author zzm
	 *
	 */
	public class CompanyRowMapper implements RowMapper<Company> {

		@Override
		public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
			Company r = new Company();
			r.setCode(rs.getString("company_code"));
			r.setCorpid(rs.getString("corpid"));
			r.setSecret(rs.getString("secret"));
			return r;
		}

	}
}
