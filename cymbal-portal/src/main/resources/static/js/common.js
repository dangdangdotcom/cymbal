// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


function changeContentHeader(h1, li1, li2) {
    var header = $('section.content-header');
    header.children('h1').text(h1);
    header.children('ol').children('li').eq(1).text(li1)
    header.children('ol').children('li').eq(2).text(li2)
}

// 存储本地访问路径
var hashMap;
var currentHash;

// 页面跳转，带回调方法参数
// href 目标url
// callback 回调方法
// data 要提交的表单数据
// tableInfo 页面中的table数据
function loadPage(href, data, tableInfo, callback) {
    if (!href) {
        return;
    }

    // blockUI first
    blockUI();

    clearIntervals();

    //	如果tableInfo位置是一个js方法,将其和callback调换
    if ($.isFunction(tableInfo)) {
        callback = tableInfo;
        tableInfo = {};
    }

    // href = href + '&random=' + Math.random();
    $.get(href, data, function (result) {
        updateHash(href, data, tableInfo);
        $(".content").html(result);
        unblockUI();
        // 回调方法
        if (callback) {
            callback.call(result);
        }
    }).fail(function () {
        var responseStatus = arguments[0].status;
        switch (responseStatus) {
            case 403:
                showErrorTip("您没有查看该集群详情的权限");
                break;
            default:
                // window.location.reload();
                break;
        }
        unblockUI();
    });
}

function blockUI(tip) {
    if (!tip) {
        tip = '加载中';
    }
    $.blockUI({
        message: '<p style="margin: 14px 0">' + tip + '</p>'
    });
}

function unblockUI() {
    $.unblockUI();
}

// 重写了原生的setInterval
// 意义是setInterval时记录句柄，异步loadPage时clearInterval
var _setInterval = window.setInterval;
window.setInterval = function (code, millisec, autoClear) {
    if (!window.intervalHandles) {
        window.intervalHandles = [];
    }
    var intervalHandle = _setInterval(code, millisec);
    if (autoClear) {
        window.intervalHandles.push(intervalHandle);
    }
    return intervalHandle;
}

function showErrorTip(tip) {
    $('#warningAlert').text(tip);
    $('#alertModal').modal('show');
    setTimeout(function () {
        $("#alertModal").modal('hide')
    }, 5000);
}

function clearIntervals() {
    // 清空intervalHanles
    if (window.intervalHandles) {
        var length = window.intervalHandles.length;
        for (var i = 0; i < length; i++) {
            var handle = window.intervalHandles.pop();
            clearInterval(handle);
        }
    }
}

function loadAllUser() {
    var pdlcUsers = sessionStorage.getItem("pdlcUsers");
    if (pdlcUsers) {
        return;
    }

    $.ajax({
        type: 'GET',
        url: "/users",
        dataType: "json",
        success: function (users) {
            sessionStorage.setItem("users", JSON.stringify(users));
        }
    });
}

function getAllUser() {
    return JSON.parse(sessionStorage.getItem("users"));
}

function updateHash(href, data, tableInfo) {
    currentHash = "#" + href;

    if (data || tableInfo) {
        currentHash += "?";
    }

    if (data) {
        currentHash += "data=" + encodeURI(JSON.stringify(data)) + "&";
    }

    if (tableInfo) {
        currentHash += "tableInfo=" + encodeURI(JSON.stringify(tableInfo));
    }

    location.hash = currentHash;

    hashMap[currentHash + "url"] = href;
    hashMap[currentHash + "data"] = data;
    hashMap[currentHash + "tableInfo"] = tableInfo;

    sessionStorage.setItem("HASH_MAP", JSON.stringify(hashMap));
}

function updateTableInfoHash(tableId, key, value) {
    var hash = location.hash;
    var href = hashMap[hash + "url"];
    var data = hashMap[hash + "data"];

    var tableInfo = hashMap[hash + "tableInfo"];
    if (!tableInfo) {
        tableInfo = {};
    } else {
        tableInfo = JSON.parse(JSON.stringify(tableInfo));
    }
    if (!tableInfo[tableId]) {
        tableInfo[tableId] = {};
    }
    // TODO 过滤掉变化后和变化前相等（默认值）的情况
    tableInfo[tableId][key] = value;

    updateHash(href, data, tableInfo);
}

function onBootstrapTableSerach(tableId, text) {
    updateTableInfoHash(tableId, "search", {
        text: text
    });
}

function onBootstrapTablePageChange(tableId, number, size) {
    updateTableInfoHash(tableId, "page", {
        number: number,
        size: size
    });
}

var DEFAULT_PAGESIZE = 10;
var DEFAULT_PAGENUMBER = 1;

function initBootstrapTable(tableId) {
    // 加载table数据
    var tableInfo = hashMap[location.hash + "tableInfo"];
    var searchText = '';
    var pageSize = DEFAULT_PAGESIZE;
    var pageNumber = DEFAULT_PAGENUMBER;
    if (tableInfo) {
        if (tableInfo[tableId]) {
            if (tableInfo[tableId]["search"]) {
                searchText = tableInfo[tableId]["search"]["text"];
            }
            if (tableInfo[tableId]["page"]) {
                pageSize = tableInfo[tableId]["page"]["size"];
                pageNumber = tableInfo[tableId]["page"]["number"];
            }
        }
    }

    var $_table = $('#' + tableId);
    $_table.bootstrapTable({
        onSearch: function (text) {
            onBootstrapTableSerach(tableId, text);
        },
        onPageChange: function (number, size) {
            onBootstrapTablePageChange(tableId, number, size);
        },
        pageSize: pageSize,
        pageNumber: pageNumber,
        searchText: searchText
    });
}