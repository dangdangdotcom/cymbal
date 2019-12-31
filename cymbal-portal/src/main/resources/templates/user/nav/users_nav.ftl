<div class="box" id="user_div">
    <div class="box-header">
        <h3 class="box-title">用户信息</h3>
    </div>
    <div class="toolbar" id="uploadFileBtn">
        <a class="btn btn-app" onclick="createUser()">
            <i class="fa fa-folder-open"></i> 新增用户
        </a>
    </div>
    <!-- Table Body -->
    <#include "../tab/users.ftl"/>
    <!-- Modal tabs -->
    <#include "../modal/user_modal.ftl"/>
</div>

<script src="/js/user/user.js"></script>