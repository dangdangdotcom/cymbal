$(document).ready(function () {
    initBootstrapTable("userRolesTable");
    changeContentHeader('管理员列表', '用户权限管理', '管理员列表');
    initUserSelect();
});

function createUserRole() {
    $("#user_role_modal").modal("show");
}

window.operate_user_role = {
    'click .delete': function (e, value, row, index) {
        if (!confirm("用户名: " + row.userEnName + "\n\n确定撤销以上用户的管理员权限吗？")) {
            return;
        }
        deleteUser(row.id);
        e.stopPropagation();
    }
};

function operateFormatter(value, row, index) {
    return [
        '<a class="delete" href="javascript:void(0)" style="margin-left: 2%" title="删除">',
        '<i class="fa fa-trash-o"></i>',
        '</a>'
    ].join('')
}

function initUserSelect() {
    var users = getAllUser();
    var usersOptionData = [];
    for (var index in users) {
        var userOption = {
            id: users[index].userName,
            text: users[index].userCnName + '<' + users[index].email + '>'
        }
        usersOptionData.push(userOption);
    }
    var tags = true;
    if (usersOptionData.length > 0) {
        tags = false;
    }
    // 初始化select搜索框
    $_userEnName = $('#userName');
    $_userEnName.select2({
        data: usersOptionData,
        language:'zh-CN',
        tags: tags
    });
    $_userEnName.on('change', function (e) {
        $('#userEnName').val($(this).find("option:selected").val());
    });
}

function addUserRole() {
    var data = {
      userEnName: $('#userEnName').val()
    };

    var table = $('#userRolesTable');
    table.bootstrapTable('showLoading');
    $.ajax({
        url: "/users/roles",
        type: "POST",
        data: JSON.stringify(data),
        cache: false,
        contentType: 'application/json',
        success: function () {
            table.bootstrapTable('refresh');
            table.bootstrapTable('hideLoading');
        }
    });

    $('#user_role_modal').modal('hide');
}