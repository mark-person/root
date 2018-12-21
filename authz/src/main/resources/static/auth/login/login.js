
$.ajaxSetup({
	error: function(r, textStatus, errorThrown) {
		var msg = "";
		if (r.responseJSON) {
			msg = "[" + r.responseJSON.errorCode + "]" + r.responseJSON.errorInfo;
		}
		$('#b').attr("data-content", "error:" + r.status + "|" + this.url + "|" + msg);
		$('#b').popover('show');
	}
});



$(function () {
	var f = '<div style="margin-top:50px">\
	<form style="width:400px;margin: 0 auto;">\
		<div class="input-group mb-3" style="width:400px">\
		<div class="input-group-prepend"><span class="input-group-text"><i class="fa fa-user"></i></span></div>\
			<input type="text" class="form-control" placeholder="请输入您的账号"\
				id="a" data-toggle="popover" data-content="请输入您的账号！" data-placement="right" data-trigger="manual" value="admin">\
		</div>\
		<div class="input-group" style="width:400px;margin-top:10px">\
			<div class="input-group-prepend"><span class="input-group-text"><i class="fa fa-lock"></i></span></div>\
			<input type="password" class="form-control" placeholder="请输入您的密码" \
				id="p" data-toggle="popover" data-content="请输入您的密码！" data-placement="right" data-trigger="manual" value="admin">\
		</div>\
		<div class="input-group" style="margin-top:10px" id="loginBtnDiv">\
			<button type="button" class="btn btn-primary popover-show"  style="width:400px" onclick="login()"\
				id="b" data-toggle="popover" data-content="" data-placement="right" data-trigger="manual">登录\
			</button>\
		</div>\
	</form>\
	</div>';
	$("body").html(f);
		
	$("#a").focus(function(){
		$('#a').popover('hide');
		$('#b').popover('hide');
	});
	
	$("#p").focus(function(){
		$('#p').popover('hide');
		$('#b').popover('hide');
	});
	
});

var RESULT_SUCCESS = 1;
var LOGIN_MSG = [];
LOGIN_MSG[-1] = "账号或密码错误";
LOGIN_MSG[-2] = "账号异常";

function login() {
	var a = $.trim($('#a').val());
	var p = $.trim($('#p').val());
	if (a == "") {
		$('#a').popover('show');
		return;
	}
	if (p == "") {
		$('#p').popover('show');
		return;
	}
	
	$("#b").attr("disabled", true);
	$("#b").text("登录中...");
	
	var v = function() {
		var c = $('meta[id="DhefwqGPrzGxEp9hPaoag"]').attr("content");
		c = c.substring(c.length - 21);
		var n = "";
		for (i in c) {
			n += (c.charCodeAt(i) + "DhefwqGPrzGxEp9hPaoag".charCodeAt(i)) + "";
		}
		return n;
	}
 
	$.post(contextPath + "auto/login/doLogin", {a:a,p:p,v:v()}, function(r) {
		if (r.value === RESULT_SUCCESS) {
			location.href = contextPath + "auto/index/menu";
		}
		else {
			$('#b').attr("data-content", LOGIN_MSG[r.value]);
			$('#b').popover('show');
			$("#b").attr("disabled", false);
			$("#b").text("登录");
		}
	});	
}