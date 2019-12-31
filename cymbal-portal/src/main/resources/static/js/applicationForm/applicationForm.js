var applicationFormTable = null;
var dataTableLanguage = {
    decimal: "",
    emptyTable: "表中无匹配的数据",
    info: "从  _START_ 到  _END_ /共 _TOTAL_ 条数据",
    infoEmpty: "没有数据",
    infoFiltered: "(从总计 _MAX_ 条数据中过滤)",
    infoPostFix: "",
    thousands: ",",
    lengthMenu: "显示 _MENU_ 条数据",
    loadingRecords: "加载中...",
    processing: "努力加载数据中...",
    search: "搜索:",
    zeroRecords: "没有检索到数据",
    paginate: {
        first: "首页",
        last: "尾页",
        next: "后一页",
        previous: "前一页"
    }
};
$(document).ready(function () {
    $('input[type="checkbox"].minimal, input[type="radio"].minimal').iCheck({
        checkboxClass: 'icheckbox_minimal-blue',
        radioClass: 'iradio_minimal-blue'
    });

    // 初始化滑块
    reInitCacheSizeSlider(1, false);
    // redisMode绑定change事件
    $("#redisMode").bind("change", function () {
        redisModeChange($(this).val());
    });
    // enableSentinel绑定click事件
    $("#enableSentinel").bind("click", function () {
        enableSentinelChange();
    });
    //禁用datatable默认的error处理模式(默认为alert)，可通过绑定error.dt事件添加自定义处理
    $.fn.dataTable.ext.errMode = 'none';
    // 加载table数据
    var url;
    if (!status) {
        url = "/user/application-forms";
        changeContentHeader('我的申请单', '申请单', '我的申请单');
    } else {
        url = "/application-forms?status=" + status;
        changeContentHeader(getStatusText(status), '申请单', getStatusText(status));
    }

    // TODO change to bootstrap table.
    initRedisApplicationFormTable(url, "GET", null);
    // $("#applicationFormTable").bootstrapTable();

    initUserSelect();
});

function getStatusText(status) {
    switch (status) {
        case "PENDING":
            return "待审批的申请单";
        case "APPROVED":
            return "审批通过的申请单";
    }
}

/**
 * 重新初始化cacheSize滑块
 */
function reInitCacheSizeSlider(initCacheSize, isDisable) {
    //销毁滑块
    var slider = $("#cacheSize").data("ionRangeSlider");
    if (slider != null) {
        slider.destroy();
    }

    //初始化滑块
    var valueArray = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64];
    $("#cacheSize").ionRangeSlider({
        type: 'single',
        values: valueArray,
        from: 0,
        postfix: "G",
        grid: false,
        disable: isDisable,
        onFinish: function (obj) {// function-callback, is called once, after slider finished it's work
            $("#cacheSize").val(obj.from_value);
        }
    });
    //直接在初始化中指定from值不生效，通过update设置
    var fromValue = valueArray.indexOf(initCacheSize) == -1 ? 0 : valueArray.indexOf(initCacheSize);
    $("#cacheSize").data("ionRangeSlider").update({from: fromValue});
}

function redisModeChange(value) {
    $("#masterCount").empty();
    $("#redisVersion").empty();
    $("#enableSentinel").prop("checked", false);
    $("#password").val('');

    if (value == "CLUSTER") {
        $("#enableSentinelDiv").hide();
        $("#passwordDiv").hide();
        $("#masterCount").prepend("<option>6</option>");
        $("#masterCount").prepend("<option>5</option>");
        $("#masterCount").prepend("<option>4</option>");
        $("#masterCount").prepend("<option>3</option>");

        // 处理redis版本
        $("#redisVersion").prepend("<option value='redis-5.0.3' selected>redis-5.0.3</option>");
        $("#redisVersion").prepend("<option value='redis-4.0.12'>redis-4.0.12</option>");
        $("#redisVersion").prepend("<option value='redis-3.2.11' selected>redis-3.2.11</option>");

    } else {
        $("#enableSentinelDiv").show();
        $("#passwordDiv").show();
        $("#masterCount").prepend("<option>5</option>");
        $("#masterCount").prepend("<option>4</option>");
        $("#masterCount").prepend("<option>3</option>");
        $("#masterCount").prepend("<option>2</option>");
        $("#masterCount").prepend("<option>1</option>");

        // 处理redis版本
        $("#redisVersion").prepend("<option value='redis-2.8.24'>redis-2.8.24</option>");
    }
    $("#masterCount").selectpicker('refresh');
    $("#redisVersion").selectpicker('refresh');
}

function enableSentinelChange() {
    checkSentinelCondition();
}

function checkSentinelCondition() {
    if ($("#enableSentinel").prop("checked")) {
        if ($('#replicaCount option:selected').val() <= 1) {
            alert("启用Sentinel需要备份节点至少两个以上！");
            $('#enableSentinel').prop("checked", false);
            return false;
        }
    }
    return true;
}

function checkRequiredField() {
    if ($("#belongSystem").val() == '') {
        alert("所属系统不允许为空！");
        return false;
    }
    return true;
}

function queryParams(params) {
    return {
        'page': params.offset,
        'size': params.limit
    }
}


function getRedisApplyInfo() {
    var id = $('#applicationFormId').val();
    var env = $('#env option:selected').val();
    var idc = $('#idc option:selected').val();
    var redisMode = $('#redisMode option:selected').val();
    var redisVersion = $('#redisVersion').val();
    var masterCount = $('#masterCount option:selected').val();
    var replicaCount = $('#replicaCount option:selected').val();
    var redisPersistenceType = $('#redisPersistenceType option:selected').val();
    var enableSentinel = $("#enableSentinel").prop("checked");
    var cacheSize = $("#cacheSize").val();
    var password = $('#password').val();
    var belongSystem = $('#belongSystem').val();
    var description = $('#description').val();
    var approvalOpinion = $('#approvalOpinion').val();
    var applicantEnName = $('#applicantEnName').val();
    var applicantCnName = $('#applicantCnName').val();

    var applyInfo = {
        "id": id,
        "env": env,
        "idc": idc,
        "redisMode": redisMode,
        "redisVersion": redisVersion,
        "masterCount": masterCount,
        "replicaCount": replicaCount,
        "redisPersistenceType": redisPersistenceType,
        "enableSentinel": enableSentinel,
        "cacheSize": cacheSize,
        "password": password,
        "belongSystem": belongSystem,
        "description": description,
        "approvalOpinion": approvalOpinion,
        "applicantEnName": applicantEnName,
        "applicantCnName": applicantCnName
    };

    return applyInfo;
}

function submitRedisApply(isSubmit) {
    if (false == checkSentinelCondition() || false == checkRequiredField()) {
        return false;
    }

    blockUI('正在保存申请单');
    $('#redisModal').modal('hide');

    var applyInfo = getRedisApplyInfo();
    if (isSubmit) {
        applyInfo.status = "PENDING";
    } else {
        applyInfo.status = "DRAFT";
    }

    $.ajax({
        type: "POST",
        url: "/application-forms",
        data: JSON.stringify(applyInfo),
        contentType: "application/json",
        success: function () {
            applicationFormTable.ajax.reload();
        },
        error: function (response) {
            alert("保存申请单失败：" + JSON.stringify(response));
        },
        complete: function (e) {
            unblockUI();
        }
    });
}

function denyRedisApply() {
    var approvalOpinion = $('#approvalOpinion').val();
    approvalOpinion = approvalOpinion ? approvalOpinion : "";
    var id = $('#applicationFormId').val();

    blockUI('正在保存申请单');
    $('#redisModal').modal('hide');

    $.ajax({
        type: "POST",
        url: "/application-forms/" + id + "/deny",
        data: approvalOpinion,
        contentType: "application/json",
        success: function () {
            applicationFormTable.ajax.reload();
        },
        error: function (response) {
            alert("保存申请单失败：" + JSON.stringify(response));
        },
        complete: function (e) {
            unblockUI();
        }
    });
}

function approveAndAllocRedisApply() {
    if (false == checkSentinelCondition()) {
        return false;
    }

    var applyInfo = getRedisApplyInfo();
    var tip = "审批通过，正在初始化redis集群环境（此操作时间可能较长）";
    blockUI(tip);
    $('#redisModal').modal('hide');

    $.ajax({
        type: "POST",
        url: "/application-forms/" + applyInfo.id + "/approve",
        data: JSON.stringify(applyInfo),
        contentType: "application/json",
        success: function (clusterId) {
            applicationFormTable.ajax.reload();
        },
        error: function (response) {
            if (response.status == 400) {
                showErrorTip("剩余内存资源不足，无法创建所需规模集群，请补充主机可用内存资源后重试。");
            } else if (response.status == 422) {
                showErrorTip("该申请单已经被审批，请勿重复审批。");
            }
        },
        complete: function (e) {
            unblockUI();
        }
    });
}

function initRedisApplicationFormTable(serverUrl, httpType, formData, tip) {
    if (!tip) {
        tip = "正在执行操作";
    }
    blockUI(tip);

    var ajaxParm = {
        url: serverUrl,
        type: httpType,
        data: function (d) {
            return $.extend({}, d, formData);
        },
        dataSrc: "data",
    };

    // DataTable不允许重复创建，先销毁，再创建
    if (applicationFormTable) {
        applicationFormTable.settings()[0].ajax = ajaxParm;
        applicationFormTable.ajax.reload();
        return;
    }

    applicationFormTable = $("#applicationFormTable").DataTable({
        paging: true,
        searching: false,
        ordering: false,
        serverSide: true,
        language: dataTableLanguage,
        processing: true,
        ajax: ajaxParm,
        columns: [
            {data: "id", "visible": false},
            {
                data: null, render: function (data, type, row) {
                    return descriptionFormatter(data.description);
                }
            },
            {data: "applicantCnName"},
            {data: "belongSystem"},
            {
                data: null, render: function (data, type, row) {
                    switch (data.env) {
                        case "TEST":
                            return "测试环境";
                        case "STAGING":
                            return "预上线环境";
                        default:
                            return "生产环境";
                    }
                }
            },
            {
                data: null, render: function (data, type, row) {
                    switch (data.redisMode) {
                        case "STANDALONE":
                            return "standalone";
                        default:
                            return "cluster";
                    }
                }
            },
            {data: "redisVersion"},
            {
                data: null, render: function (data, type, row) {
                    return data.cacheSize + "GB";
                }
            },
            {data: "idc"},
            {data: "masterCount"},
            {data: "replicaCount"},
            {
                data: null, render: function (data, type, row) {
                    return new Date(data.creationDate).format("yyyy-MM-dd HH:mm:ss");
                }
            },
            {
                data: null, render: function (data, type, row) {
                    return statusFormatter(data.status);
                }
            }
        ]
    });

    //注册error处理
    $('#applicationFormTable').on('error.dt', function (e, settings, techNote, message) {
        unblockUI();
        var index = message.indexOf('-') + 1;
        var tip = message.substring(index);
        showErrorTip(tip);
    });

    // 每次加载数据完成时的回调
    $('#applicationFormTable').on('draw.dt', function (e, settings, techNote, message) {
        unblockUI();
    });

    //增加click处理事件
    $('#applicationFormTable').off('click', 'tr a');
    $('#applicationFormTable').on('click', 'tr a', function () {
        var data = applicationFormTable.row($(this).parent('td').parent('tr')).data();
        showRedisModal(data);
    });
}

function descriptionFormatter(value) {
    var description = value;
    if (description == null || description == '' || description.trim() == '') {
        description = "redis申请单";
    } else if (description.length > 30) {
        description = description.substring(0, 27) + '...';
    }
    return "<a style='cursor: pointer'>" + description + "</a>";
}

function statusFormatter(value) {
    switch (value) {
        case "DRAFT":
            return "<span class='label label-primary'>草稿</span>";
            break;
        case "PENDING":
            return "<span class='label label-warning'>审核中</span>";
            break;
        case "APPROVED":
            return "<span class='label label-success'>审核通过</span>";
            break;
        case "DENIED":
            return "<span class='label label-danger'>驳回</span>";
            break;
        default:
            return "<span class='label label-danger'>异常</span>";
    }
}

function dateTimeFormatter(value) {
    return new Date(value).format("yyyy-MM-dd HH:mm:ss");
}

function showRedisModal(data) {

    //初始化redisMode信息
    redisModeChange(data.redisMode);
    //设置默认值
    setDefaultValueOfRedisModal(data);

    if (isAdmin == true) {
        //以管理人员身份展示界面
        showRedisModalAsAdmin(data);
    } else {
        //以本人身份展示单界面
        showRedisModalAsSelf(data);
    }

    //模态展示
    $("#redisModal").modal("show");
}

function setDefaultValueOfRedisModal(data) {
    $('#applicationFormId').val(data.id);
    $('#env').val(data.env);
    $('#idc').val(data.idc);
    $('#redisMode').val(data.redisMode);
    $('#redisVersion').val(data.redisVersion);
    $('#masterCount').val(data.masterCount);
    $('#replicaCount').val(data.replicaCount);
    $('#redisPersistenceType').val(data.redisPersistenceType);
    $('#enableSentinel').prop("checked", data.enableSentinel);
    $('#password').val(data.password);
    $('#belongSystem').val(data.belongSystem);
    $('#description').val(data.description);
    $('#approvalOpinion').val(data.approvalOpinion);
    $('.selectpicker').selectpicker('refresh');
    $('#cacheSize').val(data.cacheSize);
    $('#applicantEnName').val(data.applicantEnName).trigger('change');
}

function showRedisModalAsAdmin(data) {
    $('#approvalOpinionDiv').css('display', '');
    $('#redisSaveBtn').css('display', 'none');
    $('#redisSubmitBtn').css('display', 'none');

    if (data.status == 'DRAFT' || data.status == 'DENIED') {
        enableAllElementOfRedisModal(true, data);
        $('#approvalOpinionDiv').css('display', 'none');
        $('#redisSaveBtn').css('display', '');
        $('#redisSubmitBtn').css('display', '');
    } else if (data.status == 'PENDING') {
        enableAllElementOfRedisModal(true, data);
        $('#redisDenyBtn').css('display', '');
        $('#redisApprovalAndAllocBtn').css('display', '');
    } else {
        enableAllElementOfRedisModal(false, data);
        $('#redisDenyBtn').css('display', 'none');
        $('#redisApprovalAndAllocBtn').css('display', 'none');
        $('#applicantEnName').attr("disabled", "disabled");
    }
}

function showRedisModalAsSelf(data) {
    $('#redisDenyBtn').css('display', 'none');
    $('#redisApprovalAndAllocBtn').css('display', 'none');
    $('#approvalOpinionDiv').css('display', '');
    $('#redisSaveBtn').css('display', '');
    $('#redisSubmitBtn').css('display', '');
    $('#applicantEnName').attr("disabled", "disabled")

    if (data.status == 'DRAFT') {
        enableAllElementOfRedisModal(true, data);
        $('#approvalOpinionDiv').css('display', 'none');
    } else if (data.status == 'DENIED') {
        enableAllElementOfRedisModal(true, data);
        $('#approvalOpinion').attr('disabled', true);
    } else {
        enableAllElementOfRedisModal(false, data);
        $('#redisSaveBtn').css('display', 'none');
        $('#redisSubmitBtn').css('display', 'none');
    }
}

function enableAllElementOfRedisModal(isEnable, data) {
    $('#env').attr('disabled', !isEnable);
    $('#idc').attr('disabled', !isEnable);
    $('#redisMode').attr('disabled', !isEnable);
    $('#masterCount').attr('disabled', !isEnable);
    $('#replicaCount').attr('disabled', !isEnable);
    $('#redisPersistenceType').attr('disabled', !isEnable);
    $('#enableSentinel').attr('disabled', !isEnable);
    $('#cacheSize').attr('disabled', !isEnable);
    $('#password').attr('disabled', !isEnable);
    $('#belongSystem').attr('disabled', !isEnable);
    $('#description').attr('disabled', !isEnable);
    $('#approvalOpinion').attr('disabled', !isEnable);
    $('.selectpicker').selectpicker('refresh');

    //初始化滑块，并设置默认值和状态
    reInitCacheSizeSlider(data.cacheSize, !isEnable);
}

function clearValueOfRedisModal() {
    $('#applicationFormId').val('');
    $('#env').val('');
    $('#idc').val('');
    $('#redisMode').val('');
    $('#redisVersion').val('');
    $('#masterCount').val('');
    $('#replicaCount').val('');
    $('#redisPersistenceType').val('');
    $("#enableSentinel").prop("checked", false);
    $('#password').val('');
    $('#belongSystem').val('');
    $('#description').val('');
    $('#approvalOpinion').val('');
    $('.selectpicker').selectpicker('refresh');
}

function initUserSelect() {
    var users = getAllUser();
    var usersOptionData = [];
    for (var index in users) {
        var userOption = {
            id: users[index].userName,
            text: users[index].userCnName + '<' + users[index].email + '>'
        }
        usersOptionData.push(userOption);
    }
    var tags = true;
    if (usersOptionData.length > 0) {
        tags = false;
    }
    // 初始化select搜索框
    $_applicantEnName = $('#applicantEnName');
    $_applicantEnName.select2({
        data: usersOptionData,
        language: 'zh-CN',
        tags: tags
    });
    $_applicantEnName.on('change', function (e) {
        var text = $(this).find("option:selected").text();
        var name = text.substr(0, text.indexOf("<"))
        $('#applicantCnName').val(name);
    });
}