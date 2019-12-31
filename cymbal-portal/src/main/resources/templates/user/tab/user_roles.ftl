<div class="box-body">
    <table id="userRolesTable"
           data-show-refresh="true"
           data-toggle="table"
           data-url="/users/roles"
           data-flat="true"
           data-click-to-select="true"
           data-row-style="rowStyle"
           data-query-params="queryParams"
           data-pagination="true"
           data-search="true">
        <thead>
        <tr>
            <th data-field="id" data-visible="false"></th>
            <th data-field="userEnName" data-align="center">用户名</th>
            <th data-align="center" data-events="operate_user_role" data-formatter="operateFormatter">操作</th>
        </tr>
        </thead>
    </table>
</div><!-- /.box-body -->