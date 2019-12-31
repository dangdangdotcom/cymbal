<div class="box-body">
    <div class="col-xs-14">
        <div class="box box-primary  box-solid collapsed-box">
            <div class="box-header with-border">
                <h3 class="box-title">功能按钮区</h3>

                <div class="box-tools pull-right">
                    <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i>
                    </button>
                </div>
                <!-- /.box-tools -->
            </div>
            <!-- /.box-header -->
            <div class="box-body" style="display: none;">
                <div class="toolbar">
                    <#if cluster.userName = SPRING_SECURITY_CONTEXT.authentication.principal.username || isAdmin>
                        <a class="btn btn-app" onclick="startRedisServer()">
                            <i class="fa fa-play"></i> 启动
                        </a>
                        <a class="btn btn-app" onclick="stopRedisServer()">
                            <i class="fa fa-stop"></i> 停止
                        </a>
                    </#if>
                    <#if isAdmin && cluster.redisMode != 'CLUSTER'>
                        <a class="btn btn-app" onclick="openSlaveOfModal()">
                            <i class="fa fa-wrench"></i> SLAVEOF
                        </a>
                    </#if>
                    <!-- 仅支持3.x增从节点，主不支持迁移slot -->
                    <#if isAdmin && cluster.redisMode = 'CLUSTER'>
                        <a class="btn btn-app" onclick="delClusterNodes()">
                            <i class="fa fa-minus"></i> 移出集群
                        </a>
                        <a class="btn btn-app" onclick="failover()">
                            <i class="fa fa-repeat"></i> 主从切换
                        </a>
                    </#if>
                </div>
            </div>
            <!-- /.box-body -->
        </div>
        <!-- /.box -->
    </div>
    <table id="redisServerTable"
           data-show-refresh="true"
           data-toggle="table"
           data-url="/clusters/${cluster.clusterId}/instances"
           data-flat="true"
           data-click-to-select="true"
           data-row-style="rowStyle"
           data-query-params="queryParams"
           data-search="true"
           data-response-handler="responseHandler">
        <thead>
        <tr>
            <th data-field="state" data-checkbox="true"></th>
            <th data-field="id" data-visible="false"></th>
            <th data-field="role" data-formatter="instanceRoleFormatter" data-align="right">主从关系</th>
            <th data-field="ip" data-sortable="true" data-align="center" data-events="nodeIpEvents"
                data-formatter="instanceLinkFormatter">主机IP
            </th>
            <th data-field="port" data-sortable="true" data-align="center">服务端口</th>
            <th data-field="type" data-formatter="instanceTypeFormatter" data-align="center">节点类型</th>
            <th data-field="clusterId" data-visible="false" data-align="center">集群</th>
            <th data-field="status" data-formatter="instanceStatusFormatter" data-align="center">状态</th>
            <th data-field="creationDate" data-formatter="dateTimeFormatter" data-align="center">创建时间</th>
        </tr>
        </thead>
    </table>
</div>
<!-- js -->
<script>
    var clusterDescription = '${cluster.description}';
</script>
<script src="/js/redis/cluster_instances.js"></script>
