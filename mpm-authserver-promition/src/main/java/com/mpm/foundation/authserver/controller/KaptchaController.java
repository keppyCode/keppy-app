package com.mpm.foundation.authserver.controller;

import com.mpm.foundation.authserver.utils.I18nUtils;
import com.mpm.foundation.authserver.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.kaptcha.Kaptcha;
import com.baomidou.kaptcha.exception.KaptchaIncorrectException;
import com.baomidou.kaptcha.exception.KaptchaNotFoundException;
import com.baomidou.kaptcha.exception.KaptchaTimeoutException;


@RestController
@RequestMapping("/kaptcha")
public class KaptchaController {

	@Autowired
	private Kaptcha kaptcha;
	
	@Autowired
	private MessageSource messageSource;

	@GetMapping("/render")
	public void render() {
		kaptcha.render();
	}

	@PostMapping("/valid")
	public Json validDefaultTime(@RequestParam String code) {
		try {
			kaptcha.validate(code);
			return Json.buildSuccessJson("ok");
		} catch (KaptchaIncorrectException e) {
			e.printStackTrace();
			return Json.buildErrorJson("1",getMessage("kaptcha.error.incorrect"));
		} catch (KaptchaNotFoundException e) {
			e.printStackTrace();
			return Json.buildErrorJson("2",getMessage("kaptcha.error.unfound"));
		} catch (KaptchaTimeoutException e) {
			e.printStackTrace();
			return Json.buildErrorJson("3",getMessage("kaptcha.error.timeout"));
		}

	}

	@PostMapping("/validTime")
	public Json validWithTime(@RequestParam String code) {
		try {
			kaptcha.validate(code, 60);
			return Json.buildSuccessJson("ok");
		} catch (KaptchaIncorrectException e) {
			e.printStackTrace();
			return Json.buildErrorJson("验证码不正确");
		} catch (KaptchaNotFoundException e) {
			e.printStackTrace();
			return Json.buildErrorJson("验证码未找到");
		} catch (KaptchaTimeoutException e) {
			e.printStackTrace();
			return Json.buildErrorJson("验证码过期");
		}
	}
	
	private String getMessage(String code) {
		return I18nUtils.getMessage(messageSource, code);
	}

}
