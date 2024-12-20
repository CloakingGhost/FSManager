let map;
let nowlat = document.getElementById('latitude').value;
let nowlon = document.getElementById('longitude').value;
window.addEventListener('DOMContentLoaded',function (){
    let markers = []
    let fNames = []
    let customOverlays = [];
    let fNList = document.getElementById('fNList').value;
    fNList = fNList.split(",")
    let latList = document.getElementById('latList').value;
    latList = latList.split(",")
    let lonList = document.getElementById('lonList').value;
    lonList = lonList.split(",")

    let nowfName = document.getElementById('fName').value;
    let mapContainer = document.getElementById('map_n'),
        mapOption = {
            center: new kakao.maps.LatLng(nowlat, nowlon),
            level: 4
        };
    map = new kakao.maps.Map(mapContainer, mapOption);

    let locPosition = new kakao.maps.LatLng(nowlat, nowlon)
    let mapTypeControl = new kakao.maps.MapTypeControl();
    let contextPath = document.getElementById('contextPathHolder').getAttribute('data-contextPath');
    map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);

    let zoomControl = new kakao.maps.ZoomControl();
    map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
    displayMarker(locPosition);

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
            if (nowlat != lat2 && nowlon != lon2) {
                let marker2 = new kakao.maps.Marker({
                    map: map,
                    position: locPosition2,
                    image: markerImage2
                })
                markers[i] = marker2.getPosition().getLat().toString() + ", " + marker2.getPosition().getLng().toString()
                fNames[i] = fName

                kakao.maps.event.addListener(marker2, 'mouseover', showInfo(marker2, map));
                kakao.maps.event.addListener(map, 'zoom_changed', function () {
                    for (let j = 0; j < customOverlays.length; j++) {
                        customOverlays.pop().setMap(null)
                    }
                });
            }
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
                        if (contextPath !== '/') {
                            content = '<li class="up"><a href="'+ contextPath + '/field/click?fName=' + fNames[k] + '"><span class="title">' + fNames[k] + '</span></a></li>'
                        } else {
                            content = '<li class="up"><a href="/field/click?fName=' + fNames[k] + '"><span class="title">' + fNames[k] + '</span></a></li>'
                        }
                    } else {
                        if (contextPath !== '/') {
                            content = content + '<li class="up"><a href="'+ contextPath + '/field/click?fName=' + fNames[k] + '"><span class="title">' + fNames[k] + '</span></a></li>'
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

        function closeOverlay() {
            customOverlay.setMap(null);
        }
    }



})




let fservicediv = document.getElementById('fieldservice')
let mapdiv = document.getElementById('map_n')
function fmapopen(e){
    if(getComputedStyle(fservicediv).display == 'block'){
        fservicediv.style.display = 'none';
        mapdiv.style.display = 'block';
        resizeMap();
        relayout();
    }
}

function fmapclose(e){
    if(getComputedStyle(fservicediv).display == 'none'){
        fservicediv.style.display = 'block';
        mapdiv.style.display = 'none';
    }
}
function resizeMap() {
    mapdiv.style.width = '75%';
    mapdiv.style.height = '750px';
}

function relayout() {
    console.log("왔니 ?")
    // 지도를 표시하는 div 크기를 변경한 이후 지도가 정상적으로 표출되지 않을 수도 있습니다
    // 크기를 변경한 이후에는 반드시  map.relayout 함수를 호출해야 합니다
    // window의 resize 이벤트에 의한 크기변경은 map.relayout 함수가 자동으로 호출됩니다
    map.relayout();
    // console.log("center : " + nowlat + " , " + nowlon)
    map.setCenter(new kakao.maps.LatLng(nowlat, nowlon))
    console.log("겟센터 : " + map.getCenter())
}
