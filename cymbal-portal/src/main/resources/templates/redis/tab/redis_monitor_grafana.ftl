<div class="row" id="redis_monitor_grafana_row">
    <div style="margin-left: 20px;">
        <form class="form-horizontal">
            <div class="box-body" style="margin-left: 0px;margin-top: 0px;margin-right: 0px;margin-bottom: 0px;overflow: hidden;">
                <iframe id="redis_monitor_grafana_body" style="position: relative; left: -80px; overflow: hidden;" width="100%" height="1600px" frameborder="0" scrolling="no" url="/grafana/d/dd_ops_platform_redis/redis?var-CLUSTER_ID=${cluster.clusterId}" onload="grafanaPageLoaded()">

                </iframe>
            </div>
        </form>
    </div>
</div>

<div class="row hide" id="redis_monitor_grafana_tip_row">
    <div style="margin-left: 20px;">
        <div class="box-body" style="margin-left: 0px;margin-top: 0px;margin-right: 0px;margin-bottom: 0px;overflow: hidden;">
            <div class="callout callout-warning">
                <h4>该集群目前未启用监控</h4>
                <a style="cursor: pointer;" onclick="applyMonitor()">点击这里启用监控（初始化需要一小段时间）</a>
            </div>
        </div>
    </div>
</div>
<!-- js -->
<script src="/js/redis/redis_monitor_grafana.js"></script>