$(function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
    $("#updateBtn").click(setUpdate);

    closeLoading();
    $(function() {
        $(".selectpicker").selectpicker({
            noneSelectedText : '请选择'    //默认显示内容
        });

        loadProvince();    //执行此函数，从后台获取数据，拼接成option标签，添加到select的里面

        //初始化刷新数据
        $(window).on('load', function() {
            $('.selectpicker').selectpicker('refresh');
        });

    });
});

//点赞
function like(btn, entityType, entityId, entityUserId, postId) {
    //发送AJAX请求之前，将CSRF的令牌设置到消息的请求头中
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        //xhr发送异步请求的核心对象
        xhr.setRequestHeader(header, token);

    });

    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId, "postId": postId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1 ? '已赞' : "赞");
            } else {
                alert(data.msg);
            }
        }
    );
}

//置顶
function setTop() {
    //发送AJAX请求之前，将CSRF的令牌设置到消息的请求头中
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        //xhr发送异步请求的核心对象
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                window.location.reload();
                $("#topBtn").attr("disabled", "disabled");
            }else{
                alert(data.msg)
            }
        }
    );
}

//加精
function setWonderful() {
    //发送AJAX请求之前，将CSRF的令牌设置到消息的请求头中
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        //xhr发送异步请求的核心对象
        xhr.setRequestHeader(header, token);

    });

    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                window.location.reload();
                $("#wonderfulBtn").attr("disabled", "disabled");
            }else{
                alert(data.msg)
            }
        }
    );
}

//管理员删除帖子
function setDelete() {
    //发送AJAX请求之前，将CSRF的令牌设置到消息的请求头中
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        //xhr发送异步请求的核心对象
        xhr.setRequestHeader(header, token);

    });

    // $.post(
    //     CONTEXT_PATH + "/discuss/delete",
    //     {"id":$("#postId").val()},
    //     function (data) {
    //         data = $.parseJSON(data);
    //         if (data.code == 0) {
    //             window.location.reload();
    //             location.href = CONTEXT_PATH + "/index";
    //         }else{
    //             alert(data.msg)
    //         }
    //     }
    // );

    $.ajax({
        url:CONTEXT_PATH + "/discuss/delete",
        async: true,
        type: 'POST',
        datatype: 'json',
        data:{id:$("#postId").val()},
        beforeSend:function (XMLRequest) {
            loading();
        },
        success:function (data) {
            closeLoading();
            data = $.parseJSON(data);
            if (data.code == 0) {
                window.location.reload();
                location.href = CONTEXT_PATH + "/index";
            }else {
                alert(data.msg);
            }
        }
    });
}

//管理员更新用户帖子板块
function setUpdate() {
    //发送AJAX请求之前，将CSRF的令牌设置到消息的请求头中
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        //xhr发送异步请求的核心对象
        xhr.setRequestHeader(header, token);
    });

    var modular = $("#province option:selected").val();
    $.post(
        CONTEXT_PATH + "/discuss/updateModular",
        {"id": $("#postId").val(),"modular":modular},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                window.location.reload();
            }else{
                alert(data.msg)
            }
        }
    );
}

//加载板块选项
function loadProvince(){
    $.ajax({
        url : CONTEXT_PATH + "/getProvince",    //后台controller中的请求路径
        type : 'GET',
        async : false,
        datatype : 'json',
        success : function(results) {
            if(results){
                var jsondata = results.data;
                var netnames =[];
                console.log(jsondata.length);
                for(var i=0,len=jsondata.length;i<len;i++){
                    var netdata  = jsondata[i];
                    console.log(netdata)
                    //拼接成多个<option><option/>
                    netnames.push('<option value="'+netdata.provinceName+'">'+ netdata.provinceName+'</option>')
                }
                $("#province").html(netnames.join(''));    //根据parkID(根据你自己的ID写)填充到select标签中
                $('#province').selectpicker('val', '');
                $('#province').selectpicker('refresh');
            }
        },
        error : function() {
            alert('查询出错');
        }
    });
}

function loading() {
    document.getElementById("loadDiv").style.visibility="visible";//显示
}

function autoCloseLoading(){
    setTimeout(function(){closeLoading()},15000);
}

function closeLoading() {
    document.getElementById("loadDiv").style.visibility="hidden";//隐藏
}