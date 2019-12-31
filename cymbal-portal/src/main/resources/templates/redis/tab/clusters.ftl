<div class="box-body">
    <table id="redisServerTable"
           data-show-refresh="true"
           data-toggle="table"
           data-url="/user/clusters"
           data-flat="true"
           data-click-to-select="false"
           data-row-style="rowStyle"
           data-query-params="queryParams"
           data-pagination="true"
           data-search="true">
        <thead>
        <tr>
            <th data-field="id" data-visible="false"></th>
            <th data-field="clusterId" data-formatter="ahrefFormatter" data-align="center">集群ID</th>
            <th data-field="env" data-formatter="envFormatter" data-align="center">环境类型</th>
            <th data-field="idc" data-align="center">数据中心</th>
            <th data-field="masterCount" data-align="center">主节点数</th>
            <th data-field="replicaCount" data-align="center" data-formatter="replicaFormatter">从节点数</th>
            <th data-field="cacheSize" data-formatter="cacheSizeFormatter" data-align="center">总内存</th>
            <th data-field="redisMode" data-align="center">集群模式</th>
            <th data-field="redisVersion" data-align="center">版本</th>
            <th data-field="description" data-align="center">描述</th>
            <th data-field="userCnName" data-align="center">申请人</th>
            <th data-field="creationDate" data-formatter="dateTimeFormatter" data-align="center">创建时间</th>
        </tr>
        </thead>
    </table>
</div>
<!-- js -->
<script src="/js/redis/clusters.js"></script>