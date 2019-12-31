<div class="modal fade" id="nodeModal" tabindex="-1" role="dialog" aria-labelledby="serverModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    集群扩容
                </h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="box-body">
                        <div class="form-group" style="display:none">
                            <div class="col-sm-10">
                                <input type="hidden" name="clusterId" id="clusterIdOfNodeModal"></input>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-3 control-label">类型:</label>
                            <div class="col-sm-8">
                                <input type="radio" name="type" value="HORIZONTAL" checked="true">水平扩容</input>&nbsp;
                                <input type="radio" name="type" value="VERTICAL">垂直扩容</input>
                                <input type="radio" name="type" value="SLAVE_ONLY">补充从节点</input>
                            </div>
                        </div>
                        <div class="form-group" id="horizontalDiv">
                            <label for="description" class="col-sm-3 control-label">主节点数量:</label>
                            <div class="col-sm-8">
                                <input class="form-control" style="width:20%" id="horizontalEnlargeNum" autocomplete="off" />
                                <p><i>每组节点包括1个主节点和多个从节点，从节点数量与当前服务配置相同。</i></p>
                            </div>
                        </div>
                        <div class="form-group" style="display:none" id="verticalDiv">
                            <label for="description" class="col-sm-3 control-label">每个节点扩容大小(GB):</label>
                            <div class="col-sm-8">
                                <input class="form-control" style="width:20%" id="verticalEnlargeNum" autocomplete="off" />
                                <p><i>集群中的每个节点的maxmemory都会增加该数值。</i></p>
                            </div>
                        </div>
                        <div class="form-group" style="display:none" id="slaveOnlyDiv">
                            <label for="description" class="col-sm-3 control-label">从节点数量:</label>
                            <div class="col-sm-8">
                                <input class="form-control" style="width:20%" id="slaveOnlyEnlargeNum" autocomplete="off" />
                                <p><i>每组节点均会增加相同数量的从节点。</i></p>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onclick="addClusterNode()" id="redisSaveBtn">提交</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->