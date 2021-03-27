$(function () {
    $("#deleteBtn1").click(setDelete1);
    $("#deleteBtn2").click(setDelete2);
});

//个人删除帖子
function setDelete1() {

    //发送AJAX请求时，将csrf令牌设置到请求的消息头中
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e,xhr,options) {
        xhr.setRequestHeader(header,token);
    })

    $.post(
        CONTEXT_PATH + "/discuss/delete1",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    );
}

//个人删除评论
function setDelete2() {

    //发送AJAX请求时，将csrf令牌设置到请求的消息头中
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e,xhr,options) {
        xhr.setRequestHeader(header,token);
    })

    $.post(
        CONTEXT_PATH + "/comment/delete",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    );
}