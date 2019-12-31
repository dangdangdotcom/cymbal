$(document).ready(function () {
    //turn to inline mode
    $.fn.editable.defaults.mode = 'inline';

    // 加载table数据
    initBootstrapTable("redisServerTable");
    initBootstrapTable("redisConfigTable");

    $('.modal').on('show.bs.modal', centerModals);
    $(window).on('resize', centerModals);

    $("#configModal").on('hidden', function () {
        $(this).data('modal').$element.removeData();
    });

    $('#configModal').on('hidden.bs.modal', function () {
        refreshConfigTable();
    })

    changeContentHeader('Redis管理主界面', 'NoSQL', 'Redis集群列表');
});

function cacheSizeFormatter(value, row, index) {
    var cacheSizeStr = row.masterCount * value + 'GB (每个主节点' + value + 'GB)';
    return cacheSizeStr;
}

function ahrefFormatter(value, row, index) {
    return '<a href="javascript:loadClusterInstance(\'' + value + '\', \'' + row.description + '\', \'' + row.userCnName + '\');">' + value + '</a>';
}

function replicaFormatter(value, row, index) {
    var replicaStr = row.masterCount * value + ' (每个主节点' + value + '个副本)';
    return replicaStr;
}

function envFormatter(value, row, index) {
    switch (value) {
        case "TEST":
            return "测试环境";
        case "STAGING":
            return "预上线环境";
        default:
            return "生产环境";
    }
}

function loadClusterInstance(clusterId) {
    loadPage("/clusters/" + clusterId + "/instances/page");
}

function dateTimeFormatter(value) {
    return new Date(value).format("yyyy-MM-dd HH:mm:ss");
}

function clusterModeFormatter(value) {
    if (value == "cluster") {

    }
}

function loadConfigModal() {
    var selectedTable = $('#redisConfigTable').bootstrapTable('getSelections');
    if (selectedTable.length != 1) {
        alert("请选中一项进行修改！");
        return;
    }
    $.ajax({
        type: "GET",
        url: "/configs/" + selectedTable[0].id + "/details",
        dataType: "json",
        contentType: "application/json",
        success: function (response) {
            initBaseConfigInfo(selectedTable[0]);
            initRedisConfigDetailTable(response);
            $('#configModal').modal('show');
        },
        error: function (e) {
            alert("获取配置信息异常！");
            return;
        }
    });
}

function initBaseConfigInfo(selectedRow) {
    $('#config-id').val(selectedRow.id);

    $('#config-name').editable('destroy');
    $('#config-name').editable({
        type: 'text',
        pk: selectedRow.id,
        value: selectedRow.configName,
        display: function (value) {
            $(this).text(value);
        },
        url: '/clusters/' + selectedRow.clusterId + '/configs/' + selectedRow.id,
        ajaxOptions: {
            contentType: "application/json",
            dataType: "text",
            method: "PATCH"
        },
        params: function (params) {
            return params.value;
        },
        success: function (response) {

        },
        error: function (xhr) {
            alert('修改失败');
            alert(JSON.stringify(xhr));
        }
    });
}

var redisConfigDetailTable = null;

function initRedisConfigDetailTable(dataSet) {
    //DataTable不允许重复创建，先销毁，再创建
    if (redisConfigDetailTable != null) {
        redisConfigDetailTable.destroy();
    }

    redisConfigDetailTable = $("#redisConfigDetailTable").DataTable({
        paging: false,
        searching: false,
        ordering: false,
        info: false,
        data: dataSet,
        columns: [
            {data: "id", "visible": false},
            {data: "itemName"},
            {
                data: null, render: function (data, type, row) {
                    if (!data.id) {
                        data.id = '';
                    }
                    return "<a href='#' data-name='" + data.itemName + "' data-pk='" + data.id + "'>" + data.itemValue + "</a>";
                }
            },
            {data: "itemComment"}
        ]
    });

    $('a[data-pk]').editable('destroy');
    $('a[data-pk]').editable({
        type: 'text',
        display: function (value) {
            $(this).text(value);
        },
        url: '/configs/' + $('#config-id').val() + '/details',
        ajaxOptions: {
            contentType: "application/json",
            dataType: "text",
            method: "PUT"
        },
        params: function (params) {
            return JSON.stringify({
                id: params.pk,
                configId: $('#config-id').val(),
                itemName: params.name,
                itemValue: params.value
            });
        },
        success: function (response) {

        },
        error: function (xhr) {
            alert('修改失败');
            alert(JSON.stringify(xhr));
        }
    });
}

function updateRedisConfig(obj) {
    var l = Ladda.create(obj);
    l.start();
    $.ajax({
        type: "POST",
        url: "redis/config/updateRedisConfig",
        data: JSON.stringify($('#config-id').val()),
        dataType: "json",
        contentType: "application/json",
        success: function (response) {
            alert(response == true ? '更新成功' : '更新失败');
            l.stop();
        },
        error: function (errors) {
            alert('更新失败');
            l.stop();
        }
    });
}

function sendSelectedServerInfo(command) {
    var table = $('#redisServerTable');
    var hasError = false;
    var servers = $.map(table.bootstrapTable('getSelections'), function (row) {
        if (row["status"].toLowerCase().indexOf(command) != -1) {
            hasError = true;
            return;
        }
        delete row.state;
        return flatRow(row);
    });
    if (hasError) {
        alert("无法重复操作已开启或停止的服务器！");
        return;
    }
    if (servers.length < 1) {
        alert("请至少选择一项！");
        return;
    }
    $.ajax({
        type: "POST",
        url: "redis/" + command,
        data: JSON.stringify(servers),
        dataType: "json",
        contentType: "application/json",
        success: function (response) {
            table.bootstrapTable('hideLoading');
            table.bootstrapTable('refresh', {data: response});
        }
    });
}

function flatRow(row) {
    row.serverInfo = {
        "serverId": row["serverInfo.serverId"],
        "serverType": row["serverInfo.serverType"],
        "ip": row["serverInfo.ip"],
        "host": row["host"],
        "env": row["serverInfo.env"],
        "idc": row["serverInfo.idc"],
        "totalMemory": row["serverInfo.totalMemory"],
        "freeMemory": row["serverInfo.freeMemory"],
    };
    return row;
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

function refreshConfigTable() {
    $('#redisConfigTable').bootstrapTable('refresh');
}