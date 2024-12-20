function convertURL(url) {
    return contextPath ? contextPath + url : url;
}
function f() {
    document.getElementsByClassName("dropdown")[0].classList.toggle("down");
    document.getElementsByClassName("arrow")[0].classList.toggle("gone");
    if (
        document.getElementsByClassName("dropdown")[0].classList.contains("down")
    ) {
        setTimeout(function () {
            document.getElementsByClassName("dropdown")[0].style.overflow = "visible";
        }, 500);
    } else {
        document.getElementsByClassName("dropdown")[0].style.overflow = "hidden";
    }
}


async function movetoreservation(e){
    let message = e.lastElementChild.lastElementChild.innerHTML;
    console.log("message : " + message)
    url = convertURL("/movetoreservation?message="+message)
    let option = {
        method:"post"
    }
    try{
        let res = await fetch(url, option)
        console.log("moveto reservation 패치 후 성공")
        window.location.href=convertURL("/reservation")

    }catch (err){
        console.log("movetoreservation 패치 후 오류 : " + err)
    }
}

let quickmenubtn = document.getElementById('quickmenubtn')
let quickmenumodal = document.getElementById('quickmenumodal')

quickmenubtn.addEventListener("click", function() {
    // Check if the modal is currently displayed
    if (getComputedStyle(quickmenumodal).visibility === 'hidden') {
        // Show the modal
        // Animate the transition
        // quickmenumodal.animate({
        //     top: 0,
        //     opacity: 1
        // }, {
        //     duration: 800,
        //     easing: 'ease-out'
        // });
        quickmenumodal.style.visibility = 'visible';
    } else {
        // Animate the transition
        // quickmenumodal.animate({
        //     top: 0,
        //     opacity: 0
        // }, {
        //     duration: 800,
        //     easing: 'ease-out'
        // });
        quickmenumodal.style.visibility = 'hidden';
    }
});



// let mapherelol = document.getElementById('map_n')
// let mapopenlol = document.getElementById('mapopenlol')
// mapopenlol.addEventListener('click',fmapopenlol)
// function fmapopenlol(e){
//     console.log("mapopenlol 함수 들어옴")
//     if(mapherelol.style.display == 'none'){
//         console.log("mapopenlol 함수 if문조건통과")
//         mapherelol.style.display ='block'
//         relayoutlol();
//         if(calendarhere.style.display == 'block'){
//             calendarhere.style.display = 'none'
//         }
//         if(alarmhere.style.display == 'block'){
//             alarmhere.style.display = 'none'
//         }
//     }else if(mapherelol.style.display == 'block'){
//         console.log("mapopenlol 함수 else문조건통과")
//         mapherelol.style.display ='none'
//         relayoutlol();
//     }
// }
//
//
// function relayoutlol() {
//     console.log("혼자야 ?")
//     // 지도를 표시하는 div 크기를 변경한 이후 지도가 정상적으로 표출되지 않을 수도 있습니다
//     // 크기를 변경한 이후에는 반드시  map.relayout 함수를 호출해야 합니다
//     // window의 resize 이벤트에 의한 크기변경은 map.relayout 함수가 자동으로 호출됩니다
//     map.relayout();
//     console.log("메인에서 relayout함수 발동")
//     console.log("메인에서 센터변경전 : " + map.getCenter())
//     map.setCenter(new kakao.maps.LatLng(37.4946287, 127.0276197))
//     console.log("메인에서 센터변경후 : " + map.getCenter())
// }

let alarmopenlol = document.getElementById('alarmopenlol')
let alarmhere = document.getElementById('alarmhere')
alarmopenlol.addEventListener('click',alarmopen)
function alarmopen(){
    if(getComputedStyle(alarmhere).display == 'none'){
        alarmhere.style.display ='block';
    }else{
        alarmhere.style.display = 'none';
    }
}

// let calendaropenlol  = document.getElementById('calendaropenlol')
// let calendarhere = document.getElementById('calendarhere')
// calendaropenlol.addEventListener('click',calendaropen)
// function calendaropen(e){
//     if(calendarhere.style.display == 'none'){
//         calendarhere.style.display ='block';
//         if(alarmhere.style.display == 'block'){
//             alarmhere.style.display = 'none'
//         }
//         if(mapherelol.style.display == 'block'){
//             mapherelol.style.display = 'none'
//         }
//     }else{
//         calendarhere.style.display = 'none';
//     }
// }
function openreservation(){
    location.href = convertURL("/reservation");
}
function opencommunity(){
    location.href = convertURL("/community/board");
}
function openteam(){
    location.href = convertURL("/teamtables");
}

function withdraw(){
    console.log("서비스 준비중 !")
    alert("서비스 준비중입니다.")
}