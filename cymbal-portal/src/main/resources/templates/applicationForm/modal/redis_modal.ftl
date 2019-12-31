<div class="modal fade" id="redisModal" role="dialog" aria-labelledby="serverModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    申请Redis集群
                </h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="box-body">
                        <div class="form-group" style="display:none" >
                            <div class="col-sm-8">
                                <input type="hidden" id="applicationFormId"></input>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-3 control-label">申请人:</label>
                            <div class="col-sm-8">
                                <input type="hidden" id="applicantCnName"></input>
                                <select class="form-control select2 select2-hidden-accessible" style="width: 100%;" tabindex="-1" aria-hidden="true" name="applicantEnName" id="applicantEnName">
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-3 control-label">环境:</label>
                            <div class="col-sm-8">
                                <select class="selectpicker" id="env">
                                    <option value="TEST">测试环境</option>
                                    <option value="STAGING">预上线环境</option>
                                    <option value="PRODUCTION">生产环境</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-3 control-label">数据中心:</label>
                            <div class="col-sm-8">
                                <select class="selectpicker" id="idc">
                                    <option value="TEST">test</option>
                                    <option value="IDC4">idc4</option>
                                    <option value="IDC5">idc5</option>
                                    <option value="IDC7">idc7</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="redisMode" class="col-sm-3 control-label">集群模式:</label>
                            <div class="col-sm-8">
                                <select class="selectpicker" id="redisMode">
                                    <option value="STANDALONE">standalone</option>
                                    <option value="CLUSTER">cluster</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="redisVersion" class="col-sm-3 control-label">redis版本:</label>
                            <div class="col-sm-8">
                                <select class="selectpicker" id="redisVersion">
                                    <option value="redis-2.8.24">redis-2.8.24</option>
                                    <option value="redis-3.2.11">redis-3.2.11</option>
                                    <option value="redis-4.0.12">redis-4.0.12</option>
                                    <option value="redis-5.0.3">redis-5.0.3</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="masterInstanceNum" class="col-sm-3 control-label">主节点数:</label>
                            <div class="col-sm-8">
                                <select class="selectpicker" id="masterCount">
                                    <option>1</option>
                                    <option>2</option>
                                    <option>3</option>
                                    <option>4</option>
                                    <option>5</option>
                                    <option>6</option>
                                    <option>7</option>
                                    <option>8</option>
                                    <option>9</option>
                                    <option>10</option>
                                    <option>11</option>
                                    <option>12</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group" id="slaveNodes">
                            <label for="slaveInstanceNum" class="col-sm-3 control-label">备份节点数:</label>
                            <div class="col-sm-8">
                                <select class="selectpicker" id="replicaCount">
                                    <option>0</option>
                                    <option selected>1</option>
                                    <option>2</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="memory" class="col-sm-3 control-label">缓存大小:</label>
                            <div class="col-sm-8">
                                <input id="cacheSize" type="text" name="cacheSize" >
                            </div>
                        </div>
                        <div class="form-group" id="slaveNodes">
                            <label for="redisPersistenceType" class="col-sm-3 control-label">持久化方式:</label>
                            <div class="col-sm-8">
                                <select class="selectpicker" id="redisPersistenceType">
                                    <option value="NO">不持久化</option>
                                    <option value="RDB">RDB</option>
                                    <option value="AOF">AOF</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group" id="enableSentinelDiv">
                            <label for="memory" class="col-sm-3 control-label">Sentinel:</label>
                            <div class="col-sm-8">
                                <label>
                                  <input type="checkbox" id="enableSentinel">&nbsp;&nbsp;启用Sentinel监控
                                </label>
                            </div>
                        </div>
                        <div class="form-group" id="passwordDiv">
                            <label for="description" class="col-sm-3 control-label">密码:</label>
                            <div class="col-sm-8">
                                <input class="form-control" style="width:60%" id="password" />
                                <p><i>redis访问密码，如果设置建议至少16位以上。</i></p>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="envLabel" class="col-sm-3 control-label">所属系统:</label>
                            <div class="col-sm-8">
                                <input class="form-control" style="width:60%" id="belongSystem" />
                            </div>
                        </div>
                        <div class="form-group" id="descriptionDiv">
                            <label for="description" class="col-sm-3 control-label">集群描述:</label>
                            <div class="col-sm-8">
                                <textarea class="form-control" id="description"></textarea>
                                <p><i>注明集群主要用途，容量评估依据，redis核心配置参数(例如rdb快照保存频率，缓存淘汰策略)等。</i></p>
                            </div>
                        </div>
                        <div class="form-group" style="display:none" id="approvalOpinionDiv">
                            <label for="description" class="col-sm-3 control-label">审核意见:</label>
                            <div class="col-sm-8">
                                <textarea class="form-control" id="approvalOpinion"></textarea>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onclick="submitRedisApply(false)" id="redisSaveBtn">保存草稿</button>
                <button type="button" class="btn btn-primary" onclick="submitRedisApply(true)" id="redisSubmitBtn">提交审核</button>
                <button type="button" class="btn btn-primary" onclick="denyRedisApply()" id="redisDenyBtn" style="display:none">驳回</button>
                <button type="button" class="btn btn-primary" onclick="approveAndAllocRedisApply()" id="redisApprovalAndAllocBtn" style="display:none">审核分配</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->