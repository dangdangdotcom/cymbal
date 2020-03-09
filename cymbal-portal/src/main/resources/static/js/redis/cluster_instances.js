$(document).ready(function () {
    //turn to inline mode
    $.fn.editable.defaults.mode = 'inline';

    $("input[type='radio'][name='slaveof_type']").bind("change", function () {
        slaveOfTypeChange();
    });
    // 加载table数据
    $('#redisServerTable').bootstrapTable();

    // 开启监控数据
    setInterval(monitorStatus, 20000, true);

    $('.modal').on('show.bs.modal', centerModals);
    $(window).on('resize', centerModals);

    changeContentHeader(clusterDescription, 'NoSQL', 'Redis集群详情');
});

function responseHandler(data) {
    // 遍历数组并排序
    var result = new Array();
    for (i in data) {
        // 找到下一个master节点
        if (data[i] && data[i].role == 'MASTER') {
            result.push(data[i]);
            // 找到该master节点的所有slave节点
            for (j in data) {
                if (i != j && data[j] && data[j].slaveof == data[i].ip + ":" + data[i].port) {
                    result.push(data[j]);
                    data[j] = null;
                }
            }
            data.splice(i, 1, null);
        }
    }
    // 如果遍历了一次还有剩余，说明该节点目前状态不明确
    for (i in data) {
        if (data[i]) {
            result.push(data[i]);
        }
    }
    return result;
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

function instanceRoleFormatter(value, row, index, field) {
    if ("MASTER" == value) {
        return "<b>主节点</b>";
    }
    if ("SLAVE" == value) {
        if (row.slaveof) {
            return "└";
        }
    }
    return "";
}

function instanceStatusFormatter(value) {
    if ("STARTED" == value) {
        return "运行中";
    }
    if ("STOPPED" == value) {
        return "已停止";
    }
    return "未运行";
}

function instanceTypeFormatter(value) {
    if (value.indexOf("SENTINEL") != -1) {
        return "sentinel节点";
    } else {
        return "redis节点";
    }
}

function dateTimeFormatter(value) {
    return new Date(value).format("yyyy-MM-dd HH:mm:ss");
}

// 存储当前行的样式，用于区分每组主从
var classIndex = 2;

function rowStyle(row, index) {
    var classes = ['active', 'success', 'info', 'warning', 'danger'];
    if (row.status == "IDLE") {
        return {
            classes: 'info'
        };
    }
    if (row.status == "STARTED") {
        if (row.role == 'MASTER') {
            // success info两个来回切换
            classIndex = 3 - classIndex;
        }
        return {
            classes: classes[classIndex]
        };
    }
    if (row.status == "STOPPED") {
        return {
            classes: 'danger'
        };
    }
    return {};
}

function monitorStatus() {
    var table = $('#redisServerTable');
    var data = table.bootstrapTable('getData');
    if (data.length > 0) {
        $.ajax({
            type: "PATCH",
            url: "/clusters/" + data[0].clusterId + "/instances",
            complete: function(XMLHttpRequest, textStatus) {
                switch(XMLHttpRequest.status) {
                    case 205:
                        table.bootstrapTable('refresh');
                        break;
                    default:
                        break;
                }
            }
        });
    }
}

function startRedisServer() {
    sendSelectedServerInfo('startup', '启动');
}

function stopRedisServer() {
    sendSelectedServerInfo('shutdown', '停止');
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
        return row.id;
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
        type: "PATCH",
        url: "/" + command + "/instances",
        data: JSON.stringify(servers),
        contentType: "application/json",
        success: function () {
            table.bootstrapTable('refresh');
            unblockUI();
        }
    });
}

function delClusterNodes() {
    var table = $('#redisServerTable');
    var servers = $.map(table.bootstrapTable('getSelections'), function (row) {
        return hierarchicalRow(row);
    });

    if (servers.length < 1) {
        alert("请至少选择一项!");
        return;
    }
    ;

    if (!confirm("您确认进行删除吗？")) {
        return;
    }

    if (!checkWhetherAllowedRemove(servers)) {
        return;
    }

    table.bootstrapTable('showLoading');
    $.ajax({
        type: "POST",
        url: "redis/cluster/delClusterNodes",
        data: JSON.stringify(servers),
        dataType: "json",
        contentType: "application/json",
        success: function (response) {
            table.bootstrapTable('hideLoading');
            table.bootstrapTable('refresh', {data: response});
        }
    });
}

function checkWhetherAllowedRemove(servers) {
    var result = false;
    $.ajax({
        type: "POST",
        url: "redis/cluster/getNodesInfo",
        data: JSON.stringify(servers),
        dataType: "json",
        contentType: "application/json",
        async: false,
        success: function (data) {
            if (!data || data.length == 0) {
                alert("未找到匹配的节点，请重试或检查主机状态！");
                return;
            }
            var msg = "";
            for (var i = 0; i < data.length; i++) {
                node = data[i];
                if (node.slotRanges.length) {
                    msg += "[" + node.ip + ":" + node.port + "],";
                }
            }
            if (msg != "") {
                msg = msg.substring(0, msg.length - 1) + "仍有slot分布，请先迁移slot！";
                alert(msg);
            } else {
                result = true;
            }
        },
        error: function (e) {
            alert("检测主机状态失败！");
        }
    });

    return result;
}

/**
 * 主从切换
 */
function failover() {
    var table = $('#redisServerTable');
    var servers = table.bootstrapTable('getSelections');

    if (servers.length != 1) {
        alert("您只能选取一个节点执行此操作！");
        return;
    }

    if (servers[0].role != 'SLAVE') {
        alert("此操作只能在slave节点上进行，请重新选取slave节点！");
        return;
    }

    if (!confirm("确定执行主从切换操作？")) {
        return;
    }

    blockUI("正在执行操作");

    $.ajax({
        type: "PATCH",
        url: "/failover/instances/" + servers[0].id,
        success: function (response) {
            table.bootstrapTable('refresh');
        },
        error: function (e) {
            alert(e.responseJSON.data);
        },
        complete: function(e) {
            unblockUI();
        }
    });
}

function openSlaveOfModal() {
    var selectedRows = $('#redisServerTable').bootstrapTable('getSelections');
    if (selectedRows.length != 1) {
        alert("请选中一项进行修改！");
        return;
    }

    var row = selectedRows[0];
    if (row.redisVersion == 'redis-sentinel') {
        alert("该节点为sentinel节点，无法配置主从关系！");
        return;
    }

    var allRows = $('#redisServerTable').bootstrapTable('getData');
    var isWithSentinel = false;
    for (i = 0; i < allRows.length; i++) {
        if (allRows[i].redisVersion == 'redis-sentinel') {
            isWithSentinel = true;
            break;
        }
    }
    if (isWithSentinel == true) {
        if (!confirm("带sentinel监控的集群sentinel可能会自主恢复为配置前的主从状态，建议手工移除或更新sentinel节点，请确认知晓该情况！")) {
            return;
        }
    }

    $('#slaveOf_serverInstanceId').val(row.id);
    $('#slaveOf_clusterId').val(row.clusterId);
    $('#slaveInfo').val(row.ip + ":" + row.port);
    $('#slaveOfModal').modal('show');
}

function configSlaveOf() {
    var confirmMsg =
        "您确认按照如下配置调整主从关系吗？" + "\r" +
        "从节点：" + $('#slaveInfo').val() + "\r";

    var type = $("input[type='radio'][name='slaveof_type']:checked").val();
    if (type == 'OTHER_INST') {
        if ("" == $("#masterHost").val() || "" == $("#masterPort").val()) {
            alert("请输入正确的主节点HOST、PORT信息");
            return;
        }
        var masterURI = $('#masterHost').val() + ":" + $('#masterPort').val();
        if (masterURI == $('#slaveInfo').val()) {
            alert("主从节点信息相同，请调整主节点信息");
            return;
        }
        confirmMsg +=
            "主节点：" + masterURI + "\r" +
            "主节点访问密码：" + $('#masterPassword').val();
    } else {
        confirmMsg += "主节点： NO ONE";
    }

    if (!confirm(confirmMsg)) {
        return;
    }

    var slaveOfInfo = {};
    var type;

    if (type == 'OTHER_INST') {
        slaveOfInfo.ip = $("#masterHost").val();
        slaveOfInfo.port = $("#masterPort").val();
        slaveOfInfo.password = $("#masterPassword").val();
        type = "PATCH";
    } else {
        type = "DELETE";
    }

    // blockUI first
    blockUI("正在保存配置");

    var table = $('#redisServerTable');
    table.bootstrapTable('showLoading');
    $.ajax({
        type: type,
        url: "/replication/instances/" + $("#slaveOf_serverInstanceId").val(),
        data: JSON.stringify(slaveOfInfo),
        contentType: 'application/json',
        success: function (response) {
            alert("配置主从关系成功！");
            table.bootstrapTable('refresh');
        },
        error: function (e) {
            alert("配置主从关系失败！");
        },
        complete: function (e) {
            table.bootstrapTable('hideLoading');
            unblockUI();
        }
    });

    $('#slaveOfModal').modal('hide');
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

    // 如果是全量更新，则直接调用load方法
    if (data.length == allRows.length) {
        table.bootstrapTable('load', data);
        return;
    }

    // 局部更新，遍历局部修改点
    for (var i = 0; i < data.length; i++) {
        for (var j = 0; j < allRows.length; j++) {
            // 找到对应行
            if (allRows[j].id == data[i].id) {
                // 若role或slaveof发生了变化，则需要重新排序
                var targetIndex = j;
                if (data[i].role != allRows[j].role || data[i].slaveof != allRows[j]['slaveof']) {
                    if (data[i].role == 'MASTER') {
                        // master 插入到当前位置的下过一个master之前
                        for (; targetIndex < allRows.length; targetIndex++) {
                            if (allRows[targetIndex].role == 'MASTER') {
                                targetIndex--;
                                break;
                            }
                        }
                    } else {
                        // slave 插入到所属master的位置
                        for (var p = 0; p < allRows.length; p++) {
                            if (data[i].slaveof == allRows[p]['ip'] + ':' + allRows[p]['port']) {
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

window.nodeIpEvents = {
    'click .edit': function (e, value, row, index) {
        // 防止冒泡，避免触发表格的行选中事件
        e.stopPropagation();
        loadPage('/nodes/' + row["nodeId"] + '/instances/page');
    }
};

function instanceLinkFormatter(value, row) {
    var description = "";
    if (row["nodeDescription"]) {
        description = '<i class="glyphicon glyphicon-flag pull-right text-red" data-toggle="tooltip" data-original-title="' + row["nodeDescription"] + '"></i>';
    }
    return '<a class="edit ml10" href="javascript:void(0);">' + value + description + '</a>';
}