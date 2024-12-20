// let userid = document.getElementById("userId")
let fName = $("#fName").text()
let fPrice = $("#fPrice").text()
let fDate = $("#fDate").text()
let fTime = $("#fTime").text()
let fPhoneNo = $("#fPhoneNo").text()
let rState = "A"
let nickName1 = $("#nickName1").text()
let tName1 = $("#tName1").text()
let rDate1 = new Date(+new Date() + 3240 * 10000).toISOString().replace('T', ' ').replace(/\..*/, '');
let nickName2 = "null"
let tName2 = "null"
let rDate2 = "null"
let balanceA = $("#balanceA").val()
let balanceB = $("#balanceB").val()
let typeValue = $("#typeValue").val()

if ((typeValue == 'green' && balanceA >= 0) || (typeValue == 'yellow' && balanceB >= 0)) {

    $("#tryReserve").on("click", function () {

        if ($("#acc").is(':checked') && $("#acc2").is(':checked')) {
            let type = $('input[name=reserveType]:checked').val();
            let targetUrl = convertURL("/reserveTo/checkAjax");
            let targetUrl2 = convertURL("/reserveTo/insert");
            let targetUrl3 = convertURL("/reserveTo/checkAjax2");
            let targetUrl4 = convertURL("/reserveTo/saveAway");
            let targetUrl5 = convertURL("/reserveListTo/save");


            $.ajax({
                type: "POST",
                url: targetUrl,
                data: {field: fName, fTime: fTime, fDate: fDate},
                dataType: "text",
                success: function (result) {
                    console.log("예약 정보 : " + result)
                    if (result == "null") {
                        if (type == "all") {
                            nickName2 = $("#nickName1").text()
                            tName2 = $("#tName1").text()
                            rDate2 = rDate1
                        }
                        $.ajax({
                            type: "POST",
                            url: targetUrl2,
                            data: {
                                fName: fName, fPrice: fPrice, fDate: fDate, fTime: fTime, fPhoneNo: fPhoneNo,
                                type: type, rState: rState, nickName1: nickName1, tName1: tName1, rDate1: rDate1,
                                nickName2: nickName2, tName2: tName2, rDate2: rDate2
                            },
                            success: function () {
                                alert("예약이 완료되었습니다.")
                                location = convertURL("/field/click?fName=" + fName);
                            }
                        })
                    }
                    if (result == "A") {
                        // save로 보내야함
                        nickName2 = $("#nickName2").text()
                        tName2 = $("#tName2").text()
                        rDate2 = rDate1
                        // A인애가 B로 등록할경우
                        $.ajax({
                            type: "POST",
                            url: targetUrl3,
                            data: {field: fName, fTime: fTime, fDate: fDate},
                            dataType: "text",
                            success: function (result) {
                                if (result == "true") {
                                    if (confirm("이미 홈팀으로 등록되셨습니다. 홈 & 어웨이로 등록하시겠습니까?")) {
                                        type = "all"
                                        $.ajax({
                                            type: "POST",
                                            url: targetUrl4,
                                            data: {
                                                field: fName,
                                                fTime: fTime,
                                                fDate: fDate,
                                                nickName2: nickName2,
                                                tName2: tName2,
                                                rDate2: rDate1
                                            },
                                            success: function (result) {
                                                alert("예약이 완료되었습니다.")
                                                location = convertURL("/field/click?fName=" + fName);
                                            }
                                        })
                                    }
                                } else {
                                    $.ajax({
                                        type: "POST",
                                        url: targetUrl5,
                                        data: {
                                            field: fName,
                                            fTime: fTime,
                                            fDate: fDate,
                                            nickName2: nickName2,
                                            tName2: tName2,
                                            rDate2: rDate2
                                        },
                                        success: function (result) {
                                            console.log("result : " + result)
                                            console.log("resulttype : " + typeof (result))
                                            if (result === 1) {
                                                if (typeValue == 'yellow') {
                                                    let message = {
                                                        "field": fName,
                                                        "date": fDate,
                                                        "time": fTime,
                                                        "hometeam": document.getElementById("tName3").innerHTML //홈팀
                                                    }
                                                    // console.log("보낼값 확인 : " + message.fName + "/ " + message.fDate + "/ " + message.fTime + "/ " + message.hometeam)
                                                    stompClient.send("/app/sendtohometeam", {}, JSON.stringify({
                                                        "message": message
                                                    }));
                                                }
                                                alert("예약이 완료되었습니다.");
                                                location = convertURL("/field/click?fName=" + fName)
                                            } else if (result === -1) {
                                                alert(nickName2 + "님의 팀 : " + tName2 + "은\n " + document.getElementById("tName3").innerHTML + "의 매칭수락을 기다리고 있습니다.");
                                            }
                                        }
                                    })
                                }
                            }
                        })
                    }
                },
                error: function (result) {
                    console.log("reserve Ajax Error : " + result)
                }
            })
        } else {
            alert("이용 약관에 동의해주세요.")
        }
    });
}
if ((typeValue == 'green' && balanceA < 0) || (typeValue == 'yellow' && balanceB < 0)) {
    document.getElementById("tryReserve").className = "btn-gradient gray"
    document.getElementById("tryReserve").innerText = "예약 진행 불가"
    document.getElementById("tryReserve").style.cursor = "no-drop"
}

let reserveType = document.querySelectorAll("input[name='reserveType']")
let reserveType_subtitle = document.getElementsByClassName('reserveType_subtitle')[0]
reserveType[0].addEventListener('click', function () {
    if (reserveType[0].checked) reserveType_subtitle.style.display = 'inline'
})

reserveType[1].addEventListener('click', function () {
    if (reserveType[1].checked) reserveType_subtitle.style.display = 'none'
})
