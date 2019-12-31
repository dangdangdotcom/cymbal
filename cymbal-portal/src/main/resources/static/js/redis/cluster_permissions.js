$(document).ready(function () {
    //turn to inline mode
    $.fn.editable.defaults.mode = 'inline';
    // 加载table数据
    $('#clusterPermissionsTable').bootstrapTable();

    initUserSelect();
});

function openPermissionModal(userName) {
    $('#userName').val(userName).trigger('change');
    $('#clusterPermissionModal').modal('show');
}

function addPermission() {
    blockUI();
    var userName = $('#userName').val();
    var userCnName = $('#userCnName').val();
    var clusterId = $('#clusterPermissionsTable').attr("clusterId");
    $.ajax({
        type: "POST",
        url: "/clusters/" + clusterId + "/permissions",
        contentType: "application/json",
        data: JSON.stringify({
            clusterId: clusterId,
            userName: userName,
            userCnName: userCnName
        }),
        success: function (response) {
            alert("授权成功！\r该用户现在可在redis平台查看本集群信息");
            $('#clusterPermissionsTable').bootstrapTable('refresh');
            $('#clusterPermissionModal').modal('hide');
        },
        error: function (xhr, error) {
            alert("出现未知错误，请刷新后重试!");
        },
        complete: function () {
            unblockUI();
        }
    });
}

function deletePermissions() {
    var table = $('#clusterPermissionsTable');
    var hasError = false;
    var errorMsg = "";

    var needDeletePermissionIds = [];
    var needDeletePermissionUserCnNames = " ";
    $.map(table.bootstrapTable('getSelections'), function (row) {
        needDeletePermissionIds.push(row.id);
        needDeletePermissionUserCnNames += row.userCnName + "、"
    });

    if (needDeletePermissionIds.length < 1) {
        alert("请至少选择一项！");
        return;
    }

    var clusterId = $('#clusterId').val();

    if (confirm('确定取消"' + needDeletePermissionUserCnNames.substr(0, needDeletePermissionUserCnNames.length - 1) + ' "的授权？\r取消后，这些用户将无法在查看本集群信息！')) {
        blockUI();
        $.ajax({
            type: "DELETE",
            url: "/clusters/" + clusterId + "/permissions",
            contentType: "application/json",
            data: JSON.stringify(needDeletePermissionIds),
            success: function () {
                alert("授权已取消！");
                $('#clusterPermissionsTable').bootstrapTable('refresh');
            },
            error: function (xhr, error) {
                alert("出现未知错误，请刷新后重试!");
            },
            complete: function () {
                unblockUI();
            }
        });
    }
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
    $_applicantEnName = $('#userName');
    $_applicantEnName.select2({
        data: usersOptionData,
        language: 'zh-CN',
        tags: tags
    });
    $_applicantEnName.on('change', function (e) {
        var text = $(this).find("option:selected").text();
        var name = text.substr(0, text.indexOf("<"))
        $('#userCnName').val(name);
    });
}