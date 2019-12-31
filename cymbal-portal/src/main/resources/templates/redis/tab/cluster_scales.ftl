<div class="box-body">
    <div class="toolbar">
        <a class="btn btn-app" onclick="openEnlargeModal('${cluster.clusterId}')">
            <i class="fa fa-plus"></i> 扩容
        </a>
        <#if cluster.redisMode = "CLUSTER">
            <a class="btn btn-app" onclick="retryEnlarge()">
                <i class="fa fa-repeat"></i> 重试
            </a>
        </#if>
    </div>
    <table id="clusterEnlargeTable"
           data-show-refresh="true"
           data-toggle="table"
           data-url="/clusters/${cluster.clusterId}/scales"
           data-flat="true"
           data-click-to-select="true"
           data-page-size="10"
           data-page-list="[10, 25, 50]"
           data-row-style="enlargeTableRowStyle"
           clusterId="${cluster.clusterId}"
           data-response-handler="clusterEnlargeTableDataHandler">
        <thead>
        <tr>
            <th data-radio="true"></th>
            <th data-field="id" data-visible="false"></th>
            <th data-field="type" data-formatter="enlargeTypeFormatter" data-align="center">类型</th>
            <th data-field="scaleNum" data-align="center">数量</th>
            <th data-field="status" data-formatter="enlargeStatusFormatter" data-align="center">状态</th>
            <th data-field="result" data-formatter="enlargeResultFormatter" data-align="center">结果</th>
            <th data-field="resultDesc" data-align="center">结果描述</th>
            <th data-field="operator" data-align="center">操作人</th>
            <th data-field="creationDate" data-formatter="dateTimeFormatter" data-align="center">开始时间</th>
            <th data-field="lastChangedDate" data-formatter="dateTimeFormatter" data-align="center">最后修改时间</th>
        </tr>
        </thead>
    </table>
</div>
<!-- js -->
<script src="/js/redis/cluster_scales.js"></script>