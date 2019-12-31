<div class="box" id="applicationFormDiv">
    <div class="box-header">
        <h3 class="box-title">Redis申请单列表</h3>
    </div>
    <#if !status??>
        <div class="toolbar" id="applyBtn">
            <a class="btn btn-app" onclick="showRedisModal({applicantEnName: '${SPRING_SECURITY_CONTEXT.authentication.principal.username}', status:'DRAFT', cacheSize:'1', redisVersion:'redis-3.2.11', redisMode:'CLUSTER', replicaCount: '1'});">
                <i class="fa fa-folder-open"></i> 申请
            </a>
        </div>
    </#if>
    <!-- Table Body -->
    <#include "../redis_list.ftl"/>
    <!-- Modal tabs -->
    <#include "../modal/redis_modal.ftl"/>
</div>

<script>
    var status = '${status!""}';
    var isAdmin = ${isAdmin?c};
</script>
<script src="/js/applicationForm/applicationForm.js"></script>