<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">

    <!-- debug disable cache -->
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>DD Ops Platform</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.5 -->
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
    <!-- DataTables -->
    <link rel="stylesheet" href="css/bootstrap-table.min.css">
    <link rel="stylesheet" href="css/bootstrap-table-editable.css">
    <!-- Favicon style -->
    <link rel="icon" type="image/ico" href="img/favicon.ico"/>
    <!-- Font Awesome -->
    <link rel="stylesheet" href="plugins/font-awesome-4.5.0/css/font-awesome.min.css">
    <!-- Ion Slider -->
    <link rel="stylesheet" href="plugins/ionslider/ion.rangeSlider.css">
    <link rel="stylesheet" href="plugins/ionslider/normalize.css">
    <link rel="stylesheet" href="plugins/ionslider/ion.rangeSlider.skinFlat.css">
    <link rel="icon" type="image/png" href="plugins/img/sprite-skin-flat.png"/>
    <!-- Select style -->
    <link rel="stylesheet" href="plugins/bootstrap-select/css/bootstrap-select.min.css">
    <!-- iCheck for checkboxes and radio inputs -->
    <link rel="stylesheet" href="plugins/iCheck/all.css">
    <link rel="icon" type="image/png" href="plugins/images/sort_both.png"/>
    <link rel="icon" type="image/png" href="plugins/images/sort_asc.png"/>
    <link rel="stylesheet" href="plugins/datatables/jquery.dataTables.css">
    <link rel="stylesheet" href="plugins/datatables/extensions/Responsive/css/dataTables.responsive.css">
    <!-- X-editable -->
    <link rel="stylesheet" href="plugins/bootstrap-editable/css/bootstrap-editable.css">
    <!-- bootstrap-ladda -->
    <link rel="stylesheet" href="plugins/bootstrap-ladda/css/ladda-themeless.min.css">
    <!-- select2 -->
    <link rel="stylesheet" href="/plugins/select2/css/select2.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="css/main.min.css">
    <link rel="stylesheet" href="css/skins/skin-blue.css">
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <header class="main-header">
        <!-- Logo -->
        <a href="" class="logo">
            <!-- mini logo for sidebar mini 50x50 pixels -->
            <span class="logo-mini"><b>DOP</b></span>
            <!-- logo for regular state and mobile devices -->
            <span class="logo-lg"><b>DD Ops Platform</b></span>
        </a>
        <!-- Header Navbar: style can be found in header.less -->
        <nav class="navbar navbar-static-top" role="navigation">
            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <li class="dropdown user user-menu">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            <span class="hidden-xs">${userCnName}</span>
                        </a>
                    </li>
                    <li class="dropdown user user-menu">
                        <a href="logout">
                            <span class="glyphicon glyphicon-log-out"></span>
                            logout
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
    </header>
    <!-- Left side column. contains the logo and sidebar -->
    <aside class="main-sidebar">
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">
            <!-- sidebar menu: : style can be found in sidebar.less -->
            <ul class="sidebar-menu">
                <li class="header">主菜单</li>
                <#if isAdmin == true>
                    <li class="treeview">
                        <a href="#">
                            <i class="fa fa-user"></i> <span>用户管理</span>
                            <i class="fa fa-angle-left pull-right"></i>
                        </a>
                        <ul class="treeview-menu" style="display: none;">
                            <li>
                                <a href="javascript:loadPage('/users/page');">
                                    <i class="fa fa-user-plus"></i> 用户管理
                                </a>
                            </li>
                            <li>
                                <a href="javascript:loadPage('/users/roles/page');">
                                    <i class="fa fa-shield"></i> 用户权限管理
                                </a>
                            </li>
                        </ul>
                    </li>
                    <li class="treeview">
                        <a href="#">
                            <i class="fa fa-dashboard"></i> <span>主机管理</span> <i
                                    class="fa fa-angle-left pull-right"></i>
                        </a>
                        <ul class="treeview-menu">
                            <li><a href="javascript:loadPage('/nodes/page');"><i
                                            class="fa fa-circle-o"></i>主机列表</a></li>
                        </ul>
                    </li>
                    <#if defaultMonitor == false>
                        <li class="treeview">
                            <a href="#">
                                <i class="fa fa-pie-chart"></i> <span>全平台监控</span>
                                <i class="fa fa-angle-left pull-right"></i>
                            </a>
                            <ul class="treeview-menu" style="display: none;">
                                <li>
                                    <a href="javascript:loadPage('/cluster/monitors/page', {type: 'pending'}, blockUI);">
                                        <i class="fa fa-circle-o"></i>
                                        Redis
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </#if>
                </#if>
                <li class="treeview active">
                    <a href="#">
                        <i class="fa fa-laptop"></i>
                        <span>NoSQL</span>
                        <i class="fa fa-angle-left pull-right"></i>
                    </a>
                    <ul class="treeview-menu">
                        <li>
                            <a href="javascript:loadPage('/clusters/page');">
                                <i class="fa fa-circle-o"></i>Redis
                            </a>
                        </li>
                    </ul>
                </li>
                <li class="treeview">
                    <a href="#">
                        <i class="fa fa-edit"></i> <span>申请单</span>
                        <i class="fa fa-angle-left pull-right"></i>
                    </a>
                    <ul class="treeview-menu" style="display: none;">
                        <li>
                            <a href="javascript:loadPage('/application-form/page');">
                                <i class="fa fa-circle-o"></i> 我的申请单
                            </a>
                        </li>
                        <#if isAdmin == true>
                            <li>
                                <a href="javascript:loadPage('/application-form/page', {status: 'PENDING'});">
                                    <i class="fa fa-circle-o"></i>
                                    待审批的申请单
                                </a>
                            </li>
                            <li>
                                <a href="javascript:loadPage('/application-form/page', {status: 'APPROVED'});">
                                    <i class="fa fa-circle-o"></i>
                                    审批通过的申请单
                                </a>
                            </li>
                            <li>
                                <a href="javascript:loadPage('/import/application-form/page');">
                                    <i class="fa fa-circle-o"></i>
                                    导入现有集群
                                </a>
                            </li>
                        </#if>
                    </ul>
                </li>
            </ul>
        </section>
        <!-- /.sidebar -->
    </aside>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                Redis管理主界面
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i>主菜单</a></li>
                <li><a href="#">NoSQL</a></li>
                <li class="active">Redis</li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">

        </section><!-- /.content -->
    </div><!-- /.content-wrapper -->
    <footer class="main-footer">
        <div class="pull-right hidden-xs">
            <b>Version</b> 1.8.6
        </div>
        <strong>Copyright &copy; 2015-2020 <a href="http://www.dangdang.com">当当网</a>.</strong> All rights reserved.
    </footer>

    <div class="control-sidebar-bg"></div>
</div><!-- ./wrapper -->

<div class="modal fade" id="alertModal" tabindex="-1" role="dialog" aria-labelledby="alertModalLabel">
    <strong>抱歉！</strong>
    <div id="warningAlert" class="alert alert-warning" align="center">
    </div>
</div>

<!-- jQuery 2.1.4 -->
<script src="/plugins/jQuery/jQuery-2.1.4.min.js"></script>
<!-- jQuery UI 1.11.4 -->
<script src="/plugins/jQueryUI/jquery-ui.min.js"></script>
<!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
<script>
    $.widget.bridge('uibutton', $.ui.button);
</script>
<script src="/bootstrap/js/bootstrap.min.js"></script>
<script src="/js/app.min.js"></script>
<script src="/js/common.js"></script>
<script src="/plugins/jQuery-hashchange/jquery.ba-hashchange.js"></script>
<script src="/plugins/jQuery-blockUI/jquery.blockUI.js"></script>
<script src="/js/index/index.js"></script>

<script src="/plugins/datatables/jquery.dataTables.js"></script>
<script src="/plugins/datatables/extensions/Responsive/js/dataTables.responsive.js"></script>

<script src="/js/datatables/bootstrap-table.min.js"></script>
<script src="/js/datatables/bootstrap-table-flatJSON.min.js"></script>
<script src="/js/datatables/bootstrap-table-editable.js"></script>
<script src="/js/datatables/bootstrap-table-export.js"></script>
<script src="/js/datatables/bootstrap-table-zh-CN.min.js"></script>
<script src="/plugins/ionslider/ion.rangeSlider.min.js"></script>
<script src="/plugins/bootstrap-select/js/bootstrap-select.min.js"></script>
<!--
<script src="plugins/bootstrap-select/js/i18n/defaults-zh_CN.min.js"></script>
-->
<!-- FLOT CHARTS -->
<script src="/plugins/flot/jquery.flot.min.js"></script>
<!-- FLOT RESIZE PLUGIN - allows the chart to redraw when the window is resized -->
<!--<script src="plugins/flot/jquery.flot.resize.min.js"></script>-->
<!-- FLOT PIE PLUGIN - also used to draw donut charts -->
<script src="/plugins/flot/jquery.flot.pie.min.js"></script>
<!-- FLOT CATEGORIES PLUGIN - Used to draw bar charts -->
<script src="/plugins/flot/jquery.flot.categories.min.js"></script>
<script src="/plugins/flot/jquery.flot.time.min.js"></script>

<script src="/plugins/iCheck/icheck.min.js"></script>

<!-- X-editable -->
<script src="/plugins/bootstrap-editable/js/bootstrap-editable.js"></script>

<!-- bootstrap-ladda -->
<script src="/plugins/bootstrap-ladda/js/spin.min.js"></script>
<script src="/plugins/bootstrap-ladda/js/ladda.min.js"></script>

<!-- select2 -->
<script src="/plugins/select2/js/select2.full.js"></script>
<script src="/plugins/select2/js/i18n/zh-CN.js"></script>
</body>
</html>
