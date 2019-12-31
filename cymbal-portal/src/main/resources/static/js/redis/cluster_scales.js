$(document).ready(function () {
    loadEnlargeInfoData();

    //radio type绑定change事件
    $("input[type='radio'][name='type']").bind("change", function () {
        onEnlargeTypeChange();
    });
});

function loadEnlargeInfoData() {
    //turn to inline mode
    $.fn.editable.defaults.mode = 'inline';
    // 加载table数据
    $('#clusterEnlargeTable').bootstrapTable();
}

function enlargeTypeFormatter(value, row, index, field) {
    switch (value) {
        case "HORIZONTAL":
            return "水平扩容";
            break;
        case "VERTICAL":
            return "垂直扩容";
            break;
        case "SLAVE_ONLY":
            return "补充从节点";
            break;
        default:
            break;
    }
}

function enlargeStatusFormatter(value, row, index, field) {
    switch (value) {
        case "DOING":
            return "进行中";
            break;
        case "DONE":
            return "已完成";
            break;
        default:
            break;
    }
}

function enlargeResultFormatter(value, row, index, field) {
    switch (value) {
        case "SUCCESS":
            return "成功";
            break;
        case "FAIL":
            // if (row.canRetry) {
            //    return "失败 <a onclick='retryEnlarge()' style='cursor: pointer' title='重试'><i class='fa fa-repeat'></i></a>";
            //}
            return "失败";
            break;
        default:
            break;
    }
}

function enlargeTableRowStyle(row, index) {
    if (row.result == "FAIL") {
        return {
            classes: 'danger'
        };
    }
    switch (row.result) {
        case "FAIL":
            return {
                classes: 'danger'
            };
            break;
        case "SUCCESS":
            return {
                classes: 'success'
            };
            break;
        default:
            return {
                classes: 'info'
            };
    }
}

function openEnlargeModal(clusterId) {
    $('#clusterIdOfNodeModal').val(clusterId);
    $('#nodeModal').modal('show');
}

function onEnlargeTypeChange() {
    var type = $("input[type='radio'][name='type']:checked").val();

    switch (type) {
        case "HORIZONTAL":
            $('#horizontalDiv').show();
            $('#verticalDiv').hide();
            $('#slaveOnlyDiv').hide();
            break;
        case "VERTICAL":
            $('#horizontalDiv').hide();
            $('#verticalDiv').show();
            $('#slaveOnlyDiv').hide();
            break;
        case "SLAVE_ONLY":
            $('#horizontalDiv').hide();
            $('#verticalDiv').hide();
            $('#slaveOnlyDiv').show();
            break;
        default:
            break;
    }
}


function addClusterNode() {
    var type = $("input[type='radio'][name='type']:checked").val();
    var typeDesc;

    var scaleNum;
    switch (type) {
        case "HORIZONTAL":
            scaleNum = $('#horizontalEnlargeNum').val();
            typeDesc = "水平扩容";
            break;
        case "VERTICAL":
            scaleNum = $('#verticalEnlargeNum').val();
            typeDesc = "垂直扩容";
            break;
        case "SLAVE_ONLY":
            scaleNum = $('#slaveOnlyEnlargeNum').val();
            typeDesc = "补充从节点";
            break;
        default:
            break;
    }


    if (!scaleNum || scaleNum == 0) {
        alert("请输入扩容数值！");
        return;
    }

    var enLargingInfo = {
        "clusterId": $("#clusterIdOfNodeModal").val(),
        "type": $("input[type='radio'][name='type']:checked").val(),
        "scaleNum": scaleNum
    };

    var sureEnlarge = confirm("确定按照以下方式执行扩容？\n扩容方式：" + typeDesc + "\n扩容数值：" + scaleNum);
    if (!sureEnlarge) {
        return;
    }

    blockUI("正在操作扩容，请稍后！");

    $.ajax({
        type: "POST",
        url: "/clusters/" + enLargingInfo.clusterId + "/scales",
        data: JSON.stringify(enLargingInfo),
        contentType: "application/json",
        success: function (response) {
            switch (type) {
                case "HORIZONTAL":
                    alert("扩容申请提交成功，扩容过程可能需要一段时间。\n请刷新表格查看扩容结果。");
                    break;
                default:
                    alert("扩容成功！\n点击确定刷新页面查看最新状态。");
                    break;
            }
            window.location.reload();
        },
        error: function (e) {
            alert("扩容失败，请检查主机剩余内存！");
        },
        complete: function(e) {
            unblockUI();
        }
    });

    $('#nodeModal').modal('hide');
}

function retryEnlarge() {
    var table = $('#clusterEnlargeTable');
    var selectedTable = table.bootstrapTable('getSelections');
    if (selectedTable.length != 1) {
        alert("请选中要重试的行！");
        return;
    }

    var scaleId = selectedTable[0].id;
    var clusterId = selectedTable[0].clusterId;
    // 确定选择的是最后一条水平扩容
    var allRows = table.bootstrapTable('getData');
    for (var index in allRows) {
        debugger;
        if (allRows[index].type == "HORIZONTAL") {
            if (allRows[index].result == "FAIL" && allRows[index].id == scaleId) {
                blockUI("正在操作扩容，请稍后！");

                $.ajax({
                    type: "PATCH",
                    url: "/clusters/" + clusterId + "/scales/" + scaleId,
                    success: function () {
                        alert("扩容申请提交成功，扩容过程可能需要一段时间。\n请刷新表格查看扩容结果。");
                        table.bootstrapTable('refresh');
                    },
                    error: function (e) {
                        alert("该扩容不能重试！\n只能重试最后一次失败的水平扩容，请重新选择！");
                    },
                    complete: function(e) {
                        unblockUI();
                    }
                });
                return;
            }
            break;
        }
    }
    alert("该扩容不能重试！\n只能重试最后一次失败的水平扩容，请重新选择！");
    table.bootstrapTable('refresh');
}

function clusterEnlargeTableDataHandler(clusterEnlarges) {
    for (var index in clusterEnlarges) {
        if (clusterEnlarges[index].type == "HORIZONTAL") {
            if (clusterEnlarges[index].result == "FAIL") {
                clusterEnlarges[index]["canRetry"] = true;
            }
            break;
        }
    }
    return clusterEnlarges;
}
