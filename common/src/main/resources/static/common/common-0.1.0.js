
var LOADING = '\
<div class="modal" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static" style="z-index:10000">\
	<div class="modal-dialog" role="document" style="width:120px">\
	<div class="modal-content" style="height:50px;margin-top:180px;text-align:center">\
		<div style="padding-top:13px;"><span class="fa fa-refresh fa-spin"></span>请稍候...</div>\
	</div>\
	</div>\
</div>';

$(function() {
	$('body').append(LOADING);
})

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
	this.url = contextPath + obj.url;
	this.data = obj.data;
	this.pageDiv = obj && obj.pageDiv ? obj.pageDiv : $("#pageDiv");
	
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
		pageTable.find(">tbody").find(">tr:gt(1)").remove();
		var templateHtml = pageTable.find(">tbody").find(">tr:eq(1)").html();
		$("#pageTemplate").text("{{each arrayList as v i}}<tr>" + templateHtml + "</tr>{{/each}}");
		pageTable.find(">tbody").append(template("pageTemplate", data));
		
		this.refreshFooter(data.page);
		this.pageDiv.find(".pagination").show();
	}
	
	this.refreshFooter = function(p) {
		var page = this;
		// p.pageSize每页几条记录 ，p.totalRows总记录数 ， p.pageNumber当前页
		
		var pageNumUL = this.pageDiv.find(".pagination:eq(0)");
		pageNumUL.empty();
		
		$("#totalRows").text(p.totalRows);
		if (p.totalRows == 0) return;
		
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
}
// 分页end <<<<<<<<<


