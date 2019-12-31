<div class="box" id="serverInfoDiv">
    <div class="box-header">
      <h3 class="box-title">主机信息</h3>
    </div>
    <div class="toolbar" id="uploadFileBtn">
      <a class="btn btn-app" onclick="$('#uploadFileModal').modal('show');">
        <i class="fa fa-folder-open"></i> 上传附件
      </a>
      <a class="btn btn-app" onclick="initSelectedNode()">
        <i class="fa fa-wrench"></i> 初始化redis
      </a>
      <a class="btn btn-app" onclick="editSelectedServerInfo()">
        <i class="fa fa-edit"></i> 修改
      </a>
    </div>
    <!-- Table Body -->
    <#include "nodes.ftl"/>
    <!-- Modal tabs -->
    <#include "modal/upload_modal.ftl"/>
    <#include "modal/node_modal.ftl"/>
</div>

<script src="/js/node/node.js"></script>