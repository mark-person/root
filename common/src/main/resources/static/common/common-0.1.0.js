
$.ajaxSetup({
	error: function(r, textStatus, errorThrown) {
		var msg = "";
		if (r.responseJSON) {
			msg = "[" + r.responseJSON.errorCode + "]" + r.responseJSON.errorInfo;
		}
		$('#loading').modal('hide');
		alertDanger("error:" + r.status + "|" + this.url + "|" + msg);
	}
});


var LOADING = '\
<div class="modal" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static" style="z-index:10000">\
	<div class="modal-dialog modal-sm" role="document">\
	<div class="modal-content" style="height:50px;margin-top:180px;text-align:center">\
		<div style="padding-top:13px;"><span class="fa fa-refresh fa-spin"></span>请稍候...</div>\
	</div>\
	</div>\
</div>';

var CONFIRM = '\
<div class="modal fade" id="myConfig"><div class="modal-dialog">\
<div class="modal-content">\
	<div class="modal-header"><h5 class="modal-title">确认</h5><button type="button" class="close" data-dismiss="modal">&times;</button></div>\
	<div class="modal-body">\
		<div class="form-group" style="text-align: center;margin-bottom:-5px"><span id="myConfigMsg"></span></div>\
	</div>\
	<div class="modal-footer"><button class="btn btn-primary btn-sm" onclick="myConfigOk()" type="button">确定</button>\
	<button class="btn btn-default btn-sm" data-dismiss="modal" type="button">关闭</button></div>\
</div>\
</div></div>';

$(function() {
	$('body').append(LOADING + CONFIRM + '<div id="myAlert"><strong id="myAlertMsg"></strong><span></span></div>');
})

function alertShow(msg, cls, time) {
	$("#myAlertMsg").text(msg);
	$("#myAlert").attr("class", "alert " + cls);
	$("#myAlert").show();
	if (time == 0) {
		$("#myAlert span").html('<a href="javascript:$(\'#myAlert\').hide();">关闭</a>')
	}
	else {
		setTimeout('$("#myAlert").hide();', time);
	}
}
function alertSuccess(msg) {msg=msg==undefined?"操作成功！":msg;alertShow(msg, "alert-success", 2000)}
function alertInfo(msg) {alertShow(msg, "alert-info", 2500)}
function alertWarning(msg) {alertShow(msg, "alert-warning", 2500)}
function alertDanger(msg) {alertShow(msg, "alert-danger", 0)}

function confirm(msg, func) {
	$("#myConfig").data("func", func);
	$("#myConfigMsg").text(isNaN(msg) ? msg : "确定要删除'" + msg + "'？");
	$("#myConfig").modal('show');
}

function showLoading() {
	$('#loading').modal('show');
	$($(".modal-backdrop")[$(".modal-backdrop").length - 1]).css("z-index", "9999");
	$("#loading .modal-content").hide();
	$($(".modal-backdrop")[$(".modal-backdrop").length - 1]).css("opacity", "0");
	// n毫秒没有加载完页面才出现loading
	$("#loading").data("isShowLoading", true);
	setTimeout(function() {
		if ($("#loading").data("isShowLoading")) {
			$("#loading .modal-content").show();
			$(".modal-backdrop").css("opacity", "0.4");
		}
	}, 300);
}

function hideLoading() {
	$("#loading").data("isShowLoading", false);
	$('#loading').modal('hide');	
}


// 分页begin >>>>>>>>>>
var Page = function(obj) {
	this.url = obj.url;
	this.data = obj.data;
	this.pageDiv = obj.pageDiv;
	
	// 存储起来，以便$("#pageDiv").query()使用
	$(this.pageDiv).data("page", this);
	
	// 排序图标
	this.pageDiv.find("th[data-order-name]").each(function(){
		if (!$(this).hasClass("sorting-asc") && !$(this).hasClass("sorting-desc")) {
			$(this).addClass('sorting');
		}
		$(this).append('<i class="fa"></i>');		
	});
	
	this.queryPage = function(pageNumber) {
		var page = this;
		showLoading();
		this.pageDiv.find("[name=pageNumber]").val(pageNumber ? pageNumber : 1);
		
		$.post(this.url, this.pageDiv.find("form").serialize(), function(r) {
			page.refreshPage(r);
			hideLoading();
		});
	};
	
	this.refreshPage =  function(data) {
		if (!data) return;
		var pageTable = this.pageDiv.find("table");
		var templateId = this.pageDiv.attr("id") + "Template";
		this.pageDiv.find("script").attr("id", templateId);
		pageTable.find("tbody").html(template(templateId, data))
		this.pageDiv.find(".pagination").show();
		this.refreshFooter(data.page);
	}
	
	this.refreshFooter = function(p) {
		var page = this;
		// p.pageSize每页几条记录 ，p.totalRows总记录数 ， p.pageNumber当前页
		
		var pageNumUL = this.pageDiv.find(".pagination:eq(0)");
		pageNumUL.empty();
		
		this.pageDiv.find("[name=pageSize]").show();
		this.pageDiv.find(".totalRows").find("b").text(p.totalRows);
		if (p.totalRows == 0) {
			this.pageDiv.find(".totalRows").css("width", "100%");
			this.pageDiv.find("[name=pageSize]").hide();
			return;
		}
		this.pageDiv.find(".totalRows").css("width", "auto");
	
		
		var totalNum = Math.ceil(p.totalRows/p.pageSize);
				
		if (p.pageNumber == 1) {
			pageNumUL.append('<li class="page-item disabled"><a class="page-link">«</a></li>');
		}
		else {
			var li = $('<li class="page-item"><a class="page-link" href="#this">«</a></li>');
			li.find("a").bind("click", function() {page.queryPage(1)});
			pageNumUL.append(li);
		}
		var begin = p.pageNumber <= 3 ? 1 : p.pageNumber - 2;
		for (var i = begin; i < begin + 5 && i <= totalNum; i++) {
			var activeClass = (i == p.pageNumber) ? ' active' : '';
			var li = $('<li class="page-item' + activeClass + '"><a class="page-link" href="#this">' + i + '</a></li>');
			li.find("a").bind("click", function() {page.queryPage($(this).text())});
			pageNumUL.append(li);
		}
		if (p.totalRows == 0 || p.pageNumber == totalNum) {
			pageNumUL.append('<li class="page-item disabled"><a class="page-link">»</a></li>')
		}
		else {
			var li = $('<li class="page-item"><a class="page-link" href="#this" data-num="' + totalNum + '">»</a></li>');
			li.find("a").bind("click", function() {page.queryPage($(this).attr("data-num"))});
			pageNumUL.append(li);
		}
		
		// activeSorting
		this.pageDiv.find(".sorting, .sorting-asc, .sorting-desc").unbind("click");
		this.pageDiv.find(".sorting, .sorting-asc, .sorting-desc").click(function() {
			var orderName = $(this).attr("data-order-name");
			page.pageDiv.find("[name=orderName]").val(orderName);
			if ($(this).hasClass("sorting-desc")) {
				page.pageDiv.find("[name=orderType]").val("asc");
				page.pageDiv.find(".sorting-asc, .sorting-desc").addClass("sorting");
				page.pageDiv.find(".sorting-asc").removeClass("sorting-asc");
				page.pageDiv.find(".sorting-desc").removeClass("sorting-desc");
				$(this).addClass("sorting-asc");
			} else {
				page.pageDiv.find("[name=orderType]").val("desc");
				page.pageDiv.find(".sorting-asc, .sorting-desc").addClass("sorting");
				page.pageDiv.find(".sorting-asc").removeClass("sorting-asc");
				page.pageDiv.find(".sorting-desc").removeClass("sorting-desc");
				$(this).addClass("sorting-desc");
			}
			page.queryPage();
		});
	}
	
	var page = this;
	// pageSize change
	this.pageDiv.find("[name=pageSize]").bind("change", function() {page.queryPage()});
	// search button
	this.pageDiv.find(".fa-search").parent("button").bind("click", function() {page.queryPage()});
	this.refreshPage(this.data);
};

(function(){
	$.fn.extend({page:function(obj) {obj.pageDiv = $(this);return new Page(obj);}});
	$.fn.extend({query:function() {$(this).data("page").queryPage();}});
})($);
// 分页end <<<<<<<<<




// 增删改begin >>>>>>
var SUCCESS = 1;
var EXIST = 0;
function add(callback) {
	$('#addForm')[0].reset();
	$('#add').modal('show');
	action = function() {
		if (!$("#addForm").valid()) return;
		showLoading();
		$.post(controllerPath + "insert", $("#addForm").serialize(), function(r) {
			if (r.result === SUCCESS) {
				$("#pageDiv").query();
				$('#add').modal('hide');
				alertSuccess();
			}
			else if (r.result === EXIST) {
				alertWarning(EXIST_MSG);
				hideLoading();
			}
			callback ? callback(r) : null;
		});
	}
}
function edit(id, getCallback, updateCallback) {
	showLoading();
	$.post(controllerPath + "get", {id:id}, function(r) {
		hideLoading();
		$('#edit').modal('show');
		$("#editTemplate").nextAll().remove();
		$("#editTemplate").parent().append(template("editTemplate", r));
		getCallback ? getCallback(r) : null;
	});
	action = function() {
		if (!$("#editForm").valid()) return;
		showLoading();
		$.post(controllerPath + "update", $("#editForm").serialize(), function(r) {
			if (r.result === SUCCESS) {
				$('#edit').modal('hide');
				$("#pageDiv").query();
				alertSuccess();
			}
			else if (r.result === EXIST) {
				alertWarning(EXIST_MSG);
				hideLoading();
			}
			updateCallback ? updateCallback(r) : null;
		});
	}
}
function remove(id) {
	confirm(id, function() {
		showLoading();
		$.post(controllerPath + "delete", {id:id}, function(r) {
			r.result == SUCCESS ? $("#pageDiv").query() : alertDanger("error return " + r.result);
		});
	});
}
//增删改end >>>>>>








