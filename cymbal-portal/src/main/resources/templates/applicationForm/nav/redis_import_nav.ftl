<div class="row">
    <div class="col-xs-12">
        <div class="box">
            <form class="form-horizontal" id="redisImportForm">
                <div class="box-body col-sm-9">
                    <div class="form-group">
                        <label for="envLabel" class="col-sm-2 control-label">申请人:</label>
                        <div class="col-sm-10">
                            <input type="hidden" name="userCnName" id="userCnName" />
                            <select class="form-control select2 select2-hidden-accessible" style="width: 100%;" tabindex="-1" aria-hidden="true" name="userName" id="userName">
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">环境:</label>
                        <div class="col-sm-10">
                            <select class="form-control" name="env">
                                <option value="TEST">测试环境</option>
                                <option value="STAGING">预上线环境</option>
                                <option value="PRODUCTION">生产环境</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">数据中心:</label>
                        <div class="col-sm-10">
                            <select class="form-control" name="idc">
                                <option value="TEST">test</option>
                                <option value="IDC4">idc4</option>
                                <option value="IDC5">idc5</option>
                                <option value="IDC7">idc7</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">集群模式:</label>
                        <div class="col-sm-10">
                            <input type="hidden" name="redisMode" value="CLUSTER"/>
                            <select class="form-control" name="redisVersion">
                                <option value="redis-2.8.24">单例（redis-2.8.24）</option>
                                <option value="redis-3.2.11" selected>集群（redis-3.2.11）</option>
                                <option value="redis-4.0.12">集群（redis-4.0.12）</option>
                                <option value="redis-5.0.3">集群（redis-5.0.3）</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">所属系统:</label>
                        <div class="col-sm-10">
                            <input class="form-control" name="description" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="masterInstanceNum" class="col-sm-2 control-label">主节点数:</label>
                        <div class="col-sm-10">
                            <select class="form-control" name="masterCount">
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
                        <label for="slaveInstanceNum" class="col-sm-2 control-label">备份节点数:</label>
                        <div class="col-sm-10">
                            <select class="form-control" name="replicaCount">
                                <option>0</option>
                                <option>1</option>
                                <option>2</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">每个节点缓存大小:</label>
                        <div class="col-sm-10">
                            <input id="cacheSize" type="text" name="cacheSize"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">redis访问密码:</label>
                        <div class="col-sm-10">
                            <input class="form-control" name="password">
                            <p><i>redis服务访问密码</i></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">root用户密码:</label>
                        <div class="col-sm-10">
                            <input class="form-control" name="nodePassword">
                            <p><i>linux主机root用户密码</i></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">redis节点:</label>
                        <div class="col-sm-10">
                            <textarea class="form-control" name="redisInstanceURIs" rows="3"></textarea>
                            <p><i>格式：ip:port，按行分隔</i></p>
                            <p><i>cluster模式集群输入某一节点即可，非cluster模式集群需要输入全部节点</i></p>
                        </div>
                    </div>
                    <div class="form-group hide">
                        <label class="col-sm-2 control-label">sentinel节点:</label>
                        <div class="col-sm-10">
                            <textarea class="form-control" name="sentinelInstanceURIs" rows="3"></textarea>
                            <p><i>格式：ip:port，按行分隔</i></p>
                        </div>
                    </div>
                </div>
                <div class="box-footer col-sm-9">
                    <button type="button" class="btn btn-primary pull-right col-sm-3" onclick="submitRedisImport()">
                        开始导入
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    var isAdmin = ${isAdmin?c};
    var userName = '${SPRING_SECURITY_CONTEXT.authentication.principal.username}';
</script>
<script src="/js/applicationForm/redisImport.js"></script>