<div class="box-body">
    <div class="row">
        <div style="margin-left: 20px;">
            <form class="form-horizontal">
                <div class="box-body">
                    <div class="form-group">
                        <a class="col-sm-2">选择Redis实例</a>
                        <div class="col-sm-10">
                            <div class="form-group" id="redis-monitor-server-radio">
                            </div>
                        </div>
                    </div>
                    <!--
                    <div class="form-group">
                        <a class="col-sm-1" class="">显示时间</a>
                        <div class="col-sm-10">
                            <div class="form-group" id="redis-monitor-time">
                                <input type="radio" name="time" value="2h" class="minimal" checked>
                                2小时
                                <input type="radio" name="time" value="12h" class="minimal">
                                12小时前
                                <input type="radio" name="time" value="day" class="minimal">
                                日期
                                <select class="selectpicker" id="redis-monitor-select-time">
                                </select>
                            </div>
                    </div>
                    -->
                </div>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-6">
            <div class="box box-primary">
                <div class="box-header with-border">
                    <i class="fa fa-bar-chart-o"></i>
                    <h3 class="box-title">客户端连接数</h3>
                    <h5>单位：<i>个</i> &nbsp;&nbsp;&nbsp;&nbsp; 间隔：<i>5分钟</i></h5>
                </div>

                <div class="box-body">
                    <div style="height: 300px;width: 100%" id="interactive1"></div>
                </div>
                <!-- /.box-body-->
            </div>
        </div>
        <div class="col-xs-6">
            <div class="box box-primary">
                <div class="box-header with-border">
                    <i class="fa fa-bar-chart-o"></i>
                    <h3 class="box-title">output流量</h3>
                    <h5>单位：<i>KB</i> &nbsp;&nbsp;&nbsp;&nbsp; 间隔：<i>5分钟</i></h5>
                </div>
                <div class="box-body">
                    <div style="height: 300px;width: 100%" id="interactive2"></div>
                </div>
                <!-- /.box-body-->
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-6">
            <div class="box box-primary">
                <div class="box-header with-border">
                    <i class="fa fa-bar-chart-o"></i>
                    <h3 class="box-title">缓存命中率</h3>
                    <h5>单位：<i>%</i> &nbsp;&nbsp;&nbsp;&nbsp; 间隔：<i>5分钟</i></h5>
                </div>

                <div class="box-body">
                    <div style="height: 300px;width: 100%" id="interactive3"></div>
                </div>
                <!-- /.box-body-->
            </div>
        </div>
        <div class="col-xs-6">
            <div class="box box-primary">
                <div class="box-header with-border">
                    <i class="fa fa-bar-chart-o"></i>
                    <h3 class="box-title">内存使用率</h3>
                    <h5>单位：<i>%</i> &nbsp;&nbsp;&nbsp;&nbsp; 间隔：<i>5分钟</i></h5>
                </div>
                <div class="box-body">
                    <div style="height: 300px;width: 100%" id="interactive4"></div>
                </div>
                <!-- /.box-body-->
            </div>
        </div>
    </div>
</div>
<!-- js -->
<script src="js/redis/redis_monitor.js"></script>