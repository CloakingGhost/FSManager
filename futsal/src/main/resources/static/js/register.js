$(document).ready(function(){

	$("#kakaoLogin").on('click', function(){
		document.cookie = "platform = kakao; path=/;";
		if($("#name").val() != null){
			document.cookie = "name = " + $("#name").val();
			document.cookie = "nickName = " + $("#nickName").val();
			document.cookie = "sex = " + $("#sex").val();
			document.cookie = "phoneNo = " + $("#phoneNo").val();
		}
		location.href = "https://kauth.kakao.com/oauth/authorize?client_id=d23a3184f03474573dded85b7450aa27&redirect_uri=http://localhost:8080/loginAccess&response_type=code&prompt=login";
	})
	
	$("#naverLogin").on('click', function(){
		document.cookie = "platform = naver; path=/;";
		if($("#name").val() != null){
			document.cookie = "name = " + $("#name").val();
			document.cookie = "nickName = " + $("#nickName").val();
			document.cookie = "sex = " + $("#sex").val();
			document.cookie = "phoneNo = " + $("#phoneNo").val();
		}
		location.href = $("#naverURL").val();
	})
	
	$("#googleLogin").on('click', function(){
		document.cookie = "platform = google; path=/;";
		if($("#name").val() != null){
			document.cookie = "name = " + $("#name").val();
			document.cookie = "nickName = " + $("#nickName").val();
			document.cookie = "sex = " + $("#sex").val();
			document.cookie = "phoneNo = " + $("#phoneNo").val();
		}
		location.href = "https://accounts.google.com/o/oauth2/v2/auth?client_id=407684725072-2ikndkuqcafeku5ufb4dvnm940t1d1v1.apps.googleusercontent.com&redirect_uri=http://localhost:8080/loginAccess&response_type=code&scope=email%20profile%20openid&access_type=offline&prompt=login"
	})
})