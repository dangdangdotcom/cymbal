var controller;
var forbidenCommand = ["flushdb", "flushall", "keys", "monitor", "subscribe", "shutdown", "--rdb", "--scan"];
$(document).ready(function () {
    var console = $("#redis-client-console");
    controller = console.console({
        promptLabel: '10.4.5.1:8383> ',
        commandValidate: function (line) {
            var cmd = line.trim().toLowerCase();
            if (!cmd) {
                return "请输入有效的命令";
            }
            for (var i = 0; i < forbidenCommand.length; i++) {
                if (cmd.startsWith(forbidenCommand[i])) {
                    return "该命令禁止使用";
                }
            }
            return true;
        },
        commandHandle: function (line, report) {
            setTimeout(function () {
                var serverInstances = [];
                var $_checkedRadio = $("#redis-client-server-radio input[type='radio']:checked");
                var checkedId = $_checkedRadio.val();
                if (checkedId == "all") {
                    // 遍历取出所有serverInstance
                    $("#redis-client-server-radio input[type='radio']").not(":checked").each(function () {
                        var $_this = $(this);
                        serverInstances.push({
                            id: $_this.val(),
                            context: $_this
                        });
                    });
                } else {
                    serverInstances.push({
                        id: checkedId
                    });
                }

                var counter = 0;

                for (var index in serverInstances) {
                    var serverInstance = serverInstances[index];

                    $.ajax({
                        type: "POST",
                        url: "/command/instances/" + serverInstance.id,
                        data: line,
                        async: true,
                        contentType: 'application/json',
                        dataType: 'json',
                        context: serverInstance.context,
                        timeout: 3000 * serverInstances.length,
                        success: function (response) {
                            var msgs = [];

                            // 如果勾选的是全部发送，则先输出服务uri
                            if (this.attr) {
                                msgs.push({
                                    msg: this.attr("uri") + this.attr("extraInfo") + ":",
                                    className: "jquery-console-message-type"
                                });
                            }

                            for (var i in response) {
                                var message = response[i];

                                if (!message) {
                                    if (i == 0) {
                                        message = "(nil)";
                                    }
                                } else {
                                    // slowlog 的发生时间特殊处理
                                    if (line.indexOf("slowlog get") >= 0) {
                                        if (message.indexOf("2) (integer)") > 0) {
                                            // 找到发生时间
                                            var time = message.substr(message.lastIndexOf(" ") + 1);
                                            message += " (" + formatTimeMillis(time) + ")";
                                        }
                                    }
                                }

                                msgs.push({
                                    msg: message,
                                    className: "jquery-console-message-value"
                                });
                            }

                            counter++;
                            if (counter == serverInstances.length) {
                                report(msgs);
                            } else {
                                report(msgs, true);
                            }
                        },
                        error: function (xhr) {
                            var msgs = [];

                            // 如果勾选的是全部发送，则先输出服务uri
                            if (this.attr) {
                                msgs.push({
                                    msg: this.attr("uri") + this.attr("extraInfo") + ":",
                                    className: "jquery-console-message-error"
                                });
                            }

                            if (xhr.statusText == 'timeout') {
                                msgs.push({
                                    msg: "请求超时，请检查网络情况并重试！",
                                    className: "jquery-console-message-error"
                                });
                            } else {
                                msgs.push({
                                    msg: "error: " + xhr.responseJSON[0],
                                    className: "jquery-console-message-error"
                                });
                            }

                            counter++;
                            if (counter == serverInstances.length) {
                                report(msgs);
                            } else {
                                report(msgs, true);
                            }
                        }
                    });
                }
            }, 0);
        },
        autofocus: true,
        animateScroll: true,
        promptHistory: true,
        welcomeMessage: '注意: 生产环境不要使用危险命令！',
        charInsertTrigger: function (keycode, line) {
                // Let you type until you press a-z
                // Never allow zero.
                return true;
        }
    });

    // 监听tab页的打开
    var firstOpen = true;
    $('a[href="#redis-client"]').on('shown.bs.tab', function (e) {
        if (firstOpen) {
            initRedisListForClient();
            firstOpen = false;
        }
    });

    // 绑定redis选择被选择的事件
    $("#redis-client-server-radio").delegate('input[type="radio"].minimal', "ifChecked", function (event) {
        var uri = $(this).attr('uri');
        changeRedisInstance(uri);
    });
});

function initRedisListForClient() {
    var data = $('#redisServerTable').bootstrapTable("getData");
    var $_radio_parent = $("#redis-client-server-radio");
    $_radio_parent.empty();
    var radioCount = 0;
    for (var i = 0; i < data.length; i++) {
        radioCount++;
        var ipAndPort = data[i]["ip"] + ":" + data[i]["port"];
        var id = data[i]["id"];
        var extraInfo = "";
        if (data[i]["type"] == "SENTINEL") {
            extraInfo = "(Sentinel)"
        }

        if (data.length == 1 && i == 0) {
            $_radio_parent.append(' <input type="radio" name="redis" value="' + id + '" class="minimal" uri="' + ipAndPort + '" extraInfo="' + extraInfo + '" checked /> ' + ipAndPort + extraInfo);
        } else {
            $_radio_parent.append(' <input type="radio" name="redis" value="' + id + '" class="minimal" uri="' + ipAndPort + '" extraInfo="' + extraInfo + '"/> ' + ipAndPort + extraInfo);
        }

        if (radioCount % 5 == 0) {
            $_radio_parent.append('<br />');
        }
    }

    if (data.length > 1) {
        $_radio_parent.append(' <input type="radio" name="redis" value="all" class="minimal" uri="all" checked /> 所有节点');
        changeRedisInstance("all");
    }

    $('input[type="radio"].minimal').iCheck({
        checkboxClass: 'icheckbox_minimal-blue',
        radioClass: 'iradio_minimal-blue'
    });
}

function changeRedisInstance(promptLabel) {
    promptLabel = promptLabel + "> ";
    controller.promptLabel = promptLabel;
    controller.clearScreen();
    // controller.report([{
    //     msg: "注意: 生产环境不要使用危险命令！",
    //     className: "jquery-console-message-error"
    // }]);
}

function quickCommand(command) {
    controller.promptText(command);

    // 触发回车
    var evt = $.Event('keydown', {keyCode: 13});
    controller.typer.trigger(evt);
}

function confirmCommand(command) {
    var result = confirm("确定执行危险命令 " + command + " ?");
    return result;
}

function formatTimeMillis(timeMillis) {
    var date = new Date(parseInt(timeMillis) * 1000);
    var dateStr = date.getFullYear() + '-'+ addZero(date.getMonth() + 1) + '-' + addZero(date.getDate())
        + ' '+ addZero(date.getHours()) + ':' + addZero(date.getMinutes()) + ':'+ addZero(date.getSeconds());
    return dateStr;
}

// 补零操作
function addZero(num){
    if (parseInt(num) < 10) {
        num = '0' + num;
    }
    return num;
}
