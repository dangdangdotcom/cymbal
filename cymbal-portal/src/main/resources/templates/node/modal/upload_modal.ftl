<div class="modal fade" id="uploadFileModal" tabindex="-1" role="dialog" aria-labelledby="serverModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×
                </button>
                <h4 class="modal-title" id="myModalLabel">
                                                                上传主机信息附件
                </h4>
            </div>
            <div class="modal-body">
                <form role="form" id="uploadServerForm" method="post" enctype="multipart/form-data">
                  <div class="box-body">
                    <div class="form-group">
                      <input type="file" name="uploadFile" id="serverInfoFile">
                      <p class="help-block">支持Excel 2007及以上版本，点击下载模板：
                          <a href="/download/Template" class="glyphicon glyphicon-download-alt"></a>
                      </p>
                    </div>
                  </div>
                  <div class="box-footer">
                    <button type="submit" class="btn btn-primary">上传</button>
                  </div>
                </form>

            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="alertModal" tabindex="-1" role="dialog" aria-labelledby="alertModalLabel">
    <strong>抱歉！</strong>
    <div id="warningAlert" class="alert alert-warning" align="center">
    </div>
</div>