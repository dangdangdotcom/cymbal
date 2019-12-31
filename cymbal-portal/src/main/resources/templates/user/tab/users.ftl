<div class="box-body">
    <table id="userTable"
           data-show-refresh="true"
           data-toggle="table"
           data-url="/users"
           data-flat="true"
           data-click-to-select="true"
           data-row-style="rowStyle"
           data-query-params="queryParams"
           data-pagination="true"
           data-search="true">
        <thead>
        <tr>
            <th data-field="id" data-visible="false"></th>
            <th data-field="userName" data-align="center">用户名</th>
            <th data-field="userCnName" data-align="center">姓名</th>
            <th data-field="email" data-align="center">邮箱</th>
            <th data-field="email" data-align="center">邮箱</th>
            <th data-align="center" data-events="operate_user" data-formatter="operateFormatter">操作</th>
        </tr>
        </thead>
    </table>
</div><!-- /.box-body -->