<div class="box-body">
    <table id="serverInfoTable"
           data-show-refresh="true"
           data-toggle="table"
           data-url="/nodes"
           data-flat="true"
           data-click-to-select="true"
           data-row-style="rowStyle"
           data-query-params="queryParams"
           data-pagination="true"
           data-search="true">
        <thead>
        <tr>
            <th data-field="state" data-checkbox="true"></th>
            <th data-field="serverId" data-visible="false"></th>
            <th data-field="ip" data-formatter="serverIpFormatter" data-events="serverIpEvents" data-align="center">IP</th>
            <th data-field="serverType" data-visible="false" data-align="center">类型</th>
            <th data-field="env" data-align="center">环境类型</th>
            <th data-field="idc" data-align="center">数据中心</th>
            <th data-field="host" data-align="center">HOST</th>
            <th data-field="totalMemory" data-formatter="memoryFormatter" data-align="center" data-sortable="true">总内存</th>
            <th data-field="freeMemory" data-formatter="memoryFormatter" data-align="center"data-sortable="true">可分配内存</th>
            <th data-field="password" data-visible="false"></th>
            <th data-field="status" data-formatter="nodeStatusFormatter" data-align="center">redis环境状态</th>
        </tr>
        </thead>
    </table>
</div><!-- /.box-body -->