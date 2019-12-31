$(document).ready(function () {
    //turn to inline mode
    $.fn.editable.defaults.mode = 'inline';

    // 加载table数据
    $('#redisServerTable').bootstrapTable();
    changeContentHeader(ip, '主机管理', '主机详情');
});

function enLargingTypeChange() {
    var type = $("input[type='radio'][name='type']:checked").val();

    if (type == "GROUP") {
        $("#slaveCountDiv").hide();
        $("#groupCountDiv").show();
        $("#slaveCount").val("");
    } else if (type == "SLAVE") {
        $("#groupCountDiv").hide();
        $("#slaveCountDiv").show();
        $("#groupCount").val("");
    }
}

function slaveOfTypeChange() {
    var type = $("input[type='radio'][name='slaveof_type']:checked").val();

    if (type == "OTHER_INST") {
        $("#masterInfoDiv").show();
    } else {
        $("#masterInfoDiv").hide();
        $("#masterHost").val('');
        $("#masterPort").val('');
        $("#masterPassword").val('');
    }
}

function cacheSizeFormatter(value) {
    return value + 'GB';
}

function roleFormatter(value, row, index, field) {
    if (row["type"] == "SENTINEL") {
        return "sentinel节点";
    }
    if ("MASTER" == value) {
        return "<b>主节点</b>";
    }
    if ("SLAVE" == value) {
        return "从节点";
    }

    return "";
}

function instanceStatusFormatter(value) {
    if ("STARTED" == value) {
        return "运行中";
    }
    if ("STOPED" == value) {
        return "已停止";
    }
    return "未运行";
}

function dateTimeFormatter(value) {
    return new Date(value).format("yyyy-MM-dd HH:mm:ss");
}

var lastClusterId;
var classIndex = 2;

function rowStyle(row, index) {
    var classes = ['active', 'success', 'info', 'warning', 'danger'];

    if (row.status == "IDLE") {
        return {
            classes: 'info'
        };
    }

    if (row.status == "STARTED") {
        if (row["redisClusterInfo.clusterId"] != lastClusterId) {
            lastClusterId = row["redisClusterInfo.clusterId"];
            // success info两个来回切换
            classIndex = 3 - classIndex;
        }
        return {
            classes: classes[classIndex]
        };
    }

    if (row.status == "STOPED") {
        return {
            classes: 'danger'
        };
    }
    return {};
}

function monitorStatus() {
    var table = $('#redisServerTable');
    var instances = $.map(table.bootstrapTable('getData'), function (row) {
        if (!row["serverInfo.ip"]) {
            return;
        }
        row.serverInfo = {"ip": row["serverInfo.ip"]};
        return row;
    });
    if (instances.length) {
        $.ajax({
            type: "POST",
            url: "redis/monitor/status",
            dataType: "json",
            data: JSON.stringify(instances),
            contentType: "application/json",
            success: function (response) {
                // 只更新有变化的行
                if (response.length) {
                    updateRows(response);
                }
            }
        });
    }
}

function startRedisServer() {
    sendSelectedServerInfo('start', '启动');
}

function stopRedisServer() {
    sendSelectedServerInfo('stop', '停止');
}

function sendSelectedServerInfo(command, commandTip) {
    if (!confirm("您确认进行\"" + commandTip + "\"操作吗？")) {
        return;
    }

    var table = $('#redisServerTable');
    var hasError = false;
    var servers = $.map(table.bootstrapTable('getSelections'), function (row) {
        if (row["status"].toLowerCase().indexOf(command) != -1) {
            hasError = true;
            return;
        }
        delete row.state;
        return hierarchicalRow(row);
    });
    if (hasError) {
        alert("无法重复操作已开启或停止的服务器！");
        return;
    }
    if (servers.length < 1) {
        alert("请至少选择一项！");
        return;
    }
    blockUI("正在执行操作");
    $.ajax({
        type: "POST",
        url: "redis/" + command,
        data: JSON.stringify(servers),
        dataType: "json",
        contentType: "application/json",
        success: function (response) {
            if (response.result) {
                // 只更新有变化的行
                if (response.data.length) {
                    updateRows(response.data);
                }
            } else {
                showErrorTip("操作失败，请刷新后重试");
            }
            unblockUI();
        }
    });
}

function centerModals() {
    $('.modal').each(function (i) {
        var $clone = $(this).clone().css('display', 'block').appendTo('body');
        var top = Math.round(($clone.height() - $clone.find('.modal-content').height()) / 2);
        top = top > 0 ? top : 0;
        $clone.remove();
        $(this).find('.modal-content').css("margin-top", top);
    });
}

/**
 * 更新table中data相关的数据，脏刷新
 * @param data
 */
function updateRows(data) {
    // 调整顺序
    data = responseHandler(data);
    var table = $('#redisServerTable');
    var allRows = table.bootstrapTable('getData');
    for (var i = 0; i < data.length; i++) {
        for (var j = 0; j < allRows.length; j++) {
            // 找到对应行
            if (allRows[j].serverInstanceId == data[i].serverInstanceId) {
                // 若role或slaveof发生了变化，则需要重新排序
                var targetIndex = j;
                if (data[i].role != allRows[j].role || data[i].slaveof != allRows[j]['slaveof']) {
                    if (data[i].role == 'master') {
                        // master 插入到当前位置的下过一个master之前
                        for (; targetIndex < allRows.length; targetIndex++) {
                            if (allRows[targetIndex].role == 'master') {
                                targetIndex--;
                                break;
                            }
                        }
                    } else {
                        // slave 插入到所属master的位置
                        for (var p = 0; p < allRows.length; p++) {
                            if (data[i].slaveof == allRows[p]['serverInfo.ip'] + ':' + allRows[p]['port']) {
                                targetIndex = p + 1;
                                break;
                            }
                        }
                    }
                }

                var rowData = flatRow(data[i]);
                if (targetIndex == j) {
                    table.bootstrapTable('updateRow', {
                        index: j,
                        row: rowData
                    });
                } else {
                    // 删除旧行
                    table.bootstrapTable('remove', {
                        field: 'serverInstanceId',
                        values: [data[i].serverInstanceId]
                    });

                    // 插入新行
                    if (j < targetIndex) {
                        targetIndex--;
                    }
                    table.bootstrapTable('insertRow', {
                        index: targetIndex,
                        row: rowData
                    });
                }
                break;
            }
        }
    }
}

/**
 * 将立体的json修改成扁平的
 * @param row
 * @returns {*}
 */
function flatRow(row) {
    row["serverInfo.serverId"] = row.serverInfo.serverId;
    row["serverInfo.serverType"] = row.serverInfo.serverType;
    row["serverInfo.ip"] = row.serverInfo.ip;
    row["serverInfo.host"] = row.serverInfo.host;
    row["serverInfo.env"] = row.serverInfo.env;
    row["serverInfo.idc"] = row.serverInfo.idc;
    row["serverInfo.totalMemory"] = row.serverInfo.totalMemory;
    row["serverInfo.freeMemory"] = row.serverInfo.freeMemory;
    row["redisClusterInfo.clusterId"] = row.redisClusterInfo.clusterId;
    return row;
}

/**
 * 将扁平的json恢复成立体的
 * @param row
 * @returns {*}
 */
function hierarchicalRow(row) {
    row.serverInfo = {
        "serverId": row["serverInfo.serverId"],
        "serverType": row["serverInfo.serverType"],
        "ip": row["serverInfo.ip"],
        "host": row["serverInfo.host"],
        "env": row["serverInfo.env"],
        "idc": row["serverInfo.idc"],
        "totalMemory": row["serverInfo.totalMemory"],
        "freeMemory": row["serverInfo.freeMemory"],
    };

    row.redisClusterInfo = {
        "clusterId": row["redisClusterInfo.clusterId"]
    };
    return row;
}

window.clusterIdEvents = {
    'click .edit': function (e, value, row, index) {
        loadPage("/clusters/" + row["clusterId"] + "/instances/page",
            ['Redis管理主界面', 'NoSQL', 'Redis集群详情']);
        // 防止冒泡，避免触发表格的行选中事件
        e.stopPropagation();
    }
};

function linkFormatter(value) {
    return '<a class="edit ml10" href="javascript:void(0);">' + value + '</a>';
}

function serverResponseHandler(data) {
    var assignedMemory = 0;
    var clusterCount = 0;
    var clusterIds = {};
    for (i in data) {
        if (data[i] && data[i].redisVersion.indexOf("sentinel") == -1) {
            assignedMemory += data[i].cacheSize;
        }
        if (!clusterIds[data[i].clusterId]) {
            clusterCount += 1;
            clusterIds[data[i].clusterId] = 1;
        }
    }

    $('#assigned_memory').html(assignedMemory);
    $('#cluster_count').html(clusterCount);
    $('#instance_count').html(data.length);
    return data;
}