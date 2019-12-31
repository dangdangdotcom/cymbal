<div class="row" id="redisDiv">
    <div class="col-xs-12">
        <div class="box">
            <div>
                <!-- Nav tabs -->
                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="active"><a href="#redis-server-list" aria-controls="redis-server-list" role="tab" data-toggle="tab"><i class="fa fa-table">&nbsp;&nbsp;</i>Redis集群列表</a></li>
                    <li role="presentation"><a href="#redis-config-list" aria-controls="redis-config-list" role="tab" data-toggle="tab"><i class="fa fa-area-chart">&nbsp;&nbsp;</i>Redis配置</a></li>
                </ul>

                <!-- Tab panes -->
                <div class="tab-content" style="margin-left: 10px;margin-right: 10px;">
                    <div role="tabpanel" class="tab-pane active" id="redis-server-list">
                        <#include "../tab/clusters.ftl"/>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="redis-config-list">
                        <#include "../tab/configs.ftl"/>
                    </div>
                </div>
                <#include "../modal/config_modal.ftl"/>
            </div>
        </div><!-- /.box -->
    </div><!-- /.col -->
</div><!-- /.row -->