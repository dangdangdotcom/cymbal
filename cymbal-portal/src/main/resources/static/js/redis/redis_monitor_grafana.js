$(document).ready(function () {
    initRedisMonitorGrafana();
});

var grafanaMonitorInited = true;
var clusterId;

function initRedisMonitorGrafana() {
    var $_iframe = $("#redis_monitor_grafana_body");
    var src = $_iframe.attr("url");

    // query prometheus to check if the reporter is ok
    clusterId = src.substring(src.lastIndexOf("=") + 1, src.length);
    $.ajax({
        url: '/prometheus/api/v1/series?match[]=redis_up{alias=~"' + clusterId + '"}',
        type: "get",
        dataType: "json",
        success: function (resposne) {
            if (resposne && resposne.data.length == 0) {
                grafanaMonitorInited = false;
                $("#redis_monitor_grafana_row").hide();
                $("#redis_monitor_grafana_tip_row").removeClass("hide");
            }
        }
    });

    // 监听选项卡的打开
    $('a[href="#redis-monitor"]').one('shown.bs.tab', function (e) {
        if (grafanaMonitorInited) {
            blockUI();
            $_iframe.attr("src", src);
        }
    });
}

function grafanaPageLoaded() {
    var $_iframe = $("#redis_monitor_grafana_body");

    // 每次加载页面时，重置高度
    $_iframe.attr("height", "1600px");

    var initHeight = function () {
        setTimeout(function () {
            var $_dashboardContainer = $_iframe.contents().find(".dashboard-container");
            if ($_dashboardContainer.length == 0) {
                initHeight();
            } else {
                // 100 is a magic number
                $_iframe.attr("height", ($_dashboardContainer.height() + 100) + "px");
            }
        }, 1000);
    }
    initHeight();

    unblockUI();
}

function applyMonitor() {
    blockUI("正在启用监控，此操作时间可能较长");

    $.ajax({
        url: '/clusters/' + clusterId + '/monitors',
        type: "POST",
        dataType: "json",
        complete: function (XMLHttpRequest, textStatus) {
            unblockUI();
            switch (XMLHttpRequest.status) {
                case 500:
                    alert("监控启动失败，异常信息：" + XMLHttpRequest.responseText);
                    break;
                default:
                    alert("监控启用成功，请在刷新后查看");
                    break;
            }
            window.location.reload();
        }
    });
}