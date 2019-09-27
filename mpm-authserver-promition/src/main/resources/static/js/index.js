/*
* @Author: Administrator
* @Date:   2019-01-21 14:18:43
* @Last Modified by:   liujiexian
* @Last Modified time: 2019-02-01 15:25:13
*/

/*要使用的函数*/
	// 当前login-account面板，可选项： local代表账号登录,enterprise代表企业账号登录
	// var currentPane = 'local'
	// 点击改变验证码
	
	var loginStatus = 2;
	var clicktag = 0;
	var isSendCode = false;

	if (location.host.indexOf(".com") !== -1) { 
		loginStatus = 1;
		$(".loginname-input").addClass('hide')
		$('.enterprise-input').removeClass('hide')
		sessionStorage.setItem('status', '1')
		$('.login-enterprise-title').addClass('hide')
		$('.login-account').removeClass('hide')
		$('.login-enterprise').addClass('hide')
	}

	$(window).resize(function () {
		if (document.body.clientWidth < 768) {
			$('.wrap-logo').removeClass('hide')
		} else {
			$('.wrap-logo').addClass('hide')
		}
	});

	function changeVerifyCode() {
	  //延时器防止验证码被频繁点击
	  if (clicktag == 0) {
	    clicktag = 1;                                                 
	    setTimeout(function () { 
	      $("#yzmImg").prop("src","/kaptcha/render?" + Math.floor(Math.random() * 100));
	      clicktag = 0; 
	    }, 400);                
	  }
	}
	function changePassword(){

		var content = "验证码是:9527,不要告诉别人哦"
		var fromClientId = "attendenceWechat"
		var fromUserId = "999"
		var tableId = "登录短信测试"
		var msgType = 4
		var title = "群艺考勤单点登录"
		var toChannel = "sms"
		var mobile = "13414852525"
		$.ajax({
			headers: {
		        Accept: "application/json; charset=utf-8"
		    },
			url: 'https://pushcenter-test.jifenzhi.com/pushServer/push/single',
			type: 'POST',
			contentType: "application/json",
			data: JSON.stringify({
				"content": content,
				"fromClientId": fromClientId,
				"fromUserId": fromUserId,
				"tableId": tableId,
				"msgType": msgType,
				"title": "群艺考勤单点登录",
				"toChannel": "sms",
				"toObjs": [{
					"mobile": "13414852525"
				}]
			}),
		})
		.done(function() {
			console.log("success");
		})
		.fail(function() {
			console.log("error");
		})
		.always(function() {
			console.log("complete");
		});
		
	}
	// 打开遮罩层
	function showMask(){
		$(".mask").css("height",$(document).height());
		$(".mask").css("width",$(document).width());
		$(".mask").show();
		$(".tipbox").show();
	}
	// 隐藏遮罩层  
	function hideMask(){
		$(".mask").hide();
		$(".tipbox").hide();
	}
	//设置居中
	function mainbox(){
	  var t = 0;
	  if($(window).width() > 767){
	    var t = ($(document).height() - $('.main').height()+30)/2;//pc
	  }else{
	    var t = ($(document).height() - $('.main').height())/2-60;//手机
	  }
	  if(t<0){
	    t = ($(document).height() - $('.main').height())/2 + 'px';//居中显示
	  }else{
	    t = t+'px';
	  }
	  $('.main').css('top', t);
	}

	  // 二维码
    $('.android').hover(function() {
     $('.qr-android').css('display', 'block');
    }, function() {
     $('.qr-android').css('display', 'none');
    });
   $('.iPhone').hover(function() {
     $('.qr-iPhone').css('display', 'block');
   }, function() {
     $('.qr-iPhone').css('display', 'none');
   });


	// 去空格
	function trim(e){
		return $.trim(e).val();
	}
	// 重置界面
	// 要对input进行清空，图标和登录按钮重置，图标有6个，用户、密码、企业代码、验证码、记住密码、显示密码
	function resetIcon(){
		$('input').val('');
		$('.ic-loginname').css('background-position', '-20px -40px');
		$('.ic-password').css('background-position', '-20px -60px');
		$('.ic-verifyCode').css('background-position', '-20px -100px');
		$('.ic-enterprise').css('background-position', '-20px 0');
		$('.visualPsw').css('background-position', '0 -20px');
		$("input[name='password']").prop("type", "password");
		$('.loginbtn').removeClass('active-btn').addClass('disable-btn').prop('disabled','true');
		$('.err-tip>p').hide();
		rememberPsw(1);
	}
	// 记住密码
	function rememberPsw(flag){
		if(!flag){
		  // 选中就显示打钩,并把勾选框状态设置为true，标志改为1
			$('.checkbox').css('background-position', '0 -140px');
			$('.rmbPsw').css('color','#228FFF');
			$("input[type='checkbox']").prop("checked", true); 
			flag = 1;
		}else{
			$('.checkbox').css('background-position', '-20px -140px');
			$('.rmbPsw').css('color','#A1A1A1');
			$("input[type='checkbox']").prop("checked", false); 
			flag = 0;
		}
	}
	//错误提示信息
	function errMsg(err){
	  $(".err-tip").html("<p>"+"<i class='ic-err'></i>"+err+"</p>");
	  $(".err-tip>p").css('display', 'block');
	}
	// 切换图标(亮)
	function changeIconBright(e){
		var targetHTML = e.target.parentNode.previousElementSibling;
		if(targetHTML.className.indexOf("ic-loginname") != -1){
			$('.ic-loginname').css('background-position', '0 -40px');
		}else if(targetHTML.className.indexOf("ic-password") != -1){
			$('.ic-password').css('background-position', '0 -60px');
		}else if(targetHTML.className.indexOf("ic-verifyCode") != -1){
			$('.ic-verifyCode').css('background-position', '0 -100px');
		}else if(targetHTML.className.indexOf("ic-enterprise") != -1){
			$('.ic-enterprise').css('background-position', '0 0');
		}
	}
	// 切换图标(暗)
	function changeIconDark(e){
		var targetHTML = e.target.parentNode.previousElementSibling;
		if(targetHTML.className.indexOf("ic-loginname") != -1){
			$('.ic-loginname').css('background-position', '-20px -40px');
		}else if(targetHTML.className.indexOf("ic-password") != -1){
			$('.ic-password').css('background-position', '-20px -60px');
		}else if(targetHTML.className.indexOf("ic-verifyCode") != -1){
			$('.ic-verifyCode').css('background-position', '-20px -100px');
		}else if(targetHTML.className.indexOf("ic-enterprise") != -1){
			$('.ic-enterprise').css('background-position', '-20px 0');
		}
	}
	// login按钮
	function changeLoginBtn(){
		var regTeShu = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？+-]")
		var zh = /^[\u4e00-\u9fa5]+$/
		var num = /[0-9]/
		// 如果第一个用户名没有隐藏类hide，表示当前面板是账号登录面板
		if (($('.loginname input'))[0].className.indexOf('hide') == -1 || sessionStorage.getItem('status') == "0") {
			$('.loginbtn').removeClass('active-btn').addClass('disable-btn').prop('disabled','true')
			if ($("input[name='loginname']").val().length == 0) {
				return errMsg('账号不能为空');
			// } else if (regTeShu.test($("input[name='loginname']").val()) || zh.test($("input[name='loginname']").val())) {
			// 	return errMsg('账号不能包含特殊字符或汉字')
			// } else if ($("input[name='loginname']").val().indexOf(' ') != -1) {
			// 	return errMsg('账号不能输入空格')
			} else if (num.test($("input[name='loginname']").val().substr(0, 1))) {
				if (!(/^1\d{10}$/.test($("input[name='loginname']").val()))) {
					return errMsg('手机号输入有误')
				} else {
					$('.err-tip>p').hide();
				}
			} else {
				$('.err-tip>p').hide();
			}
			// if ($("input[name='password']").val().indexOf(' ') != -1) {
			// 		return errMsg('密码不能输入空格')
			// }
			// if ($("input[name='loginname']").val().length > 0 && $("input[name='password']").val().length > 0) {
			// 	var bool = checkNewPass($("input[name='password']").val())
			// 	if (bool) {
			// 		$('.err-tip>p').hide();
			// 	}
			// } else {
			// 	$('.err-tip>p').hide();
			// }
			if($("input[name='loginname']").val().length > 0 && $("input[name='password']").val().length >= 6){
				$('.loginbtn').removeClass('disable-btn').addClass('active-btn').removeAttr('disabled');
			} else {
				$('.loginbtn').removeClass('active-btn').addClass('disable-btn').prop('disabled','true');
			}
		} else if (sessionStorage.getItem('status') == "1") {
			$('.loginbtn').removeClass('active-btn').addClass('disable-btn').prop('disabled','true')
			if ($("input[name='workname']").val().length == 0) {
				return errMsg('账号不能为空');
			// } else if (regTeShu.test($("input[name='workname']").val()) || zh.test($("input[name='workname']").val())) {
			// 	return errMsg('账号不能包含特殊字符或汉字')
			// } else if ($("input[name='workname']").val().indexOf(' ') != -1) {
			// 	return errMsg('账号不能输入空格')
			} else {
				$('.err-tip>p').hide();
			}
			// if ($("input[name='password']").val().indexOf(' ') != -1) {
			// 	return errMsg('密码不能输入空格')
			// }
			// if ($("input[name='workname']").val().length > 0 && $("input[name='password']").val().length > 0) {
			// 	var bool = checkNewPass($("input[name='password']").val())
			// 	if (bool) {
			// 		$('.err-tip>p').hide();
			// 	}
			// } else {
			// 	$('.err-tip>p').hide();
			// }
			if ($("input[name='workname']").val().length > 0 && $("input[name='password']").val().length >= 6 && $("input[name='enterprise']").val().length > 0) {
				$('.loginbtn').removeClass('disable-btn').addClass('active-btn').removeAttr('disabled');
			} else{
				$('.loginbtn').removeClass('active-btn').addClass('disable-btn').prop('disabled','true');
			}
		}
	}
	// 手机号码验证
	function checkPhone(val){ 
			if(!(/^1\d{10}$/.test(val))){
	      return false;
	    } else {
	      return true;
	    }
	}
	// 密码验证
	function checkPass(str){
	  if(str.length == 0){
	    errMsg('请输入密码');
	    return false;
	  }else if(str.length < 6){
	      errMsg('密码长度不低于6位');
	      return false;
	  }else {
	    return true;
	  }
	}
	// 设置新密码验证
	function checkNewPass(str) { 
		if (str.length < 6) {
			errMsg('密码长度不低于6位');
			return false;
		} else if (str.indexOf(' ') != -1) {
			errMsg('密码不能输入空格')
			return false;
		} else {
			var regLetter = /[A-Za-z]/;
			var regNum = /[0-9]/;
			var regTeShu =new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？+-]");
			var complex = 0;
			if (regLetter.test(str)) {
				++complex;
			}
			if (regNum.test(str)) {
				++complex;
			}
			if(regTeShu.test(str)){
				++complex;
			}
			if (complex < 2) {
				errMsg('密码含字母、数字、符号至少2种');
				return false;
			} else {
				return true;
			}
		}
	}
	// 字符串转base64
	function encode(str){
    // 对字符串进行编码
    var encode = encodeURI(str);
    // 对编码的字符串转化base64
    var base64 = btoa(encode);
    return base64;
	}
	// base64转字符串
	function decode(base64){
    // 对base64转编码
    var decode = atob(base64);
    // 编码转字符串
    var str = decodeURI(decode);
    return str;
	}
    // 请联系管理员的弹框隐藏
	function hideMask(){     
    $(".mask").hide();     
    $(".tipbox").hide();
    }
     // 请联系管理员的弹框显示
    function showMask(){     
    $(".mask").css("height",$(document).height());     
    $(".mask").css("width",$(document).width());     
    $(".mask").show();     
    $(".tipbox").show(); 
    } 
/* js执行代码 */
   $(document).ready(function() {
	//解决浏览器input密码框变黄色
	setTimeout(function(){
	  $('input[name="password"]').prop('type', 'password');
	},0);

	//设置居中
	mainbox();
	window.onresize = function(){
		mainbox();
	}
	//按回车键点击登录
	$("body").keydown(function(event) {
		if($(".loginbtn").prop('disabled') != true){
			if (event.keyCode == "13") {//keyCode=13是回车键
				$(".loginbtn").click();
			}
		}
	})
	//登录状态为1时
	if (loginStatus === 1) { 
		// $(".toggle-text").addClass('hide')
		$(".login-account-title").addClass('hide')
		$('.login-enterprise-title').removeClass('hide')
		$('.entrepreneur').removeClass('hide')
		$(".loginname-input").addClass('hide')
		$('.enterprise-input').removeClass('hide')
		sessionStorage.setItem('status', '1')
	}
//点击切换登录面板：
	$(".login-enterprise,.login-account").click(function(e) {
		var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
		if(keys) {
			for(var i = keys.length; i--;)
				document.cookie = keys[i] + '=0;expires=' + new Date(0).toUTCString()
		}
		if (location.host.indexOf(".com") !== -1) {
			if (location.host.indexOf("beta") != -1) { 
				return location.href = "https://auth-test.jifenzhi.info"			
			}
			location.href = location.href.replace('.com','.info')			
		 }
		if (location.host.indexOf(".info") !== -1) {
			if (location.host.indexOf("-test") != -1) { 
				return location.href = "https://authbeta.jifenzhi.com"			
			}
			location.href = location.href.replace('.info','.com')
		}
		// $(this).addClass('hide').siblings('p').removeClass('hide');
		// resetIcon();
		// // 如果classname含有‘enterprise’，就切换企业账号登录,否则就是账号登录
		// if(e.currentTarget.className.indexOf('enterprise') != -1){
		// 	// console.log('企业登录')
		// 	sessionStorage.setItem('status', '1');
		// 	$('.enterprise-input').removeClass('hide').siblings('input').addClass('hide')
		// 	$('.enterprise').show();
		// 	$('.entrepreneur').removeClass('hide')
		// 	$('.login-enterprise-title').removeClass('hide').siblings('h1').addClass('hide')
		// }else {
		// 	// console.log('账号登录')
		// 	sessionStorage.setItem('status', '0');
		// 	$('.loginname-input').removeClass('hide').siblings('input').addClass('hide')
		// 	$('.enterprise').hide();
		// 	$('.entrepreneur').addClass('hide')
		// 	$('.login-account-title').removeClass('hide').siblings('h1').addClass('hide')
		// }
	});
	// 手机 切换列表
	$('.wrap-toggle>ul>li').click(function(e) {
		resetIcon();
		var currentListClass = e.currentTarget.className;
		if(currentListClass === 'm-login-account'){
			$('.loginname-input').removeClass('hide').siblings('input').addClass('hide')
			$('.enterprise').hide();
			$('.m-login-account').css({'color': '#228FFF','border-bottom': '2px solid'})
			$('.m-login-enterprise').css({'color': '#999','border-bottom': 'none'})
		}else if(currentListClass === 'm-login-enterprise'){
			$('.enterprise-input').removeClass('hide').siblings('input').addClass('hide')
			$('.enterprise').show();
			$('.m-login-enterprise').css({'color': '#228FFF','border-bottom': '2px solid'})
			$('.m-login-account').css({'color': '#999','border-bottom': 'none'})
		}
	});

/* 1. input聚焦失焦切换icon  2.input失焦验证*/
	// 输入框聚焦切换icon
	 $("input").focus(function (e) {
		if (e.target.value !== '') {
			$('.err-tip>p').hide();
		}		
		changeIconBright(e)
	});
	// 输入框失焦切换icon，失焦不显示删除按钮，和失焦验证
	$("input").blur(function(e){
		// 如果没有输入框没有输入就使图标变灰色
		if (!$(this).get(0).value.length) {
			changeIconDark(e);
		}
	});
	$("input[name='loginname']").blur(function (event) {
		// checkPhone($("input[name='loginname']").val());
	});

// 清空输入框功能
	$("input").on("focus input propertychange change click", function(e) {
		e.stopPropagation();
		$(".del").hide();
		if ($(this).val()) {
			$(this).siblings(".del").css('display','inline-block')
		}
	});
	$(".del").click(function(e) {
	  e.stopPropagation();
	  $(this).siblings("input").val("");
	  $(this).css('display','none')
	  $('.loginbtn').removeClass('active-btn').addClass('disable-btn').prop('disabled','true');
	  changeIconDark(e);
	})
	// 点击其他地方隐藏X
	$(document).click(function(){
	  $(".del").hide();
	});

// 切换密码小眼睛
	$('.visualPsw,.enterprise-visualPsw').click(function() {
		if($("input[name='password'],input[name='newPsw']").prop('type') == 'password'){
		  $("input[name='password'],input[name='newPsw']").prop("type", "text");
		    $('.visualPsw').css('background-position', '-20px -20px');//pc
		}else{
		  $("input[name='password'],input[name='newPsw']").prop("type", "password");
		    $('.visualPsw').css('background-position', '0 -20px');//pc
		}
	});

// 记住密码
	$('.check').click(function() {
		var flag = 0;
		if($("input[type='checkbox']").prop("checked")){
			flag = 1
		}
		rememberPsw(flag);
	});


// 按钮灰暗切换
	$("input").on("input", function(e) {
	  	e.stopPropagation();
	  	if($(".verifyCode").css('display') != 'none'){
				if ($("input[name='verifyCode']").get(0).value.length > 0) {
					changeLoginBtn();
	  		}else {
	  			$('.loginbtn').removeClass('active-btn').addClass('disable-btn').prop('disabled','true');
	  		}
			} else {
				changeLoginBtn();
	  	}
	})
// 发送验证码按钮高亮
	$("input").on("input", function (e) {
		if (sessionStorage.getItem('status') == "2") {
			e.stopPropagation();
			// console.log($(this).get(0).value.length)
			if ($("input[name='phonenum']").get(0).value.length > 0) {
				var bool = checkPhone($("input[name='phonenum']").val());
				if (!bool) {
					$('.send-code').removeClass('active-btn').addClass('disable-btn').prop('disabled', 'true');
					$('.comfirebtn').removeClass('active-btn').addClass('disable-btn').prop('disabled', 'true');
					return errMsg("手机号输入有误");
				} else {
					$('.err-tip>p').hide();
					if (!isSendCode) { 
						$('.send-code').removeClass('disable-btn').addClass('active-btn').removeAttr('disabled');
					}
					// 忘记密码界面确定按钮是否高亮
					if ($("input[name='phonenum']").get(0).value.length > 0 && $("input[name='msgCode']").get(0).value.length > 0 && $("input[name='newPsw']").get(0).value.length > 0) {
						$('.comfirebtn').removeClass('disable-btn').addClass('active-btn').removeAttr('disabled');
					}
				}
			} else if ($("input[name='phonenum']").get(0).value.length == 0) {
				$('.send-code').removeClass('active-btn').addClass('disable-btn').prop('disabled', 'true');
				$('.comfirebtn').removeClass('active-btn').addClass('disable-btn').prop('disabled', 'true');
				return errMsg("请输入手机号");
			} else {
				$('.send-code').removeClass('active-btn').addClass('disable-btn').prop('disabled', 'true');
				$('.comfirebtn').removeClass('active-btn').addClass('disable-btn').prop('disabled', 'true');
			}
		}	
	})
// 点击发送验证码
  $('.send-code').on("click", function (e) {
		if ($(this).hasClass('active-btn')) {
      $.ajax({
        type: 'get',
        url: "/usercenter/authenticate/phonecode",
        data: {
          'phone': $("input[name='phonenum']").val()
        },
        success: function (res) {
					if (+res.code == 200) {
						$('.send-code').removeClass('active-btn')
						$('.send-code').html('重新发送(60秒)')
						isSendCode = !isSendCode;
						$count = 60;
						$countdown = setInterval(function() {
							$count--;
							$('.send-code').html('重新发送(' + $count + '秒)')
							if ($count < 0) {
								$('.send-code').addClass('active-btn')
								$('.send-code').html('发送验证码')
								if($("input[name='phonenum']").get(0).value.length == 11){
									$('.send-code').removeClass('disable-btn').addClass('active-btn').removeAttr('disabled');
									isSendCode = !isSendCode;
								} else {
									$('.send-code').removeClass('active-btn').addClass('disable-btn').prop('disabled','true');
								}
								clearInterval($countdown)
							}
						},1000)
          } else {
            errMsg(res.message);
          }
        },
        error:function(res){
          $('.wrapLoading').css('display','none');
          if(res.readyState === 4){
            alert('服务器异常，请稍后再试！');
          }else if(res.readyState === 0){
            alert('网络不给力，请稍后再试！');
          }
          lock = true;//如果业务执行失败，修改锁状态
        }
      })
    }
  });
// 忘记密码界面确定按钮高亮
	// $(".form-psw input").on("input", function(e) {
	//   	e.stopPropagation();
	//   	if($("input[name='phonenum']").get(0).value.length > 0 && $("input[name='msgCode']").get(0).value.length > 0 &&$("input[name='newPsw']").get(0).value.length > 0){
	//   		$('.comfirebtn').removeClass('disable-btn').addClass('active-btn').removeAttr('disabled');
	//   	}else {
	//   		$('.comfirebtn').removeClass('active-btn').addClass('disable-btn').prop('disabled','true');
	//   	}
  // })
// 忘记密码界面确定按钮点击
  $(".comfirebtn").on("click", function (e) {
		if ($(this).hasClass('active-btn')) {
			var bool = checkNewPass($("input[name='newPsw']").val())
			if (!bool) {
				return;
			}
			if ($("input[name='msgCode']").val().indexOf(' ') != -1) {
				return errMsg('验证码不能输入空格')
			}
      $.ajax({
        type: 'post',
        url: "/usercenter/authenticate/forgotPassword",
        data: {
          'phone': $("input[name='phonenum']").val(),
          'phoneCode': $("input[name='msgCode']").val(),
          'newPwd': $("input[name='newPsw']").val()
        },
        success: function (res) {
          if (+res.code == 200) {
            $('.comfirebtn').removeClass('active-btn').addClass('disable-btn').prop('disabled', 'true');
            $(".suctipsbox").css('display', 'block')
              $suctips = setTimeout(function() {
                $(".suctipsbox").css('display', 'none')
								clearInterval($suctips)
								sessionStorage.setItem('status', '0')
								location.href = location.href
              },3000)
          } else {
            errMsg(res.message);
          }
        },
        error:function(res){
          $('.wrapLoading').css('display','none');
          if(res.readyState === 4){
            alert('服务器异常，请稍后再试！');
          }else if(res.readyState === 0){
            alert('网络不给力，请稍后再试！');
          }
          lock = true;//如果业务执行失败，修改锁状态
        }
      })
    }
  })

	$('.forgetPsw').click(function(event) {
		resetIcon();
		if (loginStatus === 1) { 
			showMask();
			return;
		}
		var origin = '';
		if (!window.location.origin) {
			origin = window.location.protocol + '//' + window.location.hostname + (window.location.port ? ':' + window.location.port : '')
		} else {
			origin = window.location.origin
		}
		if (origin.indexOf('dev') != -1) {
			return location.href = "https://uc-dev.jifenzhi.info/forgetPassword.html?belongto=2"
		} else if (origin.indexOf('test') != -1) {
			return location.href = "https://uc-test.jifenzhi.info/forgetPassword.html?belongto=2"
		} else { 
			return location.href = "https://uc.jifenzhi.info/forgetPassword.html?belongto=2"
		}
		sessionStorage.setItem('status', '2');
		$('.form-psw').show().siblings('.login').hide();
		// changePassword();
		if($('.wrap-toggle').css('display') != 'none'){
			$('.m-forget').css('display','flex').siblings('ul').css('display','none');
			// $('.toggle-text').css('display','block');
		}
	});
		$('.to-login').click(function (event) {
		sessionStorage.setItem('status', '0');
		resetIcon();
		if($('.wrap-toggle').css('display') != 'none'){
			$('.m-forget').css('display','none').siblings('ul').css('display','flex');
			$('.form-psw').css('display','none').siblings('.login').css('display','block');
			$('.toggle-text').css('display','none');
		}else {
			$('.login').show().siblings('.form-psw').hide();	
		}
		location.href = location.href;
	});
// 点击登录按钮
	var num = 0;
	if (!window.localStorage.overTime) {
	  var overTime = new Date().getTime()+3600*1000;
	  window.localStorage.overTime = overTime;
	}
	if (sessionStorage.getItem('status') == "2") {
		$('.form-psw').show().siblings('.login').hide();
		// changePassword();
		if($('.wrap-toggle').css('display') != 'none'){
			$('.m-forget').css('display','flex').siblings('ul').css('display','none');
			// $('.toggle-text').css('display','block');
		}
	}
	if (sessionStorage.getItem('status') == "1" && loginStatus != '1') {
		$(".login-enterprise").addClass('hide').siblings('p').removeClass('hide');
		$('.enterprise-input').removeClass('hide').siblings('input').addClass('hide')
		$('.enterprise').show();
		$('.entrepreneur').removeClass('hide')
		$('.login-enterprise-title').removeClass('hide').siblings('h1').addClass('hide')
	}
	// 判断缓存里是否有账号密码和企业代码，有无过期：
	// 1如果有并且没有过期就自动填入表单并勾选中“记住密码”；
	// 2如果过期就清空表单并移除缓存里的账号密码和企业代码
	if (window.localStorage.name && window.localStorage.pd && +window.localStorage.overTime > new Date().getTime()) {
		$("input[ name='loginname' ]").val(decode(window.localStorage.name));
	  $("input[ name='workname' ]").val(decode(window.localStorage.name));		
		$("input[ name='password' ]").val(decode(window.localStorage.pd));
	  $("input[ name='enterprise' ]").val(decode(window.localStorage.code));		
	  $('.checkbox').css('background-position', '0 -140px'); 
		$("input[type='checkbox']").prop("checked", true); 
	  $("input[type='checkbox']").prop("checked", true); 
		$("input[type='checkbox']").prop("checked", true); 
		$('.loginbtn').removeClass('disable-btn').addClass('active-btn').removeAttr('disabled');
		flag = 1;
	}else if (+window.localStorage.overTime < new Date().getTime()) {
	  $("input[ name='loginname' ]").val('');
	  $("input[ name='password' ]").val('');
	  $("input[ name='username' ]").val('');
	  window.localStorage.removeItem('overTime');
	  window.localStorage.removeItem('name');
		window.localStorage.removeItem('pd');
	  window.localStorage.removeItem('code');		
	  $('.checkbox').css('background-position', '-20px -140px'); //pc
		$("input[type='checkbox']").prop("checked", false); 
	  $("input[type='checkbox']").prop("checked", false); 
		$("input[type='checkbox']").prop("checked", false); 
		$('.loginbtn').removeClass('active-btn').addClass('disable-btn').prop('disabled','true');
	  flag = 0;
	}
	var lock = true;//1.拦截频繁请求的锁
	//企业模式enterprise:${companycode}:${loginname} 
	// 账号模式local:${loginname}
	$(".loginbtn").click(function(e) {
		var loginname = '';
		var enterpriseCode = '';
		var username = '';
		var password = $("input[ name='password' ]").val();
		var code = $("input[ name='verifyCode' ]").val();
		// 如果第一个用户名没有隐藏类hide，表示当前面板是账号登录面板
		if (!navigator.onLine) { 
			return alert('无网络，请检查网络连接！');
		}
		$('.wrapLoading').css('display','block');
		if(($('.loginname input'))[0].className.indexOf('hide') == -1){
			loginname = $("input[name='loginname']").val();
			username = "local:" + loginname.trim();
		}else {
			loginname = $("input[name='workname']").val();
			enterpriseCode = $("input[ name='enterprise' ]").val();
			username = "enterprise:" + enterpriseCode + ":" + loginname.trim();
		}
		var type = username.split(":");
		// console.log(type[0])
    if (num>5) {
      if(!code){
        errMsg('请输入验证码');
      }else{
        if(!lock){// 2.判断该锁是否打开，如果是关闭的，则直接返回
          return false;
        }
        lock = false ; //3.进来后，立马把锁锁住
        $.ajax({
          type : 'POST',
          url : "/kaptcha/valid",
          data : {
            'code' : code
          },
          success : function(data) {
            if (data.success) {
              // var username = "workweixin:" + loginname;
              $("input[ name='username']").prop("value", username);
              var ajaxTimeOut = $.ajax({
                type : 'POST',
                url : "/valid/login",
                data : {
                  'username' : username,
                  'password' : password
                },	
                success : function(data) {
                  //登录成功
									$('.wrapLoading').css('display','none');
                  if (data.success) {
                    if ($("input[type='checkbox']").is(':checked')) {
                      window.localStorage.name = encode(loginname);
											window.localStorage.pd = encode(password);
											window.localStorage.code = encode(enterpriseCode);
                    }else{
                      window.localStorage.removeItem('name');
											window.localStorage.removeItem('pd');
											window.localStorage.removeItem('code');
                    }
                    $('.wrapLoading').css('display','block');
                    $("#login").submit();
									} else {//登录失败
										if (data.msg == 11) {
											return location.href = data.obj.usercenterUrl +"/bindPhone.html?belongto=" + data.obj.belongto + "&userId=" + data.obj.id
										} else if (data.msg == 12) { 
											return location.href = data.obj.usercenterUrl +"/firstLogin.html?belongto=" + data.obj.belongto + "&phone=" + data.obj.mobile
										}
										errMsg(data.obj);
										changeVerifyCode();
                    $("input[name='verifyCode']").val("");
                    lock = true;//如果业务执行失败，修改锁状态
                  }
                },
                error:function(data){
                  $('.wrapLoading').css('display','none');
                  if(data.readyState === 4){
                    alert('服务器异常，请稍后再试！');
                  }else if(data.readyState === 0){
                    alert('网络不给力，请稍后再试！');
                  }
                  lock = true;//如果业务执行失败，修改锁状态
                },
                timeout : 20000,
								complete: function (XMLHttpRequest, status) {
									$('.wrapLoading').css('display','none');
                  if(status=='timeout'){//超时,status还有success,error等值的情况
                    ajaxTimeOut.abort(); //取消请求
                    alert("网络不给力，请稍候再试！");
                    lock = true;//如果业务执行失败，修改锁状态
                  }
                },
                dataType : "json"
              });
            }else {
              errMsg(data.obj);
              changeVerifyCode();
              $("input[name='verifyCode']").val("");
              lock = true;//如果业务执行失败，修改锁状态
            }
          },
          error:function(data){
            $('.wrapLoading').css('display','none');
            if(data.readyState === 4){
              alert('服务器异常，请稍后再试！');
            }else if(data.readyState === 0){
              alert('网络不给力，请稍后再试！');
            }
            lock = true;//如果业务执行失败，修改锁状态
          },
          dataType : "json"
        });
      }
    }else{ //登录次数小于5 就判断登录
      // var username = "local:" + loginname;
      $("input[ name='username']").prop("value", username);
      if(!lock){// 2.判断该锁是否打开，如果是关闭的，则直接返回
        return false;
      }
			lock = false; //3.进来后，立马把锁锁住
      var ajaxTimeOut = $.ajax({
        type : 'POST',
        url : "/valid/login",
        data : {
          'username' : username.trim(),
          'password' : password
        },
        success : function(data) {
					$('.wrapLoading').css('display','none');
          if (data.success) {
            if ($("input[type='checkbox']").is(':checked')) {
              window.localStorage.name = encode(loginname);
							window.localStorage.pd = encode(password);
							window.localStorage.code = encode(enterpriseCode);							
            }else{
              window.localStorage.removeItem('name');
							window.localStorage.removeItem('pd');
              window.localStorage.removeItem('code');							
						}
						$('.wrapLoading').css('display','block');						
            $("#login").submit();
					} else {
						if (data.msg == 11) {
							return location.href = data.obj.usercenterUrl +"/bindPhone.html?belongto=" + data.obj.belongto + "&userId=" + data.obj.id
						} else if (data.msg == 12) { 
							return location.href = data.obj.usercenterUrl +"/firstLogin.html?belongto=" + data.obj.belongto + "&phone=" + data.obj.mobile
						}
            errMsg(data.obj);
            lock = true;//如果业务执行失败，修改锁状态
            num++;
            // 如果登录次数大于等于5 就显示验证码
            if (num==5) {
              $("#yzmImg").prop("src","/kaptcha/render?" + Math.floor(Math.random() * 100));
              $('.verifyCode').css('display','block');
              num++;
            }
          }
        },
				error: function (data) {
					$('.wrapLoading').css('display','none');
					if(data.readyState === 4){
						alert('服务器异常，请稍后再试！');
          }else if(data.readyState === 0){
            alert('网络不给力，请稍后再试！');
          }
          lock = true;//如果业务执行失败，修改锁状态
        },
        timeout : 20000,
				complete: function (XMLHttpRequest, status) {
					$('.wrapLoading').css('display','none');					
          if(status=='timeout'){//超时,status还有success,error等值的情况
            ajaxTimeOut.abort(); //取消请求
            alert("网络不给力，请稍后再试！");
            lock = true;//如果业务执行失败，修改锁状态
          }
        },
        dataType : "json"
      });
    }
  });
})