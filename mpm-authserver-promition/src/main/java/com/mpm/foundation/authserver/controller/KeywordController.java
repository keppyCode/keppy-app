package com.mpm.foundation.authserver.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mpm.foundation.authserver.service.KeywordService;


@RestController
@RequestMapping("/keyword")
public class KeywordController {
	@Autowired
	private KeywordService keywordService;

	@RequestMapping(value = "/companycode", method = RequestMethod.POST)
	public List<Map<String, Object>> companycode(@RequestParam("keyword") String keyword) {
		return keywordService.companycode(keyword);
	}

	@RequestMapping(value = "/loginname", method = RequestMethod.POST)
	public List<Map<String, Object>> loginname(@RequestParam("companycode") String companycode,
			@RequestParam("keyword") String keyword) {
		return keywordService.loginname(keyword, companycode);
	}
}
