<div class="box-body">
    <div class="row">
        <div style="margin-left: 20px;">
            <form class="form-horizontal">
                <div class="box-body">
                    <div class="form-group">
                        <a class="col-sm-2">选择Redis实例</a>
                        <div class="col-sm-10">
                            <div class="form-group" id="redis-client-server-radio">
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="redis-client-console" id="redis-client-console">

        </div>
    </div>
    <div class="row">
        <div class="box" class="col-sm-9">
            <div class="box-header">
                <h3 class="box-title">快捷命令区</h3>
            </div>
            <div class="box-body">
                <p>
                    <button type="button" class="btn btn-flat btn-success" onclick="quickCommand('ping')">ping</button>
                    <button type="button" class="btn btn-flat btn-success" onclick="quickCommand('info')">info</button>
                    <button type="button" class="btn btn-flat btn-success" onclick="quickCommand('info memory')">info memory</button>
                    <button type="button" class="btn btn-flat btn-success" onclick="quickCommand('info replication')">info replication</button>
                    <button type="button" class="btn btn-flat btn-warning" onclick="quickCommand('cluster nodes')">cluster nodes</button>
                    <button type="button" class="btn btn-flat btn-warning" onclick="quickCommand('cluster slots')">cluster slots</button>
                    <button type="button" class="btn btn-flat btn-warning" onclick="quickCommand('client list')">client list</button>
                    <button type="button" class="btn btn-flat btn-info" onclick="quickCommand('slowlog get')">slowlog get</button>
                </p>
            </div>
        </div>
    </div>
</div>

<!-- console 样式 -->
<style type="text/css" media="screen">
    /* First console */
    div.redis-client-console {
        word-wrap: break-word;
        font-size: 14px
    }

    div.redis-client-console div.jquery-console-inner {
        width: 95%;
        height: 450px;
        background: #333;
        padding: 0.5em;
        overflow: auto
    }

    div.redis-client-console div.jquery-console-prompt-box {
        color: #fff;
        font-family: monospace;
    }

    div.redis-client-console div.jquery-console-focus span.jquery-console-cursor {
        background: #fefefe;
        color: #333;
        font-weight: bold
    }

    div.redis-client-console div.jquery-console-message {
        white-space: pre;
    }

    div.redis-client-console div.jquery-console-welcome {
        color: #ef0505;
        font-family: sans-serif;
        font-weight: bold;
        padding: 0.1em;
        white-space: normal;
    }

    div.redis-client-console div.jquery-console-message-error {
        color: #ef0505;
        font-family: sans-serif;
        font-weight: bold;
        padding: 0.1em;
    }

    div.redis-client-console div.jquery-console-message-value {
        color: #1ad027;
        font-family: monospace;
        padding: 0.1em;
    }

    div.redis-client-console div.jquery-console-message-type {
        color: #52666f;
        font-family: monospace;
        padding: 0.1em;
    }

    div.redis-client-console span.jquery-console-prompt-label {
        font-weight: bold
    }
</style>
<!-- js -->
<script src="/plugins/jQuery-console/jquery.console.js"></script>
<script src="/js/redis/redis_client.js"></script>
