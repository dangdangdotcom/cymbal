<div class="modal fade" id="user_modal" tabindex="-1" role="dialog" aria-labelledby="user_modal_label" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    用户信息
                </h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" id="user_from">
                    <div class="box-body">
                        <div class="form-group" style="display:none">
                            <div class="col-sm-10">
                                <input type="hidden" id="user_id"></input>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">用户名:</label>
                            <div class="col-sm-5">
                                <input class="form-control" id="user_name">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">姓名:</label>
                            <div class="col-sm-5">
                                <input class="form-control" id="user_cn_name">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">邮箱:</label>
                            <div class="col-sm-5">
                                <input class="form-control" id="user_email">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-2 control-label">密码:</label>
                            <div class="col-sm-5">
                                <input class="form-control" type="password" id="user_password" autocomplete="off"/>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onclick="saveUser()" id="userSaveBtn">保存</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->