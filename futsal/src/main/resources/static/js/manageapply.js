function convertURL(url) {
    let targetURL;
    contextPath ? targetURL = contextPath + url : targetURL = url;
    return targetURL;
}
let acceptbtn = document.getElementsByName("acceptapply") //승인버튼
let rejectbtn = document.getElementsByName("rejectapply") //거부버튼
let kickmemberbutton = document.getElementsByName('kickmemberbtn') // 추방버튼
let exitteambtn = document.getElementsByName('exitteambtn') // 추방버튼
let cpt = document.getElementById("teamcpt").value

$(acceptbtn).on('click', acceptapply)
$(rejectbtn).on('click', rejectapply)
$(kickmemberbutton).on('click', kickMember)
$(exitteambtn).on('click', exitteam)

async function acceptapply(e){
    let applymem = e.currentTarget.value // 신청한사람의 닉네임
    console.log("applymem : " + applymem) // V
    let acceptapplyUrl = "/team/acceptApplyAjax?applymem=" + applymem
    if (contextPath != null) {
        acceptapplyUrl = contextPath + acceptapplyUrl
    }
    let option = {method: "post"}
    try{
        let acceptapplyres = await fetch(acceptapplyUrl, option)
        let acceptapplyresult = await acceptapplyres.text()
        console.log("acceptapplyresult : " + acceptapplyresult) // V
        if(acceptapplyresult == "success"){
            location.reload()
        }
    }catch (err){
        console.log("acceptapplyjs fetch err : " + err)
    }
}

async function rejectapply(e){
    let rejectmem = e.currentTarget.value // 신청한사람의 닉네임
    console.log("rejectmem : " + rejectmem) // V
    let rejectapplyUrl = "/team/rejectApplyAjax?rejectmem=" + rejectmem
    if (contextPath != null) {
        rejectapplyUrl = contextPath + rejectapplyUrl
    }
    let option = {method: "post"}
    try{
        let rejectapplyres = await fetch(rejectapplyUrl, option)
        let rejectapplyresult = await rejectapplyres.text()
        console.log("rejectapplyresult : " + rejectapplyresult) // V
        if(rejectapplyresult == "success"){
            location.reload()
        }
    }catch (err){
        console.log("rejectapplyresult fetch err : " + err)
    }
}

async function kickMember(e){
    let kickMemberConfirmChk = confirm("추방 하시겠습니까 ? ")
    if(kickMemberConfirmChk == true) {
        let kickmem = e.currentTarget.value // 추방할사람의 닉네임

        console.log("kickmem : " + kickmem)
        let kickmemberUrl = "/team/kickMemberAjax?kickmem=" + kickmem
        if (contextPath != null) {
            kickmemberUrl = contextPath + kickmemberUrl
        }
        let option = {method: "post"}
        try {
            let kickmemres = await fetch(kickmemberUrl, option)
            let kickmemresult = await kickmemres.text()
            console.log("kickmemresult : " + kickmemresult) // V
            if (kickmemresult == "success") {
                location.reload()
            }
        } catch (err) {
            console.log("kickmemresult fetch err : " + err)
        }
    }

}

async function exitteam(e){
    let exitteamConfirmChk = confirm("팀을 탈퇴하시겠습니까 ? ")
    if(exitteamConfirmChk == true){
        let exitteamname = e.currentTarget.value // 탈퇴할팀명
        console.log("탈퇴할팀명exitteamname : " + exitteamname)
        let exitteamUrl = "/team/exitTeamAjax?exitteam="+exitteamname
        if(contextPath != null){
            exitteamUrl = contextPath + exitteamUrl
        }
        let option = {method:"post"}
        try{
            let exitteamres = await fetch(exitteamUrl, option)
            let exitteamresult = await exitteamres.text()
            if(exitteamresult == "success"){
                location.reload()
            }

        }catch(err){
            console.log("exitteamresult fetch err : " + err)
        }
    }
}


// 팀 공지부분시작
let newnoticehere = document.getElementById("newnoticehere")

async function submitnotice(e){
    let tname = document.getElementById('yunuteamname').value
    let noticecontent = document.getElementById("noticecontent").value

    let form = new FormData();
    form.append("tname", tname)
    form.append("notice", noticecontent)
    console.log("notice : " + noticecontent)

    url = convertURL("/team/submitnotice")
    let option = {method:"post",body : form}
    document.getElementById("noticecontent").value =""
    try{
        let res = await fetch(url, option)
        let result = await res.json()
        console.log("submitnotice 패치 성공 결과 : " + result.notice)
        // newnoticehere 여기에 추가
        let div = '<tr id="newnoticehere">' +
            '<td>' +
            '<span style="font-weight: bold">' + cpt + '</span>' +
            '<span style="float: right">' +  result.regDate + '</span>' +
            '<textarea class="hi float-right font-classNamet-bold" readonly>' + result.notice +
            '</textarea>' +
            '</td>' + '</tr>'
        newnoticehere.innerHTML += div
    }catch (err){
        console.log("submitnotice 패치 후 오류 : " + err)
    }

}
