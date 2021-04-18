$(function(){
	$("#publishBtn").click(publish);
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

function publish() {
	$("#publishModal").modal("hide");

	//发送AJAX请求时，将csrf令牌设置到请求的消息头中
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function (e,xhr,options) {
		xhr.setRequestHeader(header,token);
	})

	//获取标题和内容以及模块选中
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	var modular = $("#modular-name option:selected").val();
	//发送异步请求（post）
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content,"modular":modular},
		function (data){
			data = $.parseJSON(data);
			//在提示框中显示返回的消息
			$("#hintBody").text(data.msg);
			//两秒后隐藏
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//发布成功code=0，刷新页面
				if(data.code==0){
					window.location.reload();
				}
			}, 2000);
		}
	);
}

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
					netnames.push('<option value="'+netdata.id+'">'+ netdata.provinceName+'</option>')
				}
				$("#province").html(netnames.join(''));    //根据parkID(根据你自己的ID写)填充到select标签中
			}
		},
		error : function() {
			alert('查询出错');
		}
	});
}