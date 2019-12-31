<div class="box-body">
    <table id="applicationFormTable"
           class="text-center"
           data-show-refresh="true"
           data-toggle="table"
           data-url="/application-forms?status=${status!""}"
           data-side-pagination="server"
           data-page-number=1
           data-page-size=10
           data-flat="true"
           data-click-to-select="true"
           data-query-params="queryParams"
           data-pagination="true"
           data-search="false">
        <thead>
        <tr>
            <th data-field="id" data-visible="false">ID</th>
            <th data-field="description" data-formatter="descriptionFormatter" data-align="center">申请说明</th>
            <th data-field="applicantCnName" data-align="center">申请人</th>
            <th data-field="belongSystem" data-align="center">所属系统</th>
            <th data-field="env" data-align="center">环境</th>
            <th data-field="redisMode" data-align="center">Redis模式</th>
            <th data-field="redisVersion" data-align="center">版本</th>
            <th data-field="cacheSize" data-align="center">单机内存</th>
            <th data-field="idc" data-align="center">数据中心</th>
            <th data-field="masterCount" data-align="center">主节点数量</th>
            <th data-field="replicaCount" data-align="center">节点副本数</th>
            <th data-field="creationDate" data-formatter="dateTimeFormatter" data-align="center">申请时间</th>
            <th data-field="status" data-formatter="statusFormatter" data-align="center">状态</th>
        </tr>
        </thead>
    </table>
</div><!-- /.box-body -->
