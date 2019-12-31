<div class="box-body">
    <#if isAdmin>
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
                        <a class="btn btn-app" onclick="startRedisServer()">
                            <i class="fa fa-play"></i> 启动
                        </a>
                        <a class="btn btn-app" onclick="stopRedisServer()">
                            <i class="fa fa-stop"></i> 停止
                        </a>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
            <!-- /.box -->
        </div>
    </#if>
    <table id="redisServerTable"
           data-show-refresh="true"
           data-toggle="table"
           data-url="/nodes/${node.id}/instances"
           data-flat="true"
           data-click-to-select="true"
           data-row-style="rowStyle"
           data-query-params="queryParams"
           data-pagination="true"
           data-search="true"
           data-response-handler="serverResponseHandler">
        <thead>
        <tr>
            <th data-field="state" data-checkbox="true"></th>
            <th data-field="id" data-visible="false"></th>
            <th data-field="role" data-formatter="roleFormatter" data-align="center">节点类型</th>
            <th data-field="port" data-sortable="true" data-align="center">端口</th>
            <th data-field="clusterId" data-align="center" data-events="clusterIdEvents"
                data-formatter="linkFormatter">集群ID
            </th>
            <th data-field="clusterDescription" data-align="center">集群名称</th>
            <th data-field="redisVersion" data-align="center">版本</th>
            <th data-field="userCnName" data-align="center">申请人</th>
            <th data-field="status" data-formatter="instanceStatusFormatter" data-align="center">状态</th>
            <th data-field="creationDate" data-formatter="dateTimeFormatter" data-align="center">创建时间</th>
        </tr>
        </thead>
    </table>
</div>
<!-- js -->
<script>
    var ip = '${node.ip}';
</script>
<script src="/js/redis/node_instance.js"></script>