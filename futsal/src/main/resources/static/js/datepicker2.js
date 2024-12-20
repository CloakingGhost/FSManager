$(document).ready(function() {
    let myteam = document.getElementById("myteam").value
    let userTeamInfo = document.getElementById("userTeamInfo").value
    let myteamlogoPath = document.getElementById("myteamlogoPath").value
    let fName = $("#fName").val();
    let time;
    let type;
    let area = document.getElementById("hometeamInfo");
    let area2 = document.getElementById("hometeamInfo2");
    const contextPath = document.getElementById('contextPathHolder').getAttribute('data-contextPath');
    let area3 = document.getElementById('weatherid')
    function getCookie(name) {
        let nameOfCookie = name + "=";
        let x = 0;
        while (x <= document.cookie.length) {
            let y = (x + nameOfCookie.length);
            if (document.cookie.substring(x, y) == nameOfCookie) {
                if ((endOfCookie = document.cookie.indexOf(";", y)) == -1)
                    endOfCookie = document.cookie.length;
                return unescape(document.cookie.substring(y, endOfCookie));
            }
            x = document.cookie.indexOf(" ", x) + 1;
            if (x == 0) {
                break;
            }
        }
        return "";
    }

    console.log("getCookie('date') : " + getCookie('date'))
    let date = getCookie('date');
    if(date.charAt(6) == "-"){// xxxx - x - xx 일때
        date = date.slice(0,5) + "0" + date.slice(5)
        console.log("바뀐date : " + date)
    }

    $("#futsalDate").datepicker({
        dateFormat: "yy-mm-dd",
        showOn: "both",
        buttonImage: "button.png",
        buttonImageOnly: true,
        changeMonth: false,
        changeYear: false,
        nextText: "다음 달",
        prevText: "이전 달",
        yearSuffix: "년",
        showMonthAfterYear: true,
        dayNames: ['월요일', '화요일', '수요일', '목요일', '금요일', '토요일', '일요일'],
        dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
        monthNamesShort: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
        monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        minDate: "0",
        maxDate: "+1M",
        showAnimation: 'slider',
        onSelect:function(d){
            console.log(d+" 선택되었습니다");
            $('#checks').text('');
            date = d

            let fieldUrl;
            if (contextPath != null){
                fieldUrl = contextPath + "/field/" + document.getElementById("fieldId").value;
            }
            else {
                fieldUrl = "/field/" + document.getElementById("fieldId").value;
            }
            if (contextPath != null){
                document.cookie = "date = " + date + '; path=' + contextPath + ';'
            }
            else {
                document.cookie = "date = " + date + '; path=/;'
            }
            $.ajax({
                type : "POST",
                url : fieldUrl,
                success : function(res){
                    // console.log("ajax 성공 res : " + res)
                    $("#timeTables").replaceWith(res.substring(res.indexOf('<div id="timeTables">'), res.indexOf('<table>')));
                    while(area.hasChildNodes()){
                        area.removeChild(area.firstChild);
                    }
                    while(area2.hasChildNodes()){
                        area2.removeChild(area2.firstChild);
                    }

                    $(".btn-gradient.green").on('click', function(e){
                        let btns = document.querySelectorAll(".btn-gradient");
                        btns.forEach(function(btn, i) {
                            if(e.currentTarget == btn) {
                                btn.classList.add("active");
                            }
                            else {
                                btn.classList.remove("active");
                            }
                        });
                        while(area.hasChildNodes()){
                            area.removeChild(area.firstChild);
                        }
                        while(area2.hasChildNodes()){
                            area2.removeChild(area2.firstChild);
                        }

                        console.log("선택 시간 : " + e.currentTarget.value);
                        time = e.currentTarget.value
                        type = "green"
                        $('#checks').text('해당 시간은 홈팀으로 예약 가능합니다.');
                        $('#checks').css('color', '#1D84F7');
                        plzsunny()
                        async function plzsunny(){
                            let datetime = time
                            console.log("time : " + time + "date : " + date + " field : " + document.getElementById("fieldId").value)
                            let plzsunnyUrl = "/field/plzsuunyAjax?time=" + datetime + "&date=" + date + "&field=" + document.getElementById("fieldId").value
                            if (contextPath != null) {
                                plzsunnyUrl = contextPath + plzsunnyUrl
                            }
                            let option = {method: "post"}
                            try{
                                let res = await fetch(plzsunnyUrl, option)
                                let weather = await res.json()
                                while(area3.hasChildNodes()){
                                    area3.removeChild(area3.firstChild);
                                }

                                let weatherdiv = "<div class='col-auto' style='margin-left: 15px; text-align: center'>" +
                                    "<article class='box weather'>" +
                                    "<div class='weatherdate'>" + weather.date + "&nbsp;" + datetime + "</div>" +
                                    "<div class='icon bubble black'>" +
                                    "<div class='spin'>" +
                                    "<img style='margin-top: -7px; margin-left: 16px;' src='" + weather.icon + "'/>" +
                                    "</div>" +
                                    "</div>" +
                                    "<h1>" + weather.main + "</h1>" +
                                    "<span class='temp'>" + weather.temp + "&deg;C" + "</span>" +
                                    "<span class='high-low'>" + "체감(" + weather.feels_like + "&deg;C) / " + "습도(" + weather.humidity + "%)" + "</span>" +
                                    "</article>" +
                                    "</div>"
                                area3.innerHTML += weatherdiv
                                area3.style.display = '';
                            }catch (err){
                                console.log("plzsunny fetch err : " + err)
                                alert("날씨정보는 현재시간부터 5일 이내에 정보만 확인 가능합니다.")
                            }
                        }
                    })

                    $(".btn-gradient.yellow").on('click', function(e){
                        let btns = document.querySelectorAll(".btn-gradient");
                        btns.forEach(function(btn, i) {
                            if(e.currentTarget == btn) {
                                btn.classList.add("active");
                            }
                            else {
                                btn.classList.remove("active");
                            }
                        });
                        while(area.hasChildNodes()){
                            area.removeChild(area.firstChild);
                        }
                        while(area2.hasChildNodes()){
                            area2.removeChild(area2.firstChild);
                        }

                        console.log("선택 시간 : " + e.currentTarget.value);
                        let date2 = getCookie('date');
                        time = e.currentTarget.value
                        async function getHomeInfo() {
                            let root;
                            if (contextPath != null){
                                root = contextPath + "/field/getHomeInfoToAjax?"
                            } else {
                                root = "/field/getHomeInfoToAjax?";
                            }
                            let targetUrl = root + "field=" + fName + "&date=" + date2 + "&time=" + time;
                            const options = {
                                method: "post"
                            }
                            try{
                                let response = await fetch(targetUrl, options);
                                let homeTeam = await response.json();
                                console.log("hometeam : " + homeTeam.logoPath)
                                console.log("hometeam : " + homeTeam.tname)

                                let manneravg;
                                if(homeTeam.tmannercnt == 0){
                                    manneravg = 0
                                }else{
                                    manneravg = (homeTeam.tmanner / homeTeam.tmannercnt).toFixed(1)
                                }
                                console.log("홈팀의 매너점수평균 : " + manneravg)

                                let victory =0;
                                let lose=0;
                                let draw=0;
                                console.log("홈팀의 레코두ㅡ : " + homeTeam.records)
                                if(homeTeam.records != null){
                                    for(let i=0;i<homeTeam.records.length;i++){
                                        let record = homeTeam.records[i]
                                        console.log("레코드 " + i + "번째결과 : " + record.result)
                                        if(record.result == "승"){
                                            victory += 1;
                                        }else if(record.result == "무"){
                                            draw += 1;
                                        }else if(record.result == "패"){
                                            lose += 1;
                                        }
                                    }
                                }

                                let hometeaminfo = `<table class="teamIn">
                                <tr>
                                    <td rowspan="7" style="width: 100px; text-align: center;"><img src="${homeTeam.logoPath}" class="profile"></td>
                                    <td style="font-size: 20px">${homeTeam.tname}</td>
                                    <td style="width: 360px;">팀 소개</td>
                                </tr>
                                <tr>
                                    <td style="font-size: 12px; color:grey;">창단일 : ${homeTeam.foundingDate}</td>
                                    <td rowspan="8"> <textarea class="teamte" readonly>${homeTeam.teamInfo}</textarea></td>
                                </tr>
                                <tr>
                                    <td style="font-size: 12px; color:grey;">통산전적 : ${victory}승 ${draw}무 ${lose}패</td>
                                </tr>
                                <tr>
                                    <td style="font-size: 12px; color: grey;">연령대 : ${homeTeam.tage}</td>
                                </tr>
                                <td style="font-size: 12px; color: grey;">판수 : ${homeTeam.ttotal}회</td>

                                <tr>
                                    <td style="font-size: 12px; color: grey;">매너점수 : ${manneravg} / 5.0</td>
                                </tr>
                                <tr>
                                    <td style="font-size: 12px; color: grey;">유니폼 칼라 : ${homeTeam.uniform}</td>
                                </tr>
                            </table>`

                                area.innerHTML += hometeaminfo

                            } catch(err) {
                                alert(err);
                            }
                        }
                        getHomeInfo();
                        type = "yellow"
                        $('#checks').text('해당 시간은 어웨이팀으로 예약 가능합니다.');
                        $('#checks').css('color', '#1D84F7');

                        plzsunny()
                        async function plzsunny(){
                            let datetime = time
                            console.log("time : " + time + "date : " + date + " field : " + document.getElementById("fieldId").value)
                            let plzsunnyUrl = "/field/plzsuunyAjax?time=" + datetime + "&date=" + date + "&field=" + document.getElementById("fieldId").value
                            if (contextPath != null) {
                                plzsunnyUrl = contextPath + plzsunnyUrl
                            }
                            let option = {method: "post"}
                            try{
                                let res = await fetch(plzsunnyUrl, option)
                                let weather = await res.json()
                                while(area3.hasChildNodes()){
                                    area3.removeChild(area3.firstChild);
                                }
                                let weatherdiv = "<div class='col-auto' style='margin-left: 15px; text-align: center'>" +
                                    "<article class='box weather'>" +
                                    "<div class='weatherdate'>" + weather.date + "&nbsp;" + datetime + "</div>" +
                                    "<div class='icon bubble black'>" +
                                    "<div class='spin'>" +
                                    "<img style='margin-top: -7px; margin-left: 16px;' src='" + weather.icon + "'/>" +
                                    "</div>" +
                                    "</div>" +
                                    "<h1>" + weather.main + "</h1>" +
                                    "<span class='temp'>" + weather.temp + "&deg;C" + "</span>" +
                                    "<span class='high-low'>" + "체감(" + weather.feels_like + "&deg;C) / " + "습도(" + weather.humidity + "%)" + "</span>" +
                                    "</article>" +
                                    "</div>"
                                area3.innerHTML += weatherdiv
                                area3.style.display = '';
                            }catch (err){
                                console.log("plzsunny fetch err : " + err)
                                alert("날씨정보는 현재시간부터 5일 이내에 정보만 확인 가능합니다.")
                            }
                        }
                    })
                    $(".btn-gradient.red").on('click', function(e){
                        $('#checks').text('해당 시간은 예약이 불가능합니다.');
                        $('#checks').css('color', '#1D84F7');
                    })
                }
            })
        }
    }).datepicker('setDate', date);

    $(".btn-gradient.green").on('click', function(e){
        let btns = document.querySelectorAll(".btn-gradient");
        btns.forEach(function(btn, i) {
            if(e.currentTarget == btn) {
                btn.classList.add("active");
            }
            else {
                btn.classList.remove("active");
            }
        });
        while(area.hasChildNodes()){
            area.removeChild(area.firstChild);
        }
        while(area2.hasChildNodes()){
            area2.removeChild(area2.firstChild);
        }
        console.log("선택 시간 : " + e.currentTarget.value);
        time = e.currentTarget.value
        type = "green"
        $('#checks').text('해당 시간은 홈팀으로 예약 가능합니다.');
        $('#checks').css('color', '#1D84F7');

        plzsunny()
        async function plzsunny(){
            let datetime = time
            console.log("time : " + time + "date : " + date + " field : " + document.getElementById("fieldId").value)
            let plzsunnyUrl = "/field/plzsuunyAjax?time=" + datetime + "&date=" + date + "&field=" + document.getElementById("fieldId").value
            if (contextPath != null) {
                plzsunnyUrl = contextPath + plzsunnyUrl
            }
            let option = {method: "post"}
            try{
                let res = await fetch(plzsunnyUrl, option)
                let weather = await res.json()
                while(area3.hasChildNodes()){
                    area3.removeChild(area3.firstChild);
                }

                let weatherdiv = "<div class='col-auto' style='margin-left: 15px; text-align: center'>" +
                    "<article class='box weather'>" +
                    "<div class='weatherdate'>" + weather.date + "&nbsp;" + datetime + "</div>" +
                    "<div class='icon bubble black'>" +
                    "<div class='spin'>" +
                    "<img style='margin-top: -7px; margin-left: 16px;' src='" + weather.icon + "'/>" +
                    "</div>" +
                    "</div>" +
                    "<h1>" + weather.main + "</h1>" +
                    "<span class='temp'>" + weather.temp + "&deg;C" + "</span>" +
                    "<span class='high-low'>" + "체감(" + weather.feels_like + "&deg;C) / " + "습도(" + weather.humidity + "%)" + "</span>" +
                    "</article>" +
                    "</div>"
                area3.innerHTML += weatherdiv
                area3.style.display = '';
            }catch (err){
                console.log("plzsunny fetch err : " + err)
                alert("날씨정보는 현재시간부터 5일 이내에 정보만 확인 가능합니다.")
            }
        }
    })

    $(".btn-gradient.yellow").on('click', function(e){
        let btns = document.querySelectorAll(".btn-gradient");
        btns.forEach(function(btn, i) {
            if(e.currentTarget == btn) {
                btn.classList.add("active");
            }
            else {
                btn.classList.remove("active");
            }
        });
        while(area.hasChildNodes()){
            area.removeChild(area.firstChild);
        }
        while(area2.hasChildNodes()){
            area2.removeChild(area2.firstChild);
        }
        console.log("선택 시간 : " + e.currentTarget.value);
        let date2 = getCookie('date');
        time = e.currentTarget.value
        async function getHomeInfo() {
            let root;
            if (contextPath != null){
                root = contextPath + "/field/getHomeInfoToAjax?"
            } else {
                root = "/field/getHomeInfoToAjax?";
            }
            let targetUrl = root + "field=" + fName + "&date=" + date2 + "&time=" + time;
            const options = {
                method: "post"
            }
            try{
                let response = await fetch(targetUrl, options);
                let homeTeam = await response.json();
                console.log("hometeam : " + homeTeam.logoPath)
                console.log("hometeam : " + homeTeam.tname)
                console.log("홈팀의 매너점수카운트 : " + homeTeam.tmannercnt)
                console.log("홈팀의 매너점수 : " + homeTeam.tmanner)

                let manneravg;
                if(homeTeam.tmannercnt == 0){
                    manneravg = 0
                }else{
                    manneravg = (homeTeam.tmanner / homeTeam.tmannercnt).toFixed(1)
                }
                console.log("홈팀의 매너점수평균 : " + manneravg)

                let victory =0;
                let lose=0;
                let draw=0;
                console.log("홈팀의 레코두ㅡ : " + homeTeam.records)
                if(homeTeam.records != null){
                    for(let i=0;i<homeTeam.records.length;i++){
                        let record = homeTeam.records[i]
                        console.log("레코드 " + i + "번째결과 : " + record.result)
                        if(record.result == "승"){
                            victory += 1;
                        }else if(record.result == "무"){
                            draw += 1;
                        }else if(record.result == "패"){
                            lose += 1;
                        }
                    }
                }

                let hometeaminfo = `<table class="teamIn">
                                <tr>
                                    <td rowspan="7" style="width: 100px; text-align: center;"><img src="${homeTeam.logoPath}" class="profile"></td>
                                    <td style="font-size: 20px">${homeTeam.tname}</td>
                                    <td style="width: 360px;">팀 소개</td>
                                </tr>
                                <tr>
                                    <td style="font-size: 12px; color:grey;">창단일 : ${homeTeam.foundingDate}</td>
                                    <td rowspan="8"> <textarea class="teamte" readonly>${homeTeam.teamInfo}</textarea></td>
                                </tr>
                                <tr>
                                    <td style="font-size: 12px; color:grey;">통산전적 : ${victory}승 ${draw}무 ${lose}패</td>
                                </tr>
                                <tr>
                                    <td style="font-size: 12px; color: grey;">연령대 : ${homeTeam.tage}</td>
                                </tr>
                                <td style="font-size: 12px; color: grey;">판수 : ${homeTeam.ttotal}회</td>

                                <tr>
                                    <td style="font-size: 12px; color: grey;">매너점수 : ${manneravg} / 5.0</td>
                                </tr>
                                <tr>
                                    <td style="font-size: 12px; color: grey;">유니폼 칼라 : ${homeTeam.uniform}</td>
                                </tr>
                            </table>`

                area.innerHTML += hometeaminfo
            } catch(err) {
                alert(err);
            }
        }
        getHomeInfo();
        type = "yellow"
        $('#checks').text('해당 시간은 어웨이팀으로 예약 가능합니다.');
        $('#checks').css('color', '#1D84F7');

        plzsunny()
        async function plzsunny(){
            let datetime = time
            console.log("time : " + time + "date : " + date + " field : " + document.getElementById("fieldId").value)
            let plzsunnyUrl = "/field/plzsuunyAjax?time=" + datetime + "&date=" + date + "&field=" + document.getElementById("fieldId").value
            if (contextPath != null) {
                plzsunnyUrl = contextPath + plzsunnyUrl
            }
            let option = {method: "post"}
            try{
                let res = await fetch(plzsunnyUrl, option)
                let weather = await res.json()
                while(area3.hasChildNodes()){
                    area3.removeChild(area3.firstChild);
                }

                let weatherdiv = "<div class='col-auto' style='margin-left: 15px; text-align: center'>" +
                    "<article class='box weather'>" +
                    "<div class='weatherdate'>" + weather.date + "&nbsp;" + datetime + "</div>" +
                    "<div class='icon bubble black'>" +
                    "<div class='spin'>" +
                    "<img style='margin-top: -7px; margin-left: 16px;' src='" + weather.icon + "'/>" +
                    "</div>" +
                    "</div>" +
                    "<h1>" + weather.main + "</h1>" +
                    "<span class='temp'>" + weather.temp + "&deg;C" + "</span>" +
                    "<span class='high-low'>" + "체감(" + weather.feels_like + "&deg;C) / " + "습도(" + weather.humidity + "%)" + "</span>" +
                    "</article>" +
                    "</div>"
                area3.innerHTML += weatherdiv
                area3.style.display = '';
            }catch (err){
                console.log("plzsunny fetch err : " + err)
                alert("날씨정보는 현재시간부터 5일 이내에 정보만 확인 가능합니다.")
            }
        }
    })
    $(".btn-gradient.red").on('click', function(e){
        $('#checks').text('해당 시간은 예약이 불가능합니다.');
        $('#checks').css('color', '#1D84F7');
    })
    $("#goReserve").on('click', function(){
        if($(".btn-gradient.yellow.active").val() == null && $(".btn-gradient.green.active").val() == null){
            alert("예약 시간을 선택해주세요")
        }
        else {
            if(contextPath != null){
                location.href = contextPath + "/reserve/" + fName + "/" + time + "/" + type;
            } else{
                location.href = "/reserve/" + fName + "/" + time + "/" + type;
            }
        }
    })

})