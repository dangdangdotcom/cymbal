<div class="modal fade" id="slaveOfModal" tabindex="-1" role="dialog" aria-labelledby="serverModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    设置主从关系
                </h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="box-body">
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-3 control-label">类型:</label>
                            <div class="col-sm-8">
                                <input type="radio" name="slaveof_type" value="OTHER_INST" checked="true">HOST PORT</input>&nbsp;
                                <input type="radio" name="slaveof_type" value="NO_ONE">NO ONE</input>
                            </div>
                        </div>
                        <div class="form-group" id="slaveInfoDiv">
                            <label for="description" class="col-sm-3 control-label">从节点信息:</label>
                            <div class="col-sm-8">
                                <input type="hidden" name="slaveOf_serverInstanceId" id="slaveOf_serverInstanceId"></input>
                                <input type="hidden" name="slaveOf_clusterId" id="slaveOf_clusterId"></input>
                                <input class="form-control" style="width:50%" id="slaveInfo" disabled="disabled">
                            </div>
                        </div>
                        <div class="form-group" id="masterInfoDiv">
                            <label for="description" class="col-sm-3 control-label">主节点信息:</label>
                            <div class="col-sm-8">
                                <p><i>HOST:</i></p>
                                <input class="form-control" style="width:50%" id="masterHost">
                                <p><i>PORT:</i></p>
                                <input class="form-control" style="width:50%" id="masterPort">
                                <p><i>MASTER PASSWORD(if needed):</i></p>
                                <input class="form-control" style="width:50%" id="masterPassword">
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onclick="configSlaveOf()" id="redisSaveBtn">提交</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->