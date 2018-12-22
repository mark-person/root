
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
		if (c[i].state.checked == 1 || c[i].state.checked == 2) {
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
	var newNode = {id:node.id,text:node.text,icon:node.icon,state:{},nodes:node.nodes};
	
	if (!node.id || resMap[node.id] == 1) {
		// 存在已经选择的节点
		for (i in resMap) {
			newNode.state.checked = 1;
			break;
		}
	}
	
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
	$.post(controllerPath + "getAuthorize", "accountId=" + $("#grantAccountId").val(), function(r){
		if (r.result == -1) {
			// 刚开始没有数据时
			initTree([{text:"资源", icon:"fa fa-home"}]);
		}
		else {
			var resMap = [];
			for (var i = 0;r.resIds && i < r.resIds.length; i++) {
				if (r.resIds[i]) {
					resMap[r.resIds[i]] = 1;
				}				
			}
			initTree([treeUtils.decompressNode(r.tree, resMap)]);			
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
}

// 装载时半选状态
function loadIndeterminate(newNode, node, resMap) {
	if (newNode.state.checked != 1 && newNode.state.checked != 2) {
		return;
	}
	if (node.nodes) {
		var no = hasNoChecked(node.nodes, resMap);
		if (no) {
			newNode.state.checked = 2;
		}
		else {
			newNode.state.checked = 1;
		}
	}
} 

function hasNoChecked(nodes, resMap) {
	for (var i = 0; i < nodes.length; i++) {
		if (resMap[nodes[i].id] != 1) return true;
		if (nodes[i].nodes) {
			if (hasNoChecked(nodes[i].nodes, resMap)) return true;
		}
	}	
	return false;
}

function initTree(tree) {
	$('#tree').treeview({data:tree,levels:2,showCheckbox:true,highlightSelected:false,
		onNodeChecked:function(event, node) {
			var n = $('#tree').treeview('getNode', node.nodeId);
			var childrenNode = treeUtils.getChildrenNodes(node);
			for (var i = 0; i < childrenNode.length; i++) {				
				var node = $('#tree').treeview('getNode', childrenNode[i].nodeId);				
				node.state.checked = 1;
			}
			clickIndeterminate(node);			
			refreshHint();			
		},
		onNodeUnchecked:function(event, node) {
			var n = $('#tree').treeview('getNode', node.nodeId);
			var childrenNode = treeUtils.getChildrenNodes(node);	
			for (var i = 0; i < childrenNode.length; i++) {				
				var node = $('#tree').treeview('getNode', childrenNode[i].nodeId);				
				node.state.checked = 0;				
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
	var nodeLen = treeUtils.getChildrenNodes(parent).length;
	var checkLen = treeUtils.getCheckedChildrenNode(parent).length;	
	if (checkLen == 0) {
		parent.state.checked = 0;
	}
	else if (nodeLen != checkLen) {	
		parent.state.checked = 2;	
	}
	else {
		parent.state.checked = 1;			
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
	var checkedIds = [];
	if (node.state.checked != 0) {
		checkedIds.push(node.id);
		var checkedNode = treeUtils.getCheckedChildrenNode(node);
		for (var i = 0; i < checkedNode.length; i++) {
			checkedIds.push(checkedNode[i].id);
		}
	}
	
	showLoading();
	var para = "accountId=" + $("#grantAccountId").val() + "&resIds=" + checkedIds;
	$.post(controllerPath + "saveAuthorize", para, function(r) {
		if (r.result == 0 || r.result == 1) {
			hideLoading();
			alertSuccess();
			$('#grantModal').modal('hide');
		}
	});
}