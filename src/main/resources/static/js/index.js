$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	let url = `${CONTEXT_PATH}/discuss/publish`;
	let title = $("#recipient-name").val();
	let content = $("#message-text").val();
	$.ajax({
		url,
		type: 'POST',
		dataType: 'json',
		data: {
			title,
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
			$("#publishModal").modal("hide");
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
			}, 2000);
		},
		error: function (err) {
			console.log(err);
			alert("发布失败, 服务器异常！", err);
		}
	})

}