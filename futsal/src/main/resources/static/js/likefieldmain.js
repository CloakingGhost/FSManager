let likeFieldArray = $("button[name='likeFieldArray']")
let heart1 = convertURL("/img/icons/heart1.png");
let heart2 = convertURL("/img/icons/heart2.png");

for(let i = 0; i<likeFieldArray.length; i++){
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
        console.log("좋아요 갯수타입 누르기전  : " + typeof(likelistcount))
        console.log("좋아요 누르기전 해당구장이름  : " + fieldname)


        while (area[i].hasChildNodes()) {
            area[i].removeChild(area[i].firstChild)
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
            let div = document.createElement('div')
            let span = document.createElement('span')
            span.className = "likelistcount"
            span.style = "margin-left:15px;"
            let btn = document.createElement('button')
            btn.type = "button"
            btn.className = "heartbrk"
            btn.name = "likeFieldArray"
            btn.value = fieldname
            btn.style = "border:0; cursor: pointer; background-color: #fff; width:37px;"
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
            console.log("좋아요 갯수타입 누르기후  : " + typeof(likelistcount))

        } catch (err) {
            console.log("좋아요js - err : " + err)
        }
    }
}
