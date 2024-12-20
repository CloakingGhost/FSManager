let likeFieldArray = $("button[name='likeFieldArray']");
let fieldname = document.getElementById("fName").value


$(likeFieldArray).on('click', likefield)
async function likefield(e) {
    let likecount = document.getElementById('likelistcount').innerText * 1

    console.log("좋아요 누르기 전 숫자 : " + likecount)
    console.log("좋아요 누르기전 likecount타입  : " + typeof(likecount))
    let area = document.getElementById("fieldheart")
    while (area.hasChildNodes()) {
        area.removeChild(area.firstChild)
    }
    let likefieldUrl = "/member/likeFieldAjax?field=" + fieldname
    if (contextPath != null) {
        likefieldUrl = contextPath + likefieldUrl
    }
    let option = {method: "post"}

    try {
        let res = await fetch(likefieldUrl, option);
        let result = await res.text();
        console.log("result : " + result)
        let span1 = document.createElement('span')
        let span2 = document.createElement('span')
        span2.id = "likelistcount"
        let btn = document.createElement('button')
        btn.type = "button"
        btn.className = "heartbrk"
        btn.name = "likeFieldArray"
        btn.value = fieldname
        btn.style = "width:50px; height:50px;"
        btn.addEventListener('click',likefield)
        let img = document.createElement('img')
        if (result == "success") {
            likecount += 1
            span2.innerText = likecount * 1
            img.src = "/img/icons/heart1.png"
            if (contextPath != null) {
                img.src = contextPath + "/img/icons/heart1.png"
            }
        } else if (result == "fail") {
            likecount -= 1
            img.src = "/img/icons/heart2.png"
            if(contextPath != null){
                img.src = contextPath + "/img/icons/heart2.png"
            }
            span2.innerText = likecount * 1
        }
        console.log("좋아요 누른 후 숫자 : " + likecount)
        console.log("좋아요 누른 후 숫자타입 : " + typeof(likecount))
        span1.append(span2)
        btn.append(img)
        area.append(span1)
        area.append(btn)

    } catch (err) {
        console.log("좋아요js - err : " + err)
    }
}