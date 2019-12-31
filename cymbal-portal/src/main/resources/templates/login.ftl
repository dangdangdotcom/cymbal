<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>DD Ops Platform Login</title>
    <link rel="icon" type="image/ico" href="img/favicon.ico"/>
    <link href="https://cdn.bootcss.com/twitter-bootstrap/3.4.0/css/bootstrap.min.css" rel="stylesheet">
</head>
<body style="background-color: black">
<form role="form" action="/login" method="post">
    <div class="modal-dialog" style="margin-top: 10%;">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title text-center" id="login_label">登录</h4>
            </div>
            <div class="modal-body" id="model-body">
                <div class="form-group">
                    <input type="text" class="form-control" name="username" placeholder="username" autocomplete="off">
                </div>
                <div class="form-group">
                    <input type="password" class="form-control" name="password" placeholder="password"
                           autocomplete="off">
                </div>
            </div>
            <div class="modal-footer">
                <div class="form-group">
                    <button type="submit" class="btn btn-primary form-control">登录</button>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</form>
</body>
</html>