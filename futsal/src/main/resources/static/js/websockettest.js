let stompClient = null;

$(function (){
    $("form").on("submit",function (e){
        e.preventDefault();
    });
    // connect();
    $( "#connect" ).click(function() {
        connect();
    });
    $("#disconnect").click(function (){
        disconnect();
    });
    $("#send").click(function (){
        sendName();
    });
    // sendName();
});

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect(){
    let socket = new SockJS('/gs-guide-websocket'); // 새로운 SockJS 만들 때, /gs-guide-websocket를 주소로 가지는 엔드포인트 설정

    console.log("socket : " + socket)
    stompClient = Stomp.over(socket); // STOMP message 함수 이용하기 위해 socket을 할당 함
    console.log("Stomp : " + Stomp)
    console.log("stompClient : " + stompClient)
    stompClient.connect({}, function (frame){ // connect함수 실행시 /topic/greetings를 구독(subscribe) 그리고 거기서 받은 message를 JSON으로 파싱하고, 그 중 content를 보여줌
        setConnected(true);
        console.log("소켓연결성공 : " + frame);

        //구독하기
        stompClient.subscribe("/topic/greetings", function (greeting){
            showGreeting(greeting.body);
            console.log("content ... : " + greeting.body)
            // console.log("content ... 2 : " + JSON.parse(greeting.body).content)
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName(){
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
    // 클라이언트.html 폼 안에서 connect 이벤트 발동
    // connect() 실행
    // /gs-guide-websocket 엔드포인트 추가
    // /topic/greetings 구독
    // 메세지 작성 및 send 클릭
    // sendName()실행
    // 컨트롤러의 @MessageMapping의 /app/hello찾음
    // 이름 벨류를 클라이언트로 반환
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});