<div class="box-body">
    <div class="toolbar">
        <a class="btn btn-app" onclick="loadConfigModal()">
            <i class="fa fa-edit"></i> 修改
        </a>
    </div>
    <table id="redisConfigTable" data-toggle="table"
           data-url="/user/configs"
           data-flat="true"
           data-click-to-select="true"
           data-row-style="rowStyle"
           data-show-export="true"
           data-page-size="10"
           data-page-list="[10, 25, 50]"
           data-sort-name="creationDate"
           data-pagination="true"
           data-search="true">
        <thead>
        <tr>
            <th data-radio="true"></th>
            <th data-field="id" data-visible="false"></th>
            <th data-field="configName" data-align="center">配置名称</th>
            <th data-field="clusterId" data-align="center">集群ID</th>
            <th data-field="redisVersion" data-align="center">版本</th>
            <th data-field="userCnName" data-align="center">申请人</th>
            <th data-field="creationDate" data-formatter="dateTimeFormatter" data-align="center">创建时间</th>
        </tr>
        </thead>
    </table>
</div>