<div class="table-responsive">
    <table class="table">
        <tbody>
        <tr>
            <th style="width:50%">IP</th>
            <td>${node.ip}</td>
        </tr>
        <tr>
            <th style="width:50%">IDC</th>
            <td>${node.idc}</td>
        </tr>
        <tr>
            <th style="width:50%">HOST</th>
            <td>${node.host}</td>
        </tr>
        <tr>
            <th style="width:50%">环境类型</th>
            <td>${node.env}</td>
        </tr>
        <tr>
            <th style="width:50%">总内存</th>
            <td>${node.totalMemory}GB</td>
        </tr>
        <tr>
            <th style="width:50%">已分配内存</th>
            <td><span id="assigned_memory"></span>GB</td>
        </tr>
        <tr>
            <th style="width:50%">集群数量</th>
            <td><span id="cluster_count"></span></td>
        </tr>
        <tr>
            <th style="width:50%">实例数量</th>
            <td><span id="instance_count"></span></td>
        </tr>
        <tr>
            <th style="width:50%">redis环境状态</th>
            <td>
                <#if node.status == "UNINITIALIZED">
                    未初始化
                <#elseif node.status == "INITIALIZED">
                    已初始化
                <#else>
                    已下线
                </#if>
            </td>
        </tr>
        <tr>
            <th style="width:50%">备注</th>
            <td>
                <#if node.description??>
                    ${node.description}
                </#if>
            </td
        </tr>
        </tbody>
    </table>
</div>