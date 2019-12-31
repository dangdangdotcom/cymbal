<div class="modal fade" id="configModal" tabindex="-1" role="dialog" aria-labelledby="configModalLabel" aria-hidden="true" class = "modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    Redis配置信息
                </h4>
                <input type="hidden" id="config-id"></input>
                <div class="form-group">
                    <label class="col-xs-4 control-label">名称:</label>
                    <div class="col-xs-4">
                        <a href="#" id="config-name"></a>
                    </div>
                </div>
            </div>
            <div class="modal-body">
                <button type="button" id="btnUpdateRedisConfig" onclick="updateRedisConfig(this)" class="btn btn-primary ladda-button" data-style="expand-right"><span class="ladda-label">生效配置</span></button>
                <div class="box-body table-responsive no-padding">
                  <table id="redisConfigDetailTable" class="table table-hover">
                    <thead>
                        <tr>
                            <th>配置项ID</th>
                            <th>参数</th>
                            <th>值</th>
                            <th>值范围</th>
                        </tr>
                    </thead>
                  </table>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
