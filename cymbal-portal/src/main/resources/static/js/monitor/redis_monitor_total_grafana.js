$(document).ready(function () {
    changeContentHeader('Redis全平台监控', '全平台监控', 'Redis');
});

function totalGrafanaPageLoaded() {
    var $_iframe = $("#redis_monitor_total_grafana_body");

    // 每次加载页面时，重置高度
    $_iframe.attr("height", "1650px");

    var initHeight = function() {
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