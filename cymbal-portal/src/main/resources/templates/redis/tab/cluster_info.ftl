<div class="table-responsive">
    <table class="table">
        <tbody>
        <tr>
            <th style="width:50%">集群 ID</th>
            <td>${cluster.clusterId}</td>
        </tr>
        <tr>
            <th>申请人:</th>
            <td>${cluster.userCnName}</td>
        </tr>
        <tr>
            <th>描述：</th>
            <td>${cluster.description}</td>
        </tr>
        <tr>
            <th>节点数：</th>
            <td>${cluster.masterCount}主${cluster.masterCount * cluster.replicaCount}从</td>
        </tr>
        <tr>
            <th>总内存：</th>
            <td>${cluster.cacheSize * cluster.masterCount}GB (每个节点${cluster.cacheSize}GB)</td>
        </tr>
        <tr>
            <th>是否集群模式：</th>
            <td>
                <#if cluster.redisMode == "CLUSTER">
                    是
                <#else>
                    否
                </#if>
            </td>
        </tr>
        <tr>
            <th>redis版本：</th>
            <td>${cluster.redisVersion}</td>
        </tr>
        <tr>
            <th>创建日期：</th>
            <td>${cluster.creationDate?datetime}</td>
        </tr>
        </tbody>
    </table>
</div>