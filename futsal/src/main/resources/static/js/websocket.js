function convertURL(url) {
    return contextPath ? contextPath + url : url;
}

if (document.getElementById("userId") == null) {
    let userid = null
} else {
    let userid = document.getElementById("userId")
}


let url = convertURL('/gs-guide-websocket')
let stompClient; // STOMP message 함수 이용하기 위해 socket을 할당 함
let socket;
let unreadMsgTypeHome = document.getElementById("unreadMsgTypeHome");
console.log("unreadMsgTypeHome : " + unreadMsgTypeHome)
mynickname = document.getElementById('mynickname').value

window.addEventListener('DOMContentLoaded', function () {
    socket = new SockJS(url); // 새로운 SockJS 만들 때, /gs-guide-websocket를 주소로 가지는 엔드포인트 설정
    stompClient = Stomp.over(socket);
    console.log("mynickname : " + mynickname)
    connect();
})

console.log("unreadMsgTypeHome : " + document.getElementById('unreadMsgTypeHome'))
let unreadMsgTypeManner = document.getElementById("unreadMsgTypeManner");
let unreadMsgTypeAway = document.getElementById("unreadMsgTypeAway");
let redbull = document.getElementById('redbull');
console.log("unreadMsgTypeHome : " + unreadMsgTypeHome)
console.log("redbull : " + redbull)

function connect() {
    let wholealarmcnt = document.getElementById('wholealarmcnt').value
    let readalarmcnt = document.getElementById("readalarmcnt").value
    if ((wholealarmcnt - readalarmcnt) > 0) {
        redbull.style.display = 'block'
    }
    console.log("unreadMsgTypeHome : " + unreadMsgTypeHome)
    stompClient.connect({}, function (frame) { // connect함수 실행시 /topic/greetings를 구독(subscribe) 그리고 거기서 받은 message를 JSON으로 파싱하고, 그 중 content를 보여줌
        console.log("unreadMsgTypeHome : " + unreadMsgTypeHome)
        stompClient.subscribe("/user/queue/greetings/" + mynickname, function (res) {
            console.log("content ...1 : " + JSON.parse(res.body))
            console.log("content ...1 : " + JSON.parse(res.body)[0])
            console.log("content ...1 : " + JSON.parse(res.body)[0].unreadalarmcount)
            console.log("content ...1 : " + JSON.parse(res.body)[0].message)
            document.getElementById("memberalramcount").innerText = JSON.parse(res.body)[0].unreadalarmcount;
            if (JSON.parse(res.body).unreadalarmcount > 0) {
                if (getComputedStyle(redbull).display === 'none') {
                    redbull.style.display = 'block'
                }
            }
            for (let i = 0; i < JSON.parse(res.body).length; i++) {
                let unreadMsgTypeMannerdiv =
                    '<a onclick="movetoreservation(this)" class="unreadMsgTypeManner">' +
                    '<div class="notif-img">' +
                    '<img src="https://i.ibb.co/BTp9rgD/testBall.png" alt="Img Profile"/>' +
                    '</div>' +
                    '<div class="notif-content">' +
                    '<span class="block">매너점수를 평가해 주세요</span>' +
                    '<span class="time" style="white-space: pre-wrap;">' + JSON.parse(res.body)[i].message + '</span>' +
                    '</div></a>'
                unreadMsgTypeManner.innerHTML += unreadMsgTypeMannerdiv
            }

        })
        stompClient.subscribe("/queue/away/" + mynickname, function (result1) { // 어셉트했을 때 홈&어웨이팀 팀원모두가 받는 것
            console.log("content메시지 ...1 : " + JSON.parse(result1.body).message)
            console.log("content알람카운트 ...1 : " + JSON.parse(result1.body).unreadalarmcount)
            document.getElementById("memberalramcount").innerText = JSON.parse(result1.body).unreadalarmcount;
            if (JSON.parse(result1.body).unreadalarmcount > 0) {
                if (getComputedStyle(redbull).display === 'none') {
                    redbull.style.display = 'block'
                }
            }
            let unreadMsgTypeAwaydiv =
                '<a onclick="movetoreservation(this)" class="unreadMsgTypeAway">' +
                '<div class="notif-img">' +
                '<img src="https://i.ibb.co/BTp9rgD/testBall.png" alt="Img Profile"/>' +
                '</div>' +
                '<div class="notif-content">' +
                '<span class="block">매칭이 확정되었습니다.</span>' +
                '<span class="time" style="white-space: pre-wrap;">' + JSON.parse(result1.body).message + '</span>' +
                '</div></a>'
            unreadMsgTypeAway.innerHTML += unreadMsgTypeAwaydiv
        })


        stompClient.subscribe("/queue/home/" + mynickname, function (result2) { // 도전했을 때 홈팀예약자가 받는 것
            console.log("content메시지 ...2 : " + JSON.parse(result2.body).message)
            console.log("content알람카운트 ...2 : " + JSON.parse(result2.body).unreadalarmcount)
            document.getElementById("memberalramcount").innerText = JSON.parse(result2.body).unreadalarmcount;
            if (JSON.parse(result2.body).unreadalarmcount > 0) {
                if (getComputedStyle(redbull).display === 'none') {
                    redbull.style.display = 'block'
                }
            }
            console.log("unreadMsgTypeHome : " + unreadMsgTypeHome)
            let unreadMsgTypeHomediv = '<a onclick="movetoreservation(this)" class="unreadMsgTypeHome">' +
                '<div class="notif-img">' +
                '<img src="https://i.ibb.co/BTp9rgD/testBall.png" alt="Img Profile"/>' +
                '</div>' +
                '<div class="notif-content">' +
                '<span class="block">매칭확정을 기다리고 있습니다.</span>' +
                '<span class="time" style="white-space: pre-wrap;">' + JSON.parse(result2.body).message + '</span>' +
                '</div></a>'
            unreadMsgTypeHome.innerHTML += unreadMsgTypeHomediv;
        })

        stompClient.subscribe("/queue/winorlose/" + mynickname, function (result3) {
            console.log("content메시지 ...3 : " + JSON.parse(result3.body).message)
            console.log("content알람카운트 ...3 : " + JSON.parse(result3.body).unreadalarmcount)
            document.getElementById("memberalramcount").innerText = JSON.parse(result3.body).unreadalarmcount;
            if (JSON.parse(result3.body).unreadalarmcount > 0) {
                if (getComputedStyle(redbull).display === 'none') {
                    redbull.style.display = 'block'
                }
            }
            let unreadMsgTypeWinOrLosediv = '<a onclick="movetoreservation(this)" class="unreadMsgTypeWinOrLose">' +
                '<div class="notif-img">' +
                '<img src="https://i.ibb.co/BTp9rgD/testBall.png" alt="Img Profile"/>' +
                '</div>' +
                '<div class="notif-content">' +
                '<span class="block">승부 결과를 알려주세요.</span>' +
                '<span class="time" style="white-space: pre-wrap;">' + JSON.parse(result3.body).message + '</span>' +
                '</div></a>'
            unreadMsgTypeManner.innerHTML += unreadMsgTypeWinOrLosediv
        })
    });
}

async function acceptAway(arrDatas) {
    // console.log("arrDatas : " + arrDatas)
    let matchAwayTeam = arrDatas[3].trim()
    let matchDate = arrDatas[0]
    let matchTime = arrDatas[1]
    let matchfield = arrDatas[2]
    console.log("사용할구장의상대방 : " + arrDatas[3])
    // console.log("사용할구장날짜 : " + arrDatas[0])
    // console.log("사용할구장시간 : " + arrDatas[1])
    // console.log("사용할구장명 : " + arrDatas[2])
    let url = "/alarmAcceptToAwayAjax?awayTeam=" + matchAwayTeam + "&date=" + matchDate + "&time=" + matchTime + "&field=" + matchfield

    let option = {
        method: "post"
    }
    try {
        let res = await fetch(convertURL(url), option)
        let result = await res.json()
        console.log("acceptAway 패치 후 결과 : " + result.time) //v
        stompClient.send("/app/accept", {}, JSON.stringify({
            "message": result
        }))

    } catch (err) {
        console.log("acceptAway 패치 후 오류 : " + err)
    }

}