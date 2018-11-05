
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



