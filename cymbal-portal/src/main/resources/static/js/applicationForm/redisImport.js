$(document).ready(function () {
    // 初始化滑块
    initCacheSizeSlider(1);

    //
    initRedisVersionSelect();

    initUserSelect();

    changeContentHeader('导入现有redis集群', '申请单', '现有集群导入');
});

/**
 * 初始化redis版本下拉事件，联动集群模式
 */
function initRedisVersionSelect() {
    $("#redisImportForm select[name='redisVersion']").change(function () {
        if ($(this).val().indexOf("2.8") != -1) {
            $("#redisImportForm select[name='redisVersion']").siblings("input[name='redisMode']").val("STANDALONE");
        } else {
            $("#redisImportForm select[name='redisVersion']").siblings("input[name='redisMode']").val("CLUSTER");
        }
    });
}


/**
 * 初始化cacheSize滑块
 */
function initCacheSizeSlider(initCacheSize) {
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
        disable: false,
        onFinish: function (obj) {// function-callback, is called once, after slider finished it's work
            $("#cacheSize").val(obj.from_value);
        }
    });
    //直接在初始化中指定from值不生效，通过update设置
    var fromValue = valueArray.indexOf(initCacheSize) == -1 ? 0 : valueArray.indexOf(initCacheSize);
    $("#cacheSize").data("ionRangeSlider").update({from: fromValue});
}

function submitRedisImport() {
    // form validate start
    // belongSystem
    var $_description = $("#redisImportForm input[name='description']");
    if (!validate($_description, "请选择所属系统！")) {
        return;
    }

    // serverPassword
    var $_serverPassword = $("#redisImportForm input[name='nodePassword']");
    if (!validate($_serverPassword, "请输入系统用户root的密码！")) {
        return;
    }

    // redis instance uri
    var $_redisInstanceURIs = $("#redisImportForm textarea[name='redisInstanceURIs']");
    var redisInstanceURIs = $_redisInstanceURIs.val();
    if (!validate($_redisInstanceURIs, "请输入集群节点信息！")) {
        return;
    } else {
        // check format of URIs
        var pattIp = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\:([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/;
        var redisInstanceURIArray = redisInstanceURIs.split("\n");

        redisInstanceURIs = [];
        for (var each in redisInstanceURIArray) {
            var redisInstanceURI = redisInstanceURIArray[each];
            if (redisInstanceURI) {
                if (!pattIp.test(redisInstanceURI)) {
                    $_redisInstanceURIs.parent().parent().addClass("has-error");
                    alert("redis节点输入格式有误，请调整！" + redisInstanceURI);
                    return;
                } else {
                    redisInstanceURIs.push(redisInstanceURI);
                }
            }
        }
    }
    $_redisInstanceURIs.parent().parent().removeClass("has-error");

    // sentinel instance uri
    var $_sentinelInstanceURIs = $("#redisImportForm textarea[name='sentinelInstanceURIs']");
    var sentinelInstanceURIs = $_sentinelInstanceURIs.val();
    if (sentinelInstanceURIs) {
        // check format of URIs
        var pattIp = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\:([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/;
        var sentinelInstanceURIsArray = sentinelInstanceURIs.split("\n");

        sentinelInstanceURIs = [];
        for (var each in sentinelInstanceURIsArray) {
            var sentinelInstanceURI = sentinelInstanceURIsArray[each];
            if (sentinelInstanceURI) {
                if (!pattIp.test(sentinelInstanceURI)) {
                    $_sentinelInstanceURIs.parent().parent().addClass("has-error");
                    alert("sentinel节点输入格式有误，请调整！" + sentinelInstanceURI);
                    return;
                } else {
                    sentinelInstanceURIs.push(sentinelInstanceURI);
                }
            }
        }
    }
    $_sentinelInstanceURIs.parent().parent().removeClass("has-error");
    // form validate end

    // make form data
    var formItems = $("#redisImportForm").serializeArray();

    var data = {};
    for (var each in formItems) {
        var eachItem = formItems[each];
        data[eachItem["name"]] = eachItem["value"];
    }
    data["redisInstanceURIs"] = redisInstanceURIs;
    data["sentinelInstanceURIs"] = sentinelInstanceURIs;

    var sureCommit = confirm("确定导入该集群吗？");
    if (sureCommit) {
        blockUI("正在导入集群，该过程可能耗时较长");
        $.ajax({
            type: "POST",
            url: "/import/clusters",
            data: JSON.stringify(data),
            contentType: "application/json",
            success: function (clusterId) {
                alert("集群导入成功！");
                $("#redisImportForm")[0].reset();
                loadClusterInstance(clusterId);
            },
            error: function (e) {
                alert("集群导入失败：" + JSON.stringify(e));
            },
            complete: function (e) {
                unblockUI();
            }
        });
    }
}

function validate($_targetItem, errorTip) {
    var value = $_targetItem.val();
    if (!value) {
        $_targetItem.parent().parent().addClass("has-error");
        alert(errorTip);
        return false;
    } else {
        $_targetItem.parent().parent().removeClass("has-error");
        return true;
    }
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
    $_userName = $('#userName');
    $_userName.select2({
        data: usersOptionData,
        language: 'zh-CN',
        tags: tags
    });

    $_userName.on('change', function (e) {
        var text = $(this).find("option:selected").text();
        var name = text.substr(0, text.indexOf("<"))
        $('#userCnName').val(name);
    });

    // 非admin，禁止选择用户
    if (!isAdmin) {
        $('#userName').attr("disabled", "disabled");
    }

    $('#userName').val(userName).trigger('change');
}
