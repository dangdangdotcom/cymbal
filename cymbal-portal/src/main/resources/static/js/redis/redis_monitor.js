$(function () {
    var selectTimeLength = 10;
    //数据初始化
    var now = new Date().getTime();
    //初始化日期选择的数据为最近10天
    for (var i = 0; i < selectTimeLength; i++) {
        var tmp = now - 24 * 60 * 60 * 1000 * i;
        var date = new Date(tmp);
        var timeStr = date.format("yyyy-MM-dd");
        $("#redis-monitor-select-time").append('<option value="' + timeStr + '">' + timeStr + '</option>');
    }

    //配置chart
    //定时刷新的时间
    var updateInterval = 2000;
    //初始化数据容器
    var realMointerData;
    //是否是第一次显示
    var firstOpen = true;
    //定时器的句柄
    var intervalHandle;
    //图表数组
    var charts = [];
    var chartsConfig = [
        {
            selector: "#interactive1",//选择器
            propertyName: "connectedClients",//属性名称
            yMin: 0,//y的最小值
            yMax: 5000,//y的最大值
        },
        {
            selector: "#interactive2",//选择器
            propertyName: "instantaneousOutputKbps",//属性名称
            yMin: 0,//y的最小值
            yMax: 5000,//y的最大值
        },
        {
            selector: "#interactive3",//选择器
            propertyName: "keyspaceHitPercent",//属性名称
            yMin: 0,//y的最小值
            yMax: 100,//y的最大值
        }, {
            selector: "#interactive4",//选择器
            propertyName: "usedMemoryPercent",//属性名称
            yMin: 0,//y的最小值
            // yMax: 100,//y的最大值
        }
    ];
    //监听监控告警的打开
    $('a[href="#redis-monitor"]').on('shown.bs.tab', function (e) {
        if (firstOpen) {
            initRedisList();
            //初始化图表
            refreshChart();
            firstOpen = false;
        }
        //设置定时刷新
        intervalHandle = setInterval(update, updateInterval, true);
    });
    //监听监控告警的关闭
    $('a[href="#redis-monitor"]').on('hidden.bs.tab', function (e) {
        clearInterval(intervalHandle);
    });

    //绑定redis选择被选择的事件
    $("#redis-monitor-server-radio").delegate('input[type="radio"].minimal', "ifChecked", function (event) {
        refreshChart();
        intervalHandle = setInterval(update, updateInterval, true);
    });

    //绑定时间选择被选择的事件
    $("#redis-monitor-time").delegate('input[type="radio"].minimal', "ifChecked", function (event) {
        refreshChart();
        intervalHandle = setInterval(update, updateInterval, true);
    });

    //绑定日期选择的事件 Bootstrap-select 没有原生提供 只能通过这个代理
    $("body").delegate("button[data-id='redis-monitor-select-time'] + .dropdown-menu", "click", function () {
        refreshChart();
        intervalHandle = setInterval(update, updateInterval, true);
    });

    function refreshChart() {
        //时间段
        var timeRange = $("#redis-monitor-time input[type='radio']:checked").val()
        //redis
        var redisId = $("#redis-monitor-server-radio input[type='radio']:checked").val()
        //日期
        var day = $("#redis-monitor-select-time").selectpicker("val");
        clearInterval(intervalHandle);
        realMointerData = RedisMointorData({
            timeRange: timeRange,
            redisId: redisId,
            day: day
        });
        initCharts();
    }

    function initCharts() {
        $(chartsConfig).each(function (i, option) {
            option.redisMointorData = realMointerData;
            var chart = RedisMointorChart(option);
            chart.initChart();
            charts.push(chart);
        });
    }

    function update() {
        $.ajax({
            type: "GET",
            url: "/instances/" + realMointerData.redisId + "/monitors",
            contentType: "application/json",
            success: function (redisMonitorInfo) {
                realMointerData.addData(redisMonitorInfo);
                updateGrid();
            }
        });
    }

    function updateGrid() {
        //遍历得到数据
        for (var i = 0; i < charts.length; i++) {
            charts[i].updateChart();
        }
    }
});

function initRedisList() {
    var data = $('#redisServerTable').bootstrapTable("getData");
    $("#redis-monitor-server-radio").empty();

    var radioCount = 0;
    for (var i = 0; i < data.length; i++) {
        if (data[i]["redisVersion"].indexOf("sentinel") != -1) {
            continue;
        }

        radioCount++;
        var ip = data[i]["ip"] + ":" + data[i]["port"];
        var id = data[i]["id"];
        //默认选择第一个
        if (i == 0) {
            $("#redis-monitor-server-radio").append(' <input type="radio" name="redis" checked value="' + id + '" class="minimal" /> ' + ip + '');
        } else {
            $("#redis-monitor-server-radio").append(' <input type="radio" name="redis" value="' + id + '" class="minimal" /> ' + ip + '');
            if (radioCount % 5 == 0) {
                $("#redis-monitor-server-radio").append('<br />');
            }
        }
    }
    $('input[type="radio"].minimal').iCheck({
        checkboxClass: 'icheckbox_minimal-blue',
        radioClass: 'iradio_minimal-blue'
    });
}

//存储图表数据的容器
function RedisMointorData(option) {
    var defaultOption = {
        timePropertyName: "scrapeTime",//时间的属性名称
        maxLength: 100,//数据存储的最大数量
        isfill: true,//是否数据填充
        updateInterval: 2 * 1000,//填充数据的时间间隔 为毫秒
        timeRange: undefined,
        redisId: undefined,
        day: undefined,
    };
    option = $.extend(defaultOption, option);
    //存储数据
    var data = [];
    if (option.isfill) {
        //按照时间间隔填充数据
        var time = new Date().getTime() - option.maxLength * option.updateInterval;
        for (var i = 0; i < option.maxLength; i++) {
            var tmp = {};
            tmp[option.timePropertyName] = time + i * option.updateInterval;
            data.push(tmp);
        }
    }
    //添加数据
    var addData = function (json) {
        //判断数据是否是数组
        if (json instanceof Array) {
            for (var i = 0; i < json.length; i++) {
                data.push(json[i]);
            }
        } else {
            data.push(json);
        }
        data.push(json);
        //截图后maxLength数据
        if (data.length > option.maxLength) {
            data = data.slice(-option.maxLength);
        }
    };
    //通过属性名称得到数据
    //propertyName 属性名称
    // defaultValue如果为空的默认值
    var getPropertyData = function (propertyName, defaultValue) {
        var result = [];
        //遍历得到数据
        for (var i = 0; i < data.length; i++) {
            var val = data[i][propertyName];
            val = typeof(val) == "undefined" ? defaultValue : val;
            result.push([data[i][option.timePropertyName], val]);
        }
        return result;
    };
    return {
        //添加数据 可以是对象或者数组
        addData: addData,
        //通过属性名称的返回图标的数据
        getPropertyData: getPropertyData,
        timeRange: option.timeRange,
        redisId: option.redisId,
        day: option.day,
    }
}

//图表的模型
function RedisMointorChart(option) {
    var plot;
    var defaultOption = {
        selector: "#id",//选择器
        propertyName: "propertyName",//属性名称
        redisMointorData: {},//redisMointorData对象
        lineColor: "#3c8dbc",//折线颜色
        tickSize: [60, "second"],//x轴标识的时间间隔
        defaultValue: undefined,//默认值
        tickFormatter: function (v, axis) { //配置x轴的显示
            var date = new Date(v);
            //这里只显示整分钟的数据
            if (date.getSeconds() % 60 == 0) {
                return date.format("HH:mm:ss");
            } else {
                return "";
            }
        },
    };
    option = $.extend(defaultOption, option);

    function getData() {
        //通过属性名字得到绘图数据
        return option.redisMointorData.getPropertyData(option.propertyName, option.defaultValue);
    }

    //初始化图表的方法
    function initChart() {
        var config = {
            grid: {
                //边框颜色
                borderColor: "#f3f3f3",
                //边框宽度
                borderWidth: 1,
                //标识颜色
                tickColor: "#f3f3f3"
            },
            series: {
                //阴影大小
                shadowSize: 0, // Drawing is faster without shadows
                //颜色
                color: "#3c8dbc"
            },
            //折线配置
            lines: {
                //是否填充
                /*fill: true, //Converts the line chart to area chart*/
                //折线颜色
                color: option.lineColor,
            },
            //y轴配置
            yaxis: {
                min: option.yMin, //y轴的最大值
                max: option.yMax,//y轴的最小值
                show: true,//是否显示
            },
            //x轴配置
            xaxis: {
                mode: "time",//展示模式为时间模式
                tickSize: option.tickSize,//x 轴的时间间隔
                tickFormatter: option.tickFormatter,//配置格式化方法
                axisLabel: "Time",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 10,
            },
        }
        var data = getData();
        //根据selector初始化图表
        plot = $.plot(option.selector, [data], config);
    }

    function updateChart() {
        var data = getData();
        //设置数据
        plot.setData([data]);
        //更新x轴
        plot.setupGrid();
        //重新绘画图表
        plot.draw();
    }

    return {
        initChart: initChart,//初始化方法
        updateChart: updateChart,//更新图表的方法
    }
}