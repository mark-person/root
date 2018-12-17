
var treeUtils = {childrenNode:[]};
treeUtils.getChildrenNodes = function(node, recursion) {
	if (!recursion) {
		this.childrenNode = [];
	}	
	else {
		this.childrenNode.push(node);
	}
	
	if (node.nodes) {
		for (var i = 0; i < node.nodes.length; i++) {
			this.getChildrenNodes(node.nodes[i], true);
		}
	}
	return this.childrenNode;
}
treeUtils.getCheckedChildrenNode = function(node) {
	var r = [];
	var c = this.getChildrenNodes(node);
	for (var i = 0; i < c.length; i++) {
		if (c[i].state.checked == true) {
			r.push(c[i]);
		}
	}
	return r;
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
treeUtils.decompressNode = function(node, resMap) {
	var newNode = {id:node.id,text:node.text,icon:node.icon,state:{}};	
	newNode.state.checked = true;
	/*
	if (!node.id || resMap[node.id] == 1) {
		// 存在已经选择的节点
		for (i in resMap) {
			newNode.state.checked = true;
			break;
		}
	}*/
	// 装载时半选状态
	loadIndeterminate(newNode, node, resMap);
	if (node.nodes) {
		newNode.nodes = [];
		for (var i = 0; i < node.nodes.length; i++) {
			newNode.nodes.push(this.decompressNode(node.nodes[i], resMap));
		}
	}
	return newNode;
}

function initResource() {
	$('#tree').html("");
	$('#loading').modal('show');
	$.post(contextPath + "auto/grant/getAuthorize", "accountId=" + $("#grantAccountId").val(), function(r){
		if (r.result == -1) {
			// 刚开始没有数据时
			var tree = [{text:"资源", icon:"fa fa-home"}];
			initTree(tree);
		}
		else {		
			var tree = r.tree;
			var resMap = [];
			for (var i = 0;r.resIds && i < r.resIds.length; i++) {
				if (r.resIds[i]) {
					resMap[r.resIds[i]] = 1;
				}				
			} 
			initTree([treeUtils.decompressNode(r.tree)]);			
			refreshHint();		
		}
		$('#loading').modal('hide');
	});
}

function refreshHint() {
	var count = [0, 0, 0];
	var node = $('#tree').treeview('getNode', 0);
	var checkedNode = treeUtils.getCheckedChildrenNode(node);
	for (var i = 0; i < checkedNode.length; i++) {
		// noteType 0目录 1菜单 2操作
		var noteType = treeUtils.getNodeType(checkedNode[i].icon);
		count[noteType]++;
	}
	
	$("#viewFolderN").text(count[0]);
	$("#viewMenuN").text(count[1]);
	$("#viewActionN").text(count[2]);
	
	
	
	var treeNode = $("#tree [data-nodeid]");
	$("#tree [data-nodeid]").each(function(i, o) {
		if ($(o).css("background-color") == "rgb(255, 255, 253)") {
			var t = $(o).find(".fa-check-square");
			t.removeClass("fa-check-square");
			t.addClass("fa-check-square-o");
		}
	})
	
	
//	var o = $("[data-nodeid=1]").find(".fa-check-square-o");
//	o.removeClass("fa-check-square-o");
//	o.addClass("fa-check-square");
}

// 装载时半选状态
function loadIndeterminate(newNode, node, resMap) {
	
	
	if (!newNode.state.checked) {
		return;
	}
	
	if (!node.n) {
		newNode.backColor = "#fffffe";
		//newNode.color = "white";	
	}
	else if (node.n) {
		var no = hasNoChecked(node.n, resMap);
		if (no) {
			newNode.backColor = "#fffffd";
			//newNode.color = "black";	
		}
		else {
			newNode.backColor = "#fffffe";
			//newNode.color = "white";
		}
	}
} 

function hasNoChecked(nodes, resMap) {
	for (var i = 0; i < nodes.length; i++) {
		if (resMap[nodes[i].id] != 1) return true;
		if (nodes[i].n) {
			if (hasNoChecked(nodes[i].n, resMap)) return true;
		}
	}	
	return false;
}

function initTree(tree) {
	$('#tree').treeview({data:tree,levels:2,showCheckbox:true,highlightSelected:false,
		onNodeChecked:function(event, node) {
			var n = $('#tree').treeview('getNode', node.nodeId);
			n.backColor = "#fffffe";
			//n.color = "white";
			
			var childrenNode = treeUtils.getChildrenNodes(node);
			for (var i = 0; i < childrenNode.length; i++) {				
				var node = $('#tree').treeview('getNode', childrenNode[i].nodeId);				
				node.state.checked = true;
				node.backColor = "#fffffe";
				//node.color = "white";
			}
			clickIndeterminate(node);			
			refreshHint();			
		},
		onNodeUnchecked:function(event, node) {
			var n = $('#tree').treeview('getNode', node.nodeId);
			n.backColor = "white";
			//n.color = "black";
			
			var childrenNode = treeUtils.getChildrenNodes(node);	
			for (var i = 0; i < childrenNode.length; i++) {				
				var node = $('#tree').treeview('getNode', childrenNode[i].nodeId);				
				node.state.checked = false;
				node.backColor = "white";
				// node.color = "black";				
			}			
			clickIndeterminate(node);		
			refreshHint();
		}
	})
}

// onNodeChecked和onNodeUnchecked时半选状态
function clickIndeterminate(node) {
	if (node.nodeId == 0) return;
	
	var parent = $('#tree').treeview('getParent', node);
	if (node.state.checked) parent.state.checked = true;
		
	var nodeLen = treeUtils.getChildrenNodes(parent).length;
	var checkLen = treeUtils.getCheckedChildrenNode(parent).length;	
	if (nodeLen != checkLen) {		
		parent.backColor = "#fffffd";
		//parent.color = "black";		
	}
	else {
		parent.backColor = "#fffffe";
		//parent.color = "black";				
	}	
	
	if (parent.state.checked == false) {
		parent.backColor = "white";
		//parent.color = "black";
	}
	clickIndeterminate(parent);
}

function grant(accountId, viewName) {
	// 初始化页面
	$("#grantAccountId").val(accountId);
	$("#grantViewName").text(viewName);
	$("#viewFolderN,#viewMenuN,#viewActionN").text(0);
	initResource();
	$('#grantModal').modal('show');
}

function authorize() {
	var node = $('#tree').treeview('getNode', 0);
	var checkedNode = treeUtils.getCheckedChildrenNode(node);
	var checkedIds = [];
	for (var i = 0; i < checkedNode.length; i++) {
		checkedIds.push(checkedNode[i].id);
	}
	
	showLoading();
	var para = "accountId=" + $("#grantAccountId").val() + "&resIds=" + checkedIds;
	$.post(contextPath + "saveAuthorize", para, function(r) {
		if (r.result == 0 || r.result == 1) {
			hideLoading();
			alertSuccess();
			$('#grantModal').modal('hide');
		}
	});
}