<div class="modal fade" id="serverModal" tabindex="-1" role="dialog" aria-labelledby="serverModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    主机信息
                </h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" id="serverInfoForm">
                    <div class="box-body">
                        <div class="form-group" style="display:none">
                            <div class="col-sm-10">
                                <input type="hidden" id="node_id"></input>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">IP:</label>
                            <div class="col-sm-5">
                                <input class="form-control" id="node_ip">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">HOST:</label>
                            <div class="col-sm-5">
                                <input class="form-control" id="node_host">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">环境:</label>
                            <div class="col-sm-10">
                                <select class="selectpicker" id="node_env">
                                    <option value="TEST">测试环境</option>
                                    <option value="STAGING">预上线环境</option>
                                    <option value="PRODUCTION">生产环境</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">数据中心:</label>
                            <div class="col-sm-10">
                                <select class="selectpicker" id="node_idc">
                                    <option value="TEST">TEST</option>
                                    <option value="IDC4">IDC4</option>
                                    <option value="IDC5">IDC5</option>
                                    <option value="IDC7">IDC7</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">总内存:</label>
                            <div class="col-sm-5">
                                <input class="form-control" id="node_totalMemory">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">剩余内存:</label>
                            <div class="col-sm-5">
                                <input class="form-control" id="node_freeMemory" autocomplete="off"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">root口令:</label>
                            <div class="col-sm-5">
                                <input class="form-control" type="password" id="node_password" autocomplete="off" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">redis环境:</label>
                            <div class="col-sm-10">
                                <select class="selectpicker" id="node_status">
                                    <option value="UNINITIALIZED">未初始化</option>
                                    <option value="INITIALIZED">已初始化</option>
                                    <option value="DOWN">已下线</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">备注:</label>
                            <div class="col-sm-8">
                                <textarea class="form-control" rows="3" placeholder="" id="description"></textarea>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onclick="saveNodeInfo()" id="nodeSaveBtn">保存</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="alertModal" tabindex="-1" role="dialog" aria-labelledby="alertModalLabel">
    <strong>抱歉！</strong>
    <div id="warningAlert" class="alert alert-warning" align="center">
    </div>
</div>