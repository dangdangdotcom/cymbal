// index's js
$(document).ready(function () {
    $.ajaxSetup({
        cache: false //关闭AJAX相应的缓存
    });

    // 初始化锚点事件，用于浏览器的前进后退
    initHashChangeEvent();

    //
    initFirstPage();

    loadAllUser();
});

function initHashChangeEvent() {
    // 从缓存中取出信息
    hashMap = sessionStorage.getItem("HASH_MAP");
    // 若缓存中不存在，则初始化
    if (!hashMap) {
        hashMap = {};
    } else {
        hashMap = JSON.parse(hashMap);
    }

    // 判断不是各种addAjax方法
    var addAjaxReg = new RegExp("AddAjax$");
    var addReg = new RegExp("Add$");

    // 设置ajax交互时，前进后退的支持
    $(window).hashchange(function () {
        var hash = location.hash;
        if (hash && hash != currentHash && hash != "#") {
            var url = hashMap[hash + "url"];
            var data = hashMap[hash + "data"];
            var tableInfo = hashMap[hash + "tableInfo"];
            if (url != undefined && !addAjaxReg.test(url) && !addReg.test(url)) {
                slideDownMenu(url);
                loadPage(url, data, tableInfo);
            }
        }
    });
}

function initFirstPage() {
    var lastHash = location.hash;
    if (!lastHash || lastHash.indexOf("undefined") != -1) {
        loadPage('/clusters/page');
    } else if (hashMap[lastHash + "url"]) {
        var url = hashMap[lastHash + "url"];
        var data = hashMap[lastHash + "data"];
        var tableInfo = hashMap[lastHash + "tableInfo"];

        slideDownMenu(url);
        loadPage(url, data, tableInfo);
    } else {
        lastHash = decodeURIComponent(lastHash);

        // 去掉首位#
        if (lastHash.startsWith("#")) {
            lastHash = lastHash.substring(1);
        }

        // 截取url
        var urlAndPara = lastHash.split("?");
        var url = urlAndPara[0];

        try {
            // 截取parameter
            var parameters = {};
            if (urlAndPara.length == 2) {
                var parameterStr = lastHash.split("?")[1];
                var parameterStrs = parameterStr.split("&");
                for (var each in parameterStrs) {
                    var name = parameterStrs[each].split("=")[0];
                    var value = parameterStrs[each].split("=")[1];
                    parameters[name] = JSON.parse(value);
                }
            }

            // 数据不完整，跳回主页
            if (url) {
                slideDownMenu(url);
                loadPage(url, parameters["data"], parameters["tableInfo"]);
            } else {
                loadPage('/redis/cluster/page');
            }
        } catch (err) {
            console.error(err);
            loadPage('/redis/cluster/page');
        }
    }
}

function slideDownMenu(url) {
    var $_aTag = $(".sidebar-menu .treeview a[href*='" + url + "']");
    if (!$_aTag.parents(".treeview").hasClass("active")) {
        // sidebar的展开
        $_aTag.parent().parent().prev().click();
    }
}

function test(data) {
    alert(data);
}