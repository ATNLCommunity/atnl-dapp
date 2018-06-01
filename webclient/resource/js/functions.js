/**
 * Created by wf on 2018\4\21 0021.
 */
function jumppage(url){
    window,location.href=url;
}

function is_weixn(){
    var ua = navigator.userAgent.toLowerCase();
    if(ua.match(/MicroMessenger/i)=="micromessenger") {
        return true;
    } else {
        return false;
    }
}

function toWxPay(api,oid){
        window.location.href=  'http://wx.atunala.com/app/'+api+'?oid=' + oid;;
}