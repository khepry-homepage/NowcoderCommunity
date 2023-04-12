$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	let url = `${CONTEXT_PATH}/letter/send`;
	let username = $("#recipient-name").val();
	let content = $("#message-text").val();
	$.ajax({
		url,
		type: 'POST',
		dataType: 'json',
		data: {
			username,
			content
		},
		xhrFields: {
			withCredentials: true
		},
		timeout: 5000,
		success: function (res) {
			if (res?.code != 200) {
				alert(res.msg);
				return;
			}
			$("#sendModal").modal("hide");
			$("#hintModal").modal("show");
			setTimeout(function(){
				window.location.reload();
				$("#hintModal").modal("hide");
			}, 2000);
		},
		error: function (err) {
			console.log(err);
			alert("发布失败, 服务器异常！", err);
		}
	})
}


function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}