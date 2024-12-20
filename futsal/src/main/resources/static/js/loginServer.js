$("#kakaoLogin").on('click', function () {
    document.cookie = "platform = kakao; path=/;";
    location.href = "https://kauth.kakao.com/oauth/authorize?client_id=d23a3184f03474573dded85b7450aa27&redirect_uri=https://www.fsmanager.run" + contextPath + "/loginAccess&response_type=code&prompt=login";
})

$("#naverLogin").on('click', function () {
    document.cookie = "platform = naver; path=/;";
    location.href = $("#naverURL").val();
})

$("#googleLogin").on('click', function () {
    document.cookie = "platform = google; path=/;";
    location.href = "https://accounts.google.com/o/oauth2/v2/auth?client_id=407684725072-2ikndkuqcafeku5ufb4dvnm940t1d1v1.apps.googleusercontent.com&redirect_uri=https://www.fsmanager.run" + contextPath + "/loginAccess&response_type=code&scope=email%20profile%20openid&access_type=offline&prompt=login"
})

$('#signIn').on('click', function () {
    $('.modal').fadeIn()
})
$('#backBtn').on('click', function () {
    $('.modal').fadeOut()
})