<div class="row" id="redisDiv">
    <div class="col-xs-12">
        <div class="box">
            <div class="box-header">
            </div>
            <div>
                <!-- Nav tabs -->
                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation"><a href="#redis-server-info" aria-controls="redis-server-info" role="tab" data-toggle="tab"><i class="fa fa-info">&nbsp;&nbsp;</i>详细信息</a></li>
                    <li role="presentation" class="active"><a href="#redis-server-instance-list" aria-controls="redis-server-list" role="tab" data-toggle="tab"><i class="fa fa-table">&nbsp;&nbsp;</i>Redis实例</a></li>
                    <#if cluster??>
                        <li role="presentation"><a href="#redis-monitor" aria-controls="redis-monitor" role="tab" data-toggle="tab"><i class="fa  fa-pie-chart">&nbsp;&nbsp;</i>redis监控</a></li>
                    </#if>
                    <li role="presentation"><a href="#redis-client" aria-controls="redis-client" role="tab" data-toggle="tab"><i class="fa fa-tv">&nbsp;&nbsp;</i>redis-cli</a></li>
                    <#if cluster??>
                        <#if SPRING_SECURITY_CONTEXT.authentication.principal.username = cluster.userName || isAdmin>
                            <li role="presentation"><a href="#cluster-permission" aria-controls="cluster-permission" role="tab" data-toggle="tab"><i class="fa fa-lock">&nbsp;&nbsp;</i>集群访问权限</a></li>
                        </#if>
                        <#if isAdmin>
                            <li role="presentation"><a href="#cluster-scale" aria-controls="cluster-scale" role="tab" data-toggle="tab"><i class="fa fa-expand">&nbsp;&nbsp;</i>扩容</a></li>
                        </#if>
                    </#if>
                </ul>
                <!-- Tab panes -->
                <div class="tab-content" style="margin-left: 10px;margin-right: 10px;">
                    <div role="tabpanel" class="tab-pane" id="redis-server-info">
                        <#if cluster??>
                            <#include "../tab/cluster_info.ftl"/>
                        </#if>
                        <#if node??>
                            <#include "../tab/node_info.ftl"/>
                        </#if>
                    </div>
                    <div role="tabpanel" class="tab-pane active" id="redis-server-instance-list">
                        <#if cluster??>
                            <#include "../tab/cluster_instances.ftl"/>
                        </#if>
                        <#if node??>
                            <#include "../tab/node_instances.ftl"/>
                        </#if>
                    </div>
                    <#if cluster??>
                        <#if defaultMonitor == true>
                            <div role="tabpanel" class="tab-pane" id="redis-monitor">
                                <#include "../tab/redis_monitor.ftl"/>
                            </div>
                        <#else>
                            <div role="tabpanel" class="tab-pane" id="redis-monitor">
                                <div style="height: 10px;width: 100%"></div>
                                <#include "../tab/redis_monitor_grafana.ftl"/>
                            </div>
                        </#if>
                    </#if>
                    <div role="tabpanel" class="tab-pane" id="redis-client">
                        <div style="height: 10px;width: 100%"></div>
                        <#include "../tab/redis_client.ftl"/>
                    </div>
                    <#if cluster??>
                        <#if SPRING_SECURITY_CONTEXT.authentication.principal.username = cluster.userName || isAdmin>
                            <div role="tabpanel" class="tab-pane" id="cluster-permission">
                                <#include "../tab/cluster_permissions.ftl"/>
                            </div>
                        </#if>
                    </#if>
                    <#if isAdmin>
                        <div role="tabpanel" class="tab-pane" id="cluster-scale">
                            <#include "../tab/cluster_scales.ftl"/>
                        </div>
                    </#if>
                </div>
                <#include "../modal/scale_modal.ftl"/>
                <#include "../modal/slaveof_modal.ftl"/>
                <#include "../modal/cluster_permission_modal.ftl"/>
            </div>
        </div><!-- /.box -->
    </div><!-- /.col -->
</div><!-- /.row -->