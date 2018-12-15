
var typeHeadSource = ['/加载中...稍后请重新打开'];
var typeHeadSourceMap = [];

$(function() {
	initTree([res.tree]);
	
	// 异步加载系统uri
	/*
	$.post(contextPath + "res/getResUri", null, function(r){
		typeHeadSource = r.arrayList;
		for (var i in typeHeadSource) {
			typeHeadSourceMap[typeHeadSource[i]] = 1;
		}
	});*/
});

var treeUtils = {childrenId:[]}
treeUtils.getChildrenIds = function(node) {
	this.childrenId.push(node.id);
	if (node.nodes) {
		for (var i = 0; i < node.nodes.length; i++) {
			this.getChildrenIds(node.nodes[i]);
		}
	}
	return this.childrenId;
}
treeUtils.compressNode = function(node) {
	var newNode = {id:node.id,t:node.text,i:this.getNodeType(node.icon)};
	if (node.nodes) {
		newNode.n = [];
		for (var i = 0; i < node.nodes.length; i++) {
			newNode.n.push(this.compressNode(node.nodes[i]));
		}
	}
	return newNode;
}

treeUtils.getNodeIcon = function(nodeType) {
	// -1资源 0目录 1菜单 2操作
	if (nodeType == 0) return "fa fa-folder";
	if (nodeType == 1) return "fa fa-file";
	if (nodeType == 2) return "fa fa-cogs";
	return "fa fa-home";
}
treeUtils.getNodeType = function(nodeIcon) {
	if (nodeIcon == "fa fa-folder") return 0;
	if (nodeIcon == "fa fa-file") return 1;
	if (nodeIcon == "fa fa-cogs") return 2;
	return -1;
}

function initTree(tree) {
	$('#tree').treeview({data:tree,levels:2,
		onNodeSelected:function(event, data) {
			// 根据节点类型，显示操作
			var nodeType = treeUtils.getNodeType(data.icon);
			if (nodeType == -1) {
				$("#addChildId").show();
				$(".operatorNode").hide();				
			}
			else if (nodeType == 2) {
				$("#addChildId").hide();
				$(".operatorNode").show();
			}
			else {
				$("#addChildId").show();
				$(".operatorNode").show();
			}
			
			move.refreshMoveButton();
			var top = $('#tree').find("[data-nodeid='" + data.nodeId + "']").position().top;
			$("#uri").css("margin-top", top);
			
			
			// 读取uri显示
			$("#uriList li:gt(0)").remove();
			if (data.id) {
				$("#uriList li:gt(0)").remove();
				$("#uriList").append('<li class="list-group-item" style="background-color: white;"><i class="fa fa-refresh fa-spin"></i></li>');
				
				/*
				$.post(contextPath + "resource/getUri", "resId=" + data.id, function(r){					
					$("#uriList li:gt(0)").remove();
					var uriList = [];
					for (var i = 0; r.uri && i < r.uri.length; i++) {
						uriList.push({uri:r.uri[i], uriIndex:r.uriIndex[i]});
					}
					$("#uriList").append(template('uriListTemplate', uriList));
				});*/
			}
			$("#uri").show();			
		},
		onNodeUnselected:function(event, data) {
			$("#uri").hide();
		},
		onNodeCollapsed:function(event, data) {
			var node = $('#tree').treeview('getSelected');			
			$('#tree').treeview('unselectNode', [node, {silent: true}]);
			$("#uri").hide();
		},
		onNodeExpanded:function(event, data) {
			var node = $('#tree').treeview('getSelected');			
			$('#tree').treeview('unselectNode', [node, {silent: true}]);
			$("#uri").hide();
		}
	});
}

function saveResource(removeIds) {
	var tree = JSON.stringify(treeUtils.compressNode($('#tree').treeview('getNode', 0)));	
	showLoading();
	var para = "tree=" + tree + "&removeIds=" + removeIds;
	$.post(contextPath + "auto/res/saveRes", para, function(r){
		alertSuccess("保存成功！");
		hideLoading();
	});
}


// >>>>>>>>>>>>>>>>>>>>>>>>>>addChild
function noteTypeChange(noteType) {
	$("#nodeGlyphicon").attr("class", treeUtils.getNodeIcon(noteType));
}

function addChild() {
	var oldValue = $("#addNodeType").val();
	$("#addNodeType").html("");	
	var selectNode = $('#tree').treeview('getSelected')[0];
	var nodeType = treeUtils.getNodeType(selectNode.icon);
	
	if (nodeType == -1) {
		$("#addNodeType").append('<option value="0">目录</option>');	
		noteTypeChange(0);
	}
	else if (nodeType == 0) {
		$("#addNodeType").append('<option value="1" selected>菜单</option>');	
		noteTypeChange(1);
	}
	else if (nodeType == 1) {
		$("#addNodeType").append('<option value="2" selected>操作</option>');	
		noteTypeChange(2);
	}
	
	$('#addChild').modal('show');
	
	action = function() {
		if (!$("#addChildForm").valid()) return;
		
		var selectNode = $('#tree').treeview('getSelected')[0];
		
		
		// insertRes(  parentId,  String resName, resType
		showLoading();
		var param = {parentId:selectNode.id,resName:$("#addNodeName").val(),resType:$("#addNodeType").val()};
		$.post(contextPath + "auto/res/insertRes", param, function(r){
			hideLoading();
			
			var icon = treeUtils.getNodeIcon($("#addNodeType").val());
			var nodes = $('#tree').treeview('getNodes');
			
			var childNode = {text:$("#addNodeName").val(),icon:icon,id:nodes.length};	
			
			if (!selectNode.nodes) {
				selectNode.nodes = [];
			}
			selectNode.nodes.push(childNode);
			selectNode.state.expanded = true;
			
			initTree([$('#tree').treeview('getNode', 0)]);
			$("#addChild").modal("hide");
		});
	}
}

function editNode() {
	var selectNode = $('#tree').treeview('getSelected')[0];
	$("#updateNodeGlyphicon").attr("class", selectNode.icon);
	$("#updateNodeName").val(selectNode.text);
	$("#editNode").modal("show");
	
	action = function() {
		if (!$("#editNodeForm").valid()) return;
		
		showLoading();
		var param = {id:selectNode.id,resName:$("#updateNodeName").val()};
		$.post(contextPath + "auto/res/updateRes", param, function(r){
			hideLoading();
			var selectedNode = $('#tree').treeview('getSelected')[0];
			selectedNode.text =  $("#updateNodeName").val();
			initTree([$('#tree').treeview('getNode', 0)]);
			$("#editNode").modal("hide");
		})
	}
}


// >>>>>>>>>>>>>>>>>remove
function removeNode() {
	var callback = function() {
		var selectedNode = $('#tree').treeview('getSelected')[0];
		showLoading();
		$.post(contextPath + "auto/res/deleteRes", {id:selectedNode.id}, function(r){
			hideLoading();
			
			var selectedNode = $('#tree').treeview('getSelected')[0];
			childrenId = [];
			var removeIds = treeUtils.getChildrenIds(selectedNode);
			
			var parentNode = $('#tree').treeview('getParent', selectedNode);
			// 只有一个节点时，删除[]，否则只删除数组里的元素
			if (parentNode.nodes.length == 1) {
				delete parentNode.nodes;
			}
			else {
				for (var i = 0; i < parentNode.nodes.length; i++) {
					if (parentNode.nodes[i].nodeId == selectedNode.nodeId) {
						parentNode.nodes.splice(i, 1);
						break;
					}
				}
			}
			
			initTree([$('#tree').treeview('getNode', 0)]);
			$("#uri").hide();
		})
	}
	
	var selectNode = $('#tree').treeview('getSelected')[0];	
	confirm("确定要删除'" + selectNode.text + "'及其子节点?", callback);
}

//>>>>>>>>>>>>>>>>>>>>URI begin
function addTypeHead(jObj) {
	jObj.typeahead({items:10,source:typeHeadSource});
}

function validateUri(obj) {
	var uri = $.trim($(obj).val());		 
	var u = uri.split("?")[0];
	if (typeHeadSourceMap[u] != 1) {
		return "uri不存在！";
	}
}

function addUri() {
	$("#uriUl").html(getUriLi('fa-plus-sign', 'addUriItem'));
	addTypeHead($("#typeaheadId"));
	$("#addUri").modal("show");
	
	action = function() {
		if (!$("#addUriModalForm").valid()) {
			return;
		}
		
		var uriArray = [];
		$("#uriUl [data-provide='typeahead']").each(function(){
			var uri = $.trim($(this).val());
			uriArray.push(uri);
		})
		
		var selectedNode = $('#tree').treeview('getSelected')[0];
		var parent = $('#tree').treeview('getParent', selectedNode);
		var parentType = treeUtils.getNodeType(parent.icon);
		// 父节点是菜单,记录menuId为了生成操作权限
		var menuId = parentType == 1 ? "&menuId=" + parent.id : "";
		
		showLoading();
		$.post(contextPath + "resource/saveUri", "resId=" + selectedNode.id + "&uri=" + uriArray + menuId, function(r){
			hideLoading();
			alertSuccess("添加成功！");			
			$("#uriList li:gt(0)").remove();		
			var uriList = [];
			for (var i = 0; r.uri && i < r.uri.length; i++) {
				uriList.push({uri:r.uri[i], uriIndex:r.uriIndex[i]});
			}
			$("#uriList").append(template('uriListTemplate', uriList));
			$("#addUri").modal("hide");
		});
	}
}

function removeUriItem(obj) {
	$(obj).parent().remove();
}

function addUriItem(obj) {
	$("#uriUl").append(getUriLi('fa-minus-sign', 'removeUriItem'));
	addTypeHead($("#uriUl [data-provide='typeahead']").last());
}

function getUriLi(icon, click) {
	var li = '<li class="list-group-item">\
		<input id="typeaheadId" type="text" class="form-control input-sm" data-provide="typeahead" required data-function="validateUri(this)" style="width:370px;float:left;margin:-5px">\
		<a href="#this" class="fa ' + icon + '" style="margin-left:12px;" onclick="' + click + '(this)"></a></li>';
	return li;
}

function removeUri(obj) {
	var callback = function() {
		// mongodb数组只有一条数据时，删除整条记录uri=-1表示删除
		var uri = $(obj).parent().parent().find("li").length == 2 ? "-1" : $(obj).attr("data-uri");
		
		showLoading();
		var resId = $('#tree').treeview('getSelected')[0].id;
		var para = "resId=" + resId + "&uri=" + uri + "&uriIndex=" + $(obj).attr("data-uri-index");
		$.post(contextPath + "resource/removeUri", para, function(r) {
			hideLoading();
			$(obj).parent().remove();	
			alertSuccess("删除成功！");	
		});
	}
	confirm("确定删除'" + $(obj).prev().text() + "'?", callback);
}


// >>>>>>>>>>>>>>>>>>>>>>>>>>move node begin
var move = {};
move.topNode = function() {
	var node = $('#tree').treeview('getSelected')[0];
	var nodeId = node.nodeId;	
	var parent = $('#tree').treeview('getParent', node);
	var tmpParentStr = JSON.stringify(parent);
	var tmpParent = JSON.parse(tmpParentStr);
	
	for (var i = 0; i < parent.nodes.length; i++) {
		if (i == 0) {
			parent.nodes[i] = node;
		}
		else if (nodeId >= parent.nodes[i].nodeId) {
			parent.nodes[i] = tmpParent.nodes[i-1];
		}
	}
	
	initTree([$('#tree').treeview('getNode', 0)]);
	
	var nodeId = $('#tree').treeview('getSelected')[0].nodeId;
	var top = $('#tree').find("[data-nodeid='" + nodeId + "']").position().top;
	$("#uri").css("margin-top", top);
	
	this.refreshMoveButton();
	saveResource();
}
move.upNode = function() {
	var node = $('#tree').treeview('getSelected')[0];
	var nodeId = node.nodeId;
	var tmpNodeStr = JSON.stringify(node);
	var tmpNode = JSON.parse(tmpNodeStr);
	var parent = $('#tree').treeview('getParent', node);
	
	var currentN = -1;
	for (var i = 0; i < parent.nodes.length; i++) {
		if (nodeId == parent.nodes[i].nodeId) {
			currentN = i;
			break;
		}
	}
	parent.nodes[currentN] = parent.nodes[currentN - 1];
	parent.nodes[currentN - 1] = tmpNode;
	
	initTree([$('#tree').treeview('getNode', 0)]);
	
	var nodeId = $('#tree').treeview('getSelected')[0].nodeId;
	var top = $('#tree').find("[data-nodeid='" + nodeId + "']").position().top;
	$("#uri").css("margin-top", top);
	
	this.refreshMoveButton();
	saveResource();
}
move.downNode = function() {
	var node = $('#tree').treeview('getSelected')[0];
	var nodeId = node.nodeId;
	var tmpNodeStr = JSON.stringify(node);
	var tmpNode = JSON.parse(tmpNodeStr);
	var parent = $('#tree').treeview('getParent', node);
	
	var currentN = -1;
	for (var i = 0; i < parent.nodes.length; i++) {
		if (nodeId == parent.nodes[i].nodeId) {
			currentN = i;
			break;
		}
	}
	parent.nodes[currentN] = parent.nodes[currentN + 1];
	parent.nodes[currentN + 1] = tmpNode;
	
	initTree([$('#tree').treeview('getNode', 0)]);
	
	var nodeId = $('#tree').treeview('getSelected')[0].nodeId;
	var top = $('#tree').find("[data-nodeid='" + nodeId + "']").position().top;
	$("#uri").css("margin-top", top);
	
	this.refreshMoveButton();
	saveResource();
}
move.refreshMoveButton = function() {
	$("#uri .fa").hide();
	
	var node = $('#tree').treeview('getSelected')[0];
	var nodeId = node.nodeId;
	var parent = $('#tree').treeview('getParent', node);
	if (parent.nodes == undefined) {
		return;
	}	
	
	var currentN = -1;
	for (var i = 0; i < parent.nodes.length; i++) {
		if (nodeId == parent.nodes[i].nodeId) {
			currentN = i;
			break;
		}
	}
		
	if (parent.nodes.length == 1) {
		return;
	}
	
	if (currentN == 0) {
		$("#uri .fa-arrow-down").show();
	}
	else if (currentN == parent.nodes.length - 1) {
		$("#uri .fa-step-backward").show();
		$("#uri .fa-arrow-up").show();	
	}
	else  {
		$("#uri .fa-step-backward").show();
		$("#uri .fa-arrow-up").show();
		$("#uri .fa-arrow-down").show();
	}
}
