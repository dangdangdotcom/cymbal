$(document).ready(function () {
    initBootstrapTable("userTable");
    changeContentHeader('用户列表', '用户管理', '用户列表');
});

function createUser() {
    showUserModal();
}

function saveUser() {
    var data = {
        "id": $('#user_id').val(),
        "userName": $('#user_name').val(),
        "userCnName": $('#user_cn_name').val(),
        "password": $('#user_password').val(),
        "email": $('#user_email').val(),
    };

    if (!data.userName || !data.userCnName || !data.password || !data.email) {
        alert("请填写全部用户信息！");
        return;
    }

    var type = "POST";
    var url = "/users";
    if (data.id) {
        url += "/" + data.id;
        type = "PUT";
    }

    var table = $('#userTable');
    table.bootstrapTable('showLoading');
    $.ajax({
        url: url,
        type: type,
        data: JSON.stringify(data),
        cache: false,
        contentType: 'application/json',
        complete: function(XMLHttpRequest, textStatus) {
            table.bootstrapTable('hideLoading');
            switch(XMLHttpRequest.status) {
                case 400:
                    alert(XMLHttpRequest.responseText);
                    break;
                case 405:
                    alert("当前权限验证模式下，不许允创建、编辑用户的操作!");
                    break;
                default:
                    table.bootstrapTable('refresh');
                    break;
            }
        }
    });

    $('#user_modal').modal('hide');
}

function editUser(user){
    $('#user_id').val(user.id);
    $('#user_name').val(user.userName);
    $('#user_cn_name').val(user.userCnName);
    $('#user_password').val(user.password);
    $('#user_email').val(user.email);

    $("#user_modal").modal("show");
}

function deleteUser(id) {
    var table = $('#userTable');
    table.bootstrapTable('showLoading');

    $.ajax({
        url: '/users/' + id,
        type: 'DELETE',
        contentType: "application/json",
        cache: false,
        success: function () {
            table.bootstrapTable('refresh');
            table.bootstrapTable('hideLoading');
        }
    });

    $('#user_modal').modal('hide');
}

function showUserModal(user) {
    $("#user_modal").modal("show");
}

window.operate_user = {
    'click .edit': function (e, value, row, index) {
        editUser(row);
        e.stopPropagation();
    },
    'click .delete': function (e, value, row, index) {
        if (!confirm("用户名: " + row.userName + "\n" +"姓名: " + row.userCnName + "\n" + "邮箱: " + row.email + "\n\n确定要删除以上用户吗？")) {
            return;
        }
        deleteUser(row.id);
        e.stopPropagation();
    }
};

function operateFormatter(value, row, index) {
    return [
        '<a class="edit" href="javascript:void(0)" title="编辑">',
        '<i class="fa fa-edit"></i>',
        '</a>',
        '<a class="delete" href="javascript:void(0)" style="margin-left: 2%" title="删除">',
        '<i class="fa fa-trash-o"></i>',
        '</a>'
    ].join('')
}