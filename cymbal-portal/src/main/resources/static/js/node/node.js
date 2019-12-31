$(document).ready(function () {
    loadServerInfo();
    changeContentHeader('主机列表', '主机管理', '主机列表');
});

function loadServerInfo() {
    initBootstrapTable("serverInfoTable");
}

$("#uploadServerForm").submit(function () {

    var filename = $("#serverInfoFile").val();
    if (filename == "") {
        alert("请选择上传的文件!");
        return false;
    }
    if (filename.substr(filename.lastIndexOf(".")) != ".xlsx") {
        alert("请选择后缀为.xlsx的文件!");
        return false;
    }

    var formData = new FormData($(this)[0]);
    var table = $('#serverInfoTable');
    table.bootstrapTable('showLoading');
    $.ajax({
        url: '/nodes',
        type: 'POST',
        data: formData,
        cache: false,
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function (response) {
            table.bootstrapTable('hideLoading');
            table.bootstrapTable('refresh');
        }
    });

    $('#uploadFileModal').modal('hide');

    return false;
});

function serverIpFormatter(value, row) {
    var description = "";
    if (row["description"]) {
        description = '<i class="glyphicon glyphicon-flag pull-right text-red" data-toggle="tooltip" data-original-title="' + row["description"] + '"></i>';
    }

    return '<a class="edit ml10" href="javascript:void(0);">' + value + description + '</a>';
}

window.serverIpEvents = {
    'click .edit': function (e, value, row, index) {
        loadPage('/nodes/' + row.id + '/instances/page');
        // 防止冒泡，避免触发表格的行选中事件
        e.stopPropagation();
    }
};

function nodeStatusFormatter(value) {
    if (value == "UNINITIALIZED") {
        return "未初始化";
    } else if (value == "INITIALIZED") {
        return "已初始化";
    } else if (value == "DOWN") {
        return "已下线";
    }
}

function editSelectedServerInfo() {
    var selectedTable = $('#serverInfoTable').bootstrapTable('getSelections');
    if (selectedTable.length != 1) {
        alert("请选中一项进行修改！");
        return;
    }
    showServerModal(selectedTable[0]);
}

function showServerModal(data) {
    //模态展示
    $('#node_id').val(data.id);
    $('#node_ip').val(data.ip);
    $('#node_host').val(data.host);
    $('#node_env').val(data.env);
    $('#node_idc').val(data.idc);
    $('#node_totalMemory').val(data.totalMemory);
    $('#node_freeMemory').val(data.freeMemory);
    $('#node_password').val(data.password);
    $('#node_status').val(data.status);
    $('#description').val(data.description);

    $('.selectpicker').selectpicker('refresh')
    $("#serverModal").modal("show");
}

function saveNodeInfo() {
    var data = {
        "id": $('#node_id').val(),
        "ip": $('#node_ip').val(),
        "host": $('#node_host').val(),
        "env": $('#node_env option:selected').val(),
        "idc": $('#node_idc option:selected').val(),
        "totalMemory": $('#node_totalMemory').val(),
        "freeMemory": $('#node_freeMemory').val(),
        "password": $('#node_password').val(),
        "status": $('#node_status option:selected').val(),
        "description": $('#description').val()
    };
    var table = $('#serverInfoTable');
    table.bootstrapTable('showLoading');
    $.ajax({
        url: '/nodes/' + data.id,
        type: 'PUT',
        data: JSON.stringify(data),
        cache: false,
        contentType: 'application/json',
        success: function () {
            table.bootstrapTable('refresh');
            table.bootstrapTable('hideLoading');
        }
    });

    $('#serverModal').modal('hide');
}

function initSelectedNode() {
    var table = $('#serverInfoTable');
    var hasError = false;
    var errorMsg = "";
    var servers = $.map(table.bootstrapTable('getSelections'), function (row) {
        if (row["initStatus"] == 1) {
            hasError = true;
            errorMsg = "无法重复初始化已初始化的服务器！";
            return;
        } else if (row["password"] == '') {
            hasError = true;
            errorMsg = "主机:" + row["ip"] + " root口令为空，请先设置root口令再执行初始化操作！";
            return;
        }
        return row.id;
    });
    if (hasError) {
        alert(errorMsg);
        return;
    }
    if (servers.length < 1) {
        alert("请至少选择一项！");
        return;
    }

    blockUI("正在初始化主机（此操作时间可能较长）");
    $.ajax({
        type: "PATCH",
        url: "/nodes",
        data: JSON.stringify(servers),
        dataType: "json",
        contentType: "application/json",
        success: function (failedNodes) {
            unblockUI();
            table.bootstrapTable('refresh');
            if (failedNodes.length > 0) {
                var failedTip = createFailedTip(failedNodes);
                alert(failedTip);
            } else {
                alert("初始化成功！");
            }
        },
        error: function (e) {
            unblockUI();
            alert("系统执行异常！");
            //alert(e.responseText);
        }
    });
}

function flatRow(row) {
    row.serverInfo = {
        "serverId": row["serverInfo.serverId"],
        "ip": row["serverInfo.ip"],
        "serverType": row["serverInfo.serverType"],
        "env": row["serverInfo.env"],
        "idc": row["serverInfo.idc"],
        "host": row["host"],
        "totalMemory": row["serverInfo.totalMemory"],
        "freeMemory": row["serverInfo.freeMemory"],
        "password": row["serverInfo.password"],
        "initStatus": row["serverInfo.initStatus"],
    };
    return row;
}

function createFailedTip(failedNodes) {
    var result = "以下主机初始化失败，请检查主机ip、密码等关键配置 : \r";
    for (index in failedNodes) {
        var node = failedNodes[index];
        result += node.ip + "\r";
    }
    return result;
}

function memoryFormatter(value, row, index) {
    return value + 'GB';
}