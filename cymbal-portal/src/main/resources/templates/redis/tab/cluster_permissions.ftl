<div class="box-body">
    <div class="toolbar">
        <a class="btn btn-app" onclick="openPermissionModal('${SPRING_SECURITY_CONTEXT.authentication.principal.username}')">
            <i class="fa fa-user-plus"></i> 新建授权
        </a>
        <a class="btn btn-app" onclick="deletePermissions()">
            <i class="fa fa-remove"></i> 取消授权
        </a>
    </div>
    <table id="clusterPermissionsTable"
           data-show-refresh="true"
           data-toggle="table"
           data-url="/clusters/${cluster.clusterId}/permissions"
           data-flat="true"
           data-click-to-select="true"
           data-search="true"
           clusterId="${cluster.clusterId}">
        <thead>
        <tr>
            <th data-field="state" data-checkbox="true"></th>
            <th data-field="id" data-visible="false"></th>
            <th data-field="userCnName" data-align="center">被授权人姓名</th>
            <th data-field="userName" data-align="center">被授权人账号</th>
        </tr>
        </thead>
    </table>
</div>
<!-- js -->
<script src="/js/redis/cluster_permissions.js"></script>