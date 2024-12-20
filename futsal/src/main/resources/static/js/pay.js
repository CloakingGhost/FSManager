let payhere = document.getElementById('payhere')
let IMP;
function payformopen(){
    if(payhere.style.display == "none"){
        payhere.style.display = 'block'
    }else{
        payhere.style.display = 'none'
    }
}

function requestPay1() {
    let test;
    let aaa = document.querySelector('input[type=radio][name=howmuch]:checked');

    if(aaa.value == "10000"){
        console.log("10000원 찍힘")
        test = document.getElementById('r1')
        console.log("test : " + test.value)
    }else if(aaa.value == "50000"){
        console.log("50000원 찍힘")
        test = document.getElementById('r2')
        console.log("test : " + test.value)
    }else if(aaa.value == "100000"){
        console.log("100000원 찍힘")
        test = document.getElementById('r3')
        console.log("test : " + test.value)
    }else if(aaa.value == "직접입력"){
        test = document.getElementById('r4')
        console.log("test : " + test.value)
    }

    let howmuch = test
    let useremail = document.getElementById('userId').value
    let mynickname = document.getElementById('mynickname').value
    let uuid = document.getElementById('uuid').value

    console.log("입력 금액 : " + howmuch.value)
    howmuch.value = parseInt(howmuch.value)
    console.log("형변환 후 입력 금액 : " + howmuch.value)
    console.log("입력한 금액의 타입 : " + typeof(howmuch.value))
    if(howmuch.value == ""){
        alert("충전할 금액을 입력하세요 !")

    }else if(howmuch.value === 'NaN'){
        alert("충전할 금액을 숫자만 입력하세요 !")
    }
    else{

        IMP = window.IMP;
        IMP.init("imp02435263");
        IMP.request_pay({
            pg: "kakaopay",
            // pay_method: "kakaopay",
            merchant_uid: uuid,
            name: "풋살매니저",
            amount: howmuch.value,
            buyer_email: useremail,
            buyer_name: mynickname,
            buyer_tel: "010-8223-4716",
            buyer_addr: "서울특별시 양천구 목동",
            buyer_postcode: "01181"
            // app_scheme: 'example',
            // digital:'false',
        }, function (rsp) { // callback
            if (rsp.success) {
                console.log("결제성공 : " + rsp);
                alert("결제 성공 !")
                howmuch.innerText = "";
                addmemberpoint(howmuch.value);
                jQuery.ajax({
                    url: "https://www.myservice.com/payments/complete", // 예: https://www.myservice.com/payments/complete
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    data: {
                        imp_uid: rsp.imp_uid,
                        merchant_uid: rsp.merchant_uid
                    }
                }).done(function (data) {

                    // 가맹점 서버 결제 API 성공시 로직
                })
            } else {
                alert("결제실패 : " + rsp);
                console.log("결제실패된 res : " + JSON.stringify(rsp));
            }
        });

    }
}
function requestPayopen(){
    let payway = document.getElementById('payway').value
    console.log("payway : " + payway)
    if(payway === "kakaopay"){
        requestPay1()

    }else if(payway === "card"){
        requestPay2();
    }
}


function requestPay2() {
    let test;
    let aaa = document.querySelector('input[type=radio][name=howmuch]:checked');

    if(aaa.value == "10000"){
        test = document.getElementById('r1')
    }else if(aaa.value == "50000"){
        test = document.getElementById('r2')
    }else if(aaa.value == "100000"){
        test = document.getElementById('r3')
    }else if(aaa.value == "직접입력"){
        test = document.getElementById('r4')
    }

    let howmuch = test
    let useremail = document.getElementById('userId').value
    let mynickname = document.getElementById('mynickname').value
    let uuid = document.getElementById('uuid').value

    console.log("입력 금액 : " + howmuch.value)
    howmuch = parseInt(howmuch.value)
    console.log("형변환 후 입력 금액 : " + howmuch)
    console.log("입력한 금액의 타입 : " + typeof(howmuch))
    if(howmuch.value == ""){
        alert("충전할 금액을 입력하세요 !")

    }else if(howmuch.value === 'NaN'){
        alert("충전할 금액을 숫자만 입력하세요 !")
    }else{

        IMP = window.IMP;
        IMP.init("imp02435263");
        console.log("가맹점 코드 식별완료")

        IMP.request_pay({
            pg : 'nice',
            pay_method : 'card',
            merchant_uid: uuid, //상점에서 생성한 고유 주문번호
            name : '풋살매니저',
            amount : howmuch,
            buyer_email : useremail,
            buyer_name : mynickname,
            buyer_tel : '010-1234-5678',
            buyer_addr : '서울특별시 양천구 목동',
            buyer_postcode : '123-456',
            // m_redirect_url : convertURL("/main"), // 예: https://www.my-service.com/payments/complete/mobile
            m_redirect_url : '{결제 완료 후 리디렉션 될 URL}', // 예: https://www.my-service.com/payments/complete
            niceMobileV2 : true // 신규 모바일 버전 적용 시 설정
        }, function(rsp) { // callback 로직
            //* ...중략 (README 파일에서 상세 샘플코드를 확인하세요)... *//
            if (rsp.success) {
                console.log("결제성공 : " + rsp);
                alert("결제 성공 !")
                howmuch.innerText = "";
                addmemberpoint(howmuch);
                jQuery.ajax({
                    url: "https://www.myservice.com/payments/complete", // 예: https://www.myservice.com/payments/complete
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    data: {
                        imp_uid: rsp.imp_uid,
                        merchant_uid: rsp.merchant_uid
                    }
                }).done(function (data) {
                    // 가맹점 서버 결제 API 성공시 로직
                })
            } else {
                alert("결제실패 : " + rsp);
                console.log("결제실패된 res : " + JSON.stringify(rsp));
            }
        });
    }
}




async function addmemberpoint(howmuch){

    url = convertURL("/member/addpoints?point=" + howmuch)
    let option = {method: "post"}
    try{
        await fetch(url,option)
        location.reload()
    }catch (err){
        console.log("addmemberpoint 패치 후 오류 : " + err)
    }
}


// /pay/test 로 amount, buyer_name 보내기
async function test(){

    let amount = document.getElementById('howmuch').value
    let mynickname = document.getElementById('mynickname').value
    let url = convertURL("/pay/test?amount=" + amount + "&buyer_name=" + mynickname)
    let option = {method:"post"}
    try{
        let res = await fetch(url, option)
        let result = await res.text();
        // document.body += result;
        const div = document.createElement("div")
        div.innerHTML = result;
        const body = document.querySelector("body")
        body.appendChild(div);


        console.log("result : " + result)

    }catch (err){
        console.log("err : "  + err)
    }
}

async function test1(){
    let test;
    let aaa = document.querySelector('input[type=radio][name=howmuch]:checked');

    if(aaa.value == "10000"){
        test = document.getElementById('r1')
    }else if(aaa.value == "50000"){
        test = document.getElementById('r2')
    }else if(aaa.value == "100000"){
        test = document.getElementById('r3')
    }else if(aaa.value == "직접입력"){
        test = document.getElementById('r4')
    }

    let howmuch = test
    let useremail = document.getElementById('userId').value
    let mynickname = document.getElementById('mynickname').value
    let uuid1 = document.getElementById('uuid').value

    console.log("입력 금액 : " + howmuch.value)
    howmuch = parseInt(howmuch.value)
    console.log("형변환 후 입력 금액 : " + howmuch)
    console.log("입력한 금액의 타입 : " + typeof(howmuch))
    if(howmuch.value == ""){
        alert("충전할 금액을 입력하세요 !")

    }else if(howmuch.value === 'NaN'){
        alert("충전할 금액을 숫자만 입력하세요 !")
    }else{

        // let form = document.createElement('form');
        // form.setAttribute('method', 'post');
        // let url = convertURL("/pay/test?amount=" + howmuch + "&buyer_name=" + mynickname + "&uuid=" + uuid1)
        // form.setAttribute('action',url)
        // console.log("넘길값 : " + howmuch + "&&" + mynickname + "&&" + uuid1 + "&&" + typeof(mynickname))
        // document.body.appendChild(form);
        // form.submit();

        try{
            let url = convertURL("/pay/test?amount=" + howmuch + "&buyer_name=" + mynickname + "&uuid=" + uuid1)
            let option = {method:"post"}
            let res = await fetch(url, option)
            // let result = await res.json();
            document.body.append(res)
        }catch (err){
            console.log("err : " + err)
        }
    }

}

