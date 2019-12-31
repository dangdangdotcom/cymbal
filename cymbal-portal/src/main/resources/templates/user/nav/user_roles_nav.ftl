<div class="box" id="user_role_div">
    <div class="box-header">
        <h3 class="box-title">用户权限</h3>
    </div>
    <div class="toolbar" id="uploadFileBtn">
        <a class="btn btn-app" onclick="createUserRole()">
            <i class="fa fa-folder-open"></i> 新增管理员
        </a>
    </div>
    <!-- Table Body -->
    <#include "../tab/user_roles.ftl"/>
    <!-- Modal tabs -->
    <#include "../modal/user_role_modal.ftl"/>
</div>

<script src="/js/user/user_role.js"></script>