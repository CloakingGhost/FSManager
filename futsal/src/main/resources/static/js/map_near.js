function convertURL(url) {
    return contextPath ? contextPath + url : url
}

let map;
let fNList = document.getElementById('fNList');
let latList = document.getElementById('latList');
let lonList = document.getElementById('lonList')
console.log("fNlist : " + fNList[0])
window.addEventListener('DOMContentLoaded', function () {
    let markers = [];
    let fNames = [];
    let customOverlays = [];
    let lat, lon;
    console.log("fNlist : " + fNList[0])


    if (fNList && latList && lonList) {
        fNList = fNList.value.split(",")
        latList = latList.value.split(",")
        lonList = lonList.value.split(",")
    }

    let mapContainer = document.getElementById('map_n'), // 지도를 표시할 div
        mapOption = {
            center: new kakao.maps.LatLng(37.4946287, 127.0276197), // 지도의 중심좌표
            level: 4 // 지도의 확대 레벨
        };
    map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다

    relayout();

    function relayout() {
        console.log("혼자야 ?")
        // 지도를 표시하는 div 크기를 변경한 이후 지도가 정상적으로 표출되지 않을 수도 있습니다
        // 크기를 변경한 이후에는 반드시  map.relayout 함수를 호출해야 합니다
        // window의 resize 이벤트에 의한 크기변경은 map.relayout 함수가 자동으로 호출됩니다
        map.relayout();
        console.log("메인에서 relayout함수 발동")
        console.log("메인에서 센터변경전 : " + map.getCenter())
        map.setCenter(new kakao.maps.LatLng(37.4946287, 127.0276197));
        console.log("메인에서 센터변경후 : " + map.getCenter())
    }

    const center = map.getCenter();
    console.log(center, "처음 lat lng")

// 일반 지도와 스카이뷰로 지도 타입을 전환할 수 있는 지도타입 컨트롤을 생성합니다
    let mapTypeControl = new kakao.maps.MapTypeControl();

// 지도에 컨트롤을 추가해야 지도위에 표시됩니다
//kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 TOPRIGHT는 오른쪽 위를 의미합니다
    map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);

// 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 생성합니다
    let zoomControl = new kakao.maps.ZoomControl();
    map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

// HTML5의 geolocation으로 사용할 수 있는지 확인합니다
    if (navigator.geolocation) {
        // GeoLocation을 이용해서 접속 위치를 얻어옵니다
        navigator.geolocation.getCurrentPosition(function (position) {

            lat = position.coords.latitude, // 위도
                lon = position.coords.longitude; // 경도

            let locPosition = new kakao.maps.LatLng(lat, lon);

            let check = document.getElementById("searchedfield");
            console.log("확인작업 : " + check + " 체크의 타입 : " + typeof (check))
            if (check && check.value === "false") {
                kakaoSort(lat, lon);
            }
            // 마커와 인포윈도우를 표시합니다
            displayMarker(locPosition);

        });

    } else { // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정합니다
        let locPosition = new kakao.maps.LatLng(37.4946287, 127.0276197);
        displayMarker(locPosition);
    }

// 지도에 마커와 인포윈도우를 표시하는 함수입니다
    function displayMarker(locPosition) {
        let imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";
        let imageSrc2 = "https://i.ibb.co/ky1DywQ/Kakao-Talk-20221019-214448571.png"
        let imageSize = new kakao.maps.Size(24, 35);
        let imageSize2 = new kakao.maps.Size(44, 44);
        let markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize)
        let markerImage2 = new kakao.maps.MarkerImage(imageSrc2, imageSize2)
        let marker = new kakao.maps.Marker({
            map: map,
            position: locPosition,
            image: markerImage
        });

        for (let i = 0; i < lonList.length; i++) {
            let fName = fNList[i].replace("[", "")
            fName = fName.replace("]", "")
            fName = fName.trim();
            let lat2 = latList[i].replace("[", "");
            lat2 = lat2.replace("]", "")
            lat2 = parseFloat(lat2)// 위도
            let lon2 = lonList[i].replace("[", "");
            lon2 = lon2.replace("]", "")
            lon2 = parseFloat(lon2)// 경도
            let locPosition2 = new kakao.maps.LatLng(lat2, lon2);

            let marker2 = new kakao.maps.Marker({
                map: map,
                position: locPosition2,
                image: markerImage2,
                text: fName
            })
            markers[i] = marker2.getPosition().getLat().toString() + ", " + marker2.getPosition().getLng().toString()
            // console.log("markers[0] : " + markers[0])
            fNames[i] = fName

            kakao.maps.event.addListener(marker2, 'mouseover', showInfo(marker2, map));
            kakao.maps.event.addListener(map, 'zoom_changed', function () {
                for (let j = 0; j < customOverlays.length; j++) {
                    customOverlays.pop().setMap(null)
                }
            });
        }

        map.setCenter(locPosition);
    }

    function showInfo(marker, map) {
        return function () {
            let str = marker.getPosition().getLat().toString() + ", " + marker.getPosition().getLng().toString()
            let content = '<div class="overlaybox"><ul>'
            for (let k = 0; k < markers.length; k++) {
                if (markers[k] == str) {
                    if (content == "") {
                        if (contextPath != null) {
                            content = '<li class="up"><a href="' + contextPath + '/field/click?fName=' + fNames[k] + '"><span class="title">' + fNames[k] + '</span></a></li>'
                        } else {
                            content = '<li class="up"><a href="/field/click?fName=' + fNames[k] + '"><span class="title">' + fNames[k] + '</span></a></li>'
                        }
                    } else {
                        if (contextPath != null) {
                            content = content + '<li class="up"><a href="' + contextPath + '/field/click?fName=' + fNames[k] + '"><span class="title">' + fNames[k] + '</span></a></li>'
                        } else {
                            content = content + '<li class="up"><a href="/field/click?fName=' + fNames[k] + '"><span class="title">' + fNames[k] + '</span></a></li>'
                        }
                    }
                }
            }
            content = content + "</ul></div>"
            let locPosition3 = new kakao.maps.LatLng(marker.getPosition().getLat(), marker.getPosition().getLng())
            let customOverlay = new kakao.maps.CustomOverlay({
                position: locPosition3,
                content: content,
                zIndex: 1,
                clickable: false
            });
            if (map.getLevel() < 9) {
                customOverlay.setMap(map, marker);
            }
            if (customOverlays.length < 1) {
                customOverlays.push(customOverlay)
            } else {
                for (let i = 0; i < customOverlays.length; i++) {
                    customOverlays.pop().setMap(null)
                }
                customOverlays.push(customOverlay)
            }
            console.log(customOverlays.length)
            return customOverlays
        }
    }
})


async function kakaoSort(lat, lon) {
    console.log("나의 현재위치기반으로 구장들을 정렬 함")
    let heart1 = convertURL("/img/icons/heart1.png");
    let heart2 = convertURL("/img/icons/heart2.png");

    let url = "/mainSortLatitudeLongitudeDistanceNearBidongki?lat=" + lat + "&lon=" + lon
    let option = {
        method: "post"
    }
    try {
        let res = await fetch(convertURL(url), option);
        let result = await res.json();
        // console.log("mainSortLatitudeLongitudeDistanceNearBidongki 패치 후 결과 : " + result[0]) //v
        let area = document.getElementById("findFields")
        while (area.hasChildNodes()) {
            area.removeChild(area.firstChild)
        }
        console.log("맵니어js && 위치기반으로 붙이기패치성공")
        for (let i = 0; i < result.length; i++) {
            let div1 = "<div class='col-md-3 mt-3 p-3' id='findFieldsStart'>" +
                "<div class='recent-game-item'>" +
                "<div class='rgi-thumb' style='background-image: url(" + result[i].fpic1 + "); background-size: cover;'></div>" +
                "<div class='rgi-content'>" +
                "<h5>" +
                "<a class='likeFName' style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\" href='" + convertURL("/field/click?fName=") + result[i].fname + "'>" +
                "<span>" + result[i].fname + "</span>" +
                "</a>" +
                "</h5>" +
                "<p>" + '주소' + " : " + "<span>" + result[i].faddress + "</span>" + "</p>" +
                "<div class='rgi-extra'>" +
                "<div class='rgi-star' style='background-color: white;'>" +
                "<input type='hidden' value='" + result[i].fname + "' name='fNames'>"
            let reviewcount = "";
            if (!result[i].reviews) {
                reviewcount =
                    "<span style='margin-left: 6px;'>" + 0 + "</span>"
            } else {
                reviewcount =
                    "<span style='margin-left: 6px;'>" + result[i].reviews.length + "</span>"
            }
            let div2 =
                "<img type='button' id='fieldreview' class='fieldreivew' src='https://i.ibb.co/w7jZ3kw/image.png' style='cursor:pointer;'>" +
                "<div class='reviewmodal' + style='position: fixed; top: 0%; left: 0%; background: rgba(0, 0, 0, 0.8); display: none'>" +
                "<div class='review_modal_content' style='position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%);'>" +
                "<div class='container' style='height: 80%; overflow-x: hidden; overflow-y: auto'>" +
                "<div class='row'>" +
                "<div class='col-md-6 text-lg-right'>" +
                "</div>" +
                "</div>" +
                "<div id='reviewarea' name='reviewarea'>"
            let div3 = ""
            if (!result[i].reviews) {
                div3 = ""
            } else {
                for (let j = 0; j < result[i].reviews?.length; j++) {
                    if (mynickname == result[i].reviews[j].nickName) {
                        div3 += "<ul class=community-post-list>" +
                            "<li>" +
                            "<div class='community-post'>" +
                            "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'></div>" +
                            "<div class='post-content'>" +
                            "<div style='width: 95%; margin-top: 10px;'>" +
                            "<h5 style='display: inline'>" + result[i].reviews[j].nickName + "</h5>" + "&nbsp;" +
                            "<span class='post-date'>" + result[i].reviews[j].regDate + "</span>" +
                            "</div>" +
                            "<p style='white-space: pre-wrap;'>" + result[i].reviews[j].review + "</p>" +
                            "<div>" +
                            "<button type='button' class='" + result[i].reviews[j].review + "' id='modifyReview' name='modifyReview' value='" + result[i].reviews[j].regDate + "' style='cursor: pointer;'>" +
                            "<img src='https://i.ibb.co/SPYLSG3/trash.png' style='float: right; margin-right: 10px; font-size: 20px;'/>" +
                            "</button>" +
                            "<button type='button' class='" + result[i].reviews[j].review + "' id='deleteReview' name='deleteReview' value='" + result[i].reviews[j].regDate + "' style='cursor: pointer;'>" +
                            "<img src='https://i.ibb.co/5vHn8bd/write.png' style='float: right; margin-right: 5px; font-size: 20px;'/>" +
                            "</button>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</li>" +
                            "</ul>"
                    } else {
                        div3 += "<ul class=community-post-list>" +
                            "<li>" +
                            "<div class='community-post'>" +
                            "<div class='author-avator set-bg' data-setbg='img/authors/1.jpg'></div>" +
                            "<div class='post-content'>" +
                            "<div style='width: 95%; margin-top: 10px;'>" +
                            "<h5 style='display: inline'>" + result[i].reviews[j].nickName + "</h5>" + "&nbsp;" +
                            "<span class='post-date'>" + result[i].reviews[j].regDate + "</span>" +
                            "</div>" +
                            "<p style='white-space: pre-wrap;'>" + result[i].reviews[j].review + "</p>" +
                            "</div>" +
                            "</div>" +
                            "</li>" +
                            "</ul>"
                    }

                }
            }
            let div4 = "</div>" +
                "</div>" +
                "<div id='backtoreviewmodaldiv' name='backtoreviewmodaldiv'>" +
                "<textarea class='review_write' name='writerReview' id='writerReview'></textarea>" +
                "<button type='button' class='btn btn-warning' name='reviewbutton' id='reviewbutton'>" + '리뷰작성' + "</button>" +
                "<button type='button' style='margin-left: -90px; margin-top: -55px;' class='btn btn-warning' id='backtomain' name='backtomain'>" + '돌아가기' + "</button>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div id='fieldheart' name='fieldheart' class='rgi-heart'>"
            let likecount = "";
            if (!result[i].likeListCount) {
                likecount = "<span style='margin-left: 15px;' class='likelistcount'>" + '0' + "</span>"
            } else {
                likecount = "<span style='margin-left: 15px;' class='likelistcount'>" + result[i].likeListCount + "</span>"
            }
            let likeheart = "";
            if (!result[i].likeListCount) {
                likeheart =
                    "<button type='button' class='heartbrk' name='likeFieldArray' style='border: 0; cursor: pointer; background-color: #fff; width: 37px;' value='" + result[i].fname + "'>" +
                    "<img src='" + heart2 + "'>" +
                    "</button>"
            } else if (result[i].likeList.includes(mynickname)) {
                likeheart =
                    "<button type='button' class='heartbrk' name='likeFieldArray' style='border: 0; cursor: pointer; background-color: #fff; width: 37px;' value='" + result[i].fname + "'>" +
                    "<img src='" + heart1 + "'>" +
                    "</button>"
            } else {
                likeheart =
                    "<button type='button' class='heartbrk' name='likeFieldArray' style='border: 0; cursor: pointer; background-color: #fff; width: 37px;' value='" + result[i].fname + "'>" +
                    "<img src='" + heart2 + "'>" +
                    "</button>"
            }
            let div5 = "</div></div></div></div></div></div>"
            if (!mynickname) {
                area.innerHTML += div1
            } else {
                area.innerHTML += (div1 + reviewcount + div2 + div3 + div4 + likecount + likeheart + div5)
            }


        }

        let likeFieldArray = $("button[name='likeFieldArray']")

        for (let i = 0; i < likeFieldArray.length; i++) {
            console.log("likeFieldArray버튼의 길이 : " + likeFieldArray.length)
            $(likeFieldArray[i]).on('click', likefieldmain)

            async function likefieldmain(e) {
                let likelistcount = $(".likelistcount")[i]
                let fieldname = e.currentTarget.value
                console.log("몇번째 likeFieldArray : " + i)
                console.log("몇번째 likelistcount : " + i)

                likelistcount = likelistcount.innerText * 1
                let area = document.getElementsByName("fieldheart")
                console.log("좋아요 갯수 누르기전 결과 : " + likelistcount)
                console.log("좋아요 갯수타입 누르기전  : " + typeof (likelistcount))
                console.log("좋아요 누르기전 해당구장이름  : " + fieldname)

                while (area[i].hasChildNodes()) {
                    area[i].removeChild(area[i].firstChild)
                }
                let likefieldUrl = "/member/likeFieldAjax?field=" + fieldname
                let option = {method: "post"}

                try {
                    let res = await fetch(convertURL(likefieldUrl), option);
                    let result = await res.text();
                    console.log("result : " + result)
                    let div = document.createElement('div')
                    let span = document.createElement('span')
                    span.className = "likelistcount"
                    span.style = "margin-left:15px;"
                    let btn = document.createElement('button')
                    btn.type = "button"
                    btn.className = "heartbrk"
                    btn.name = "likeFieldArray"
                    btn.value = fieldname
                    btn.style = "border:0; cursor: pointer; background-color: #fff; width: 37px;"
                    let img = document.createElement('img')
                    if (result == "success") {
                        likelistcount += 1
                        span.innerText = likelistcount
                        img.src = heart1
                    } else if (result == "fail") {
                        likelistcount -= 1
                        span.innerText = likelistcount
                        img.src = heart2
                    }
                    btn.addEventListener('click', likefieldmain)
                    div.append(span)
                    btn.append(img)
                    area[i].append(div)
                    area[i].append(btn)
                    console.log("좋아요 숫자 누른 후 결과 : " + likelistcount)
                    console.log("좋아요 갯수타입 누르기후  : " + typeof (likelistcount))

                } catch (err) {
                    console.log("좋아요js - err : " + err)
                }
            }
        }

        for (let i = 0; i < fadeinmodals.length; i++) {
            funcs[i] = Modal(i);// 원하는 Modal 수만큼 Modal 함수를 호출해서 funcs 함수에 정의합니다.
            funcs[i]();// 원하는 Modal 수만큼 funcs 함수를 호출합니다.
        }
        // Modal 영역 밖을 클릭하면 Modal을 닫습니다.
        window.onclick = function (event) {
            if (event.target.className == "reviewmodal") {
                event.target.style.display = "none";
            }
        };
    } catch (err) {
        console.log("mainSortLatitudeLongitudeDistanceNearBidongki 패치 후 오류 : " + err)
    }
}

