let applybtn = document.getElementsByName('applyteam')

$(applybtn).on('click', applyteam)

async function applyteam(e){
    let tName = e.currentTarget.value
    console.log(tName) // V

    let applyUrl = "/team/applyteamAjax?tName=" + tName
    if (contextPath != null) {
        applyUrl = contextPath + applyUrl
    }
    let option = {method: "post"}

    try{
        let res = await fetch(applyUrl, option)
        console.log("텍스트전타입 : " + typeof(res))
        let applyresult = await res.text();
        console.log("텍스트후타입 : " + typeof(applyresult))
        if(applyresult == "success"){
            alert("신청완료")
            location.reload();
        }else{
            alert("이미 신청한 팀입니다")
        }
    }catch (err){
        alert("applyteam fetch err : " + err)
    }
}

let waittingapply = document.getElementsByName("waittingapply")

$(waittingapply).on('click', deleteWaittingApply)

async function deleteWaittingApply(e){
    let deleteWaittingApplyconfirmchk = confirm("삭제 하시겠습니까 ? ")
    if(deleteWaittingApplyconfirmchk == true){
        let applytName = e.currentTarget.value // 해당팀네임
        let deleteapplyUrl = "/team/deleteApplyAjax?applytName=" + applytName
        if (contextPath != null) {
            deleteapplyUrl = contextPath + deleteapplyUrl
        }
        let option = {method: "post"}

        try{
            let deleteapplyres = await fetch(deleteapplyUrl, option)
            let deleteapplyresult = await deleteapplyres.text();
            console.log("deleteapplyresult : " + deleteapplyresult)
            if(deleteapplyresult == "success"){
                alert("삭제 완료")
                location.reload()
            }else{
                alert("삭제 실패")
            }

        }catch (err){
            console.log("delete applymember fetch err : " + err)
        }

    }else{
        location.reload()
    }
}