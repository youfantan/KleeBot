importScripts('./crypto-4.1.1-forked.js')
const key='e348da05aa56fe0a';
const iv='ea412a61f9cd59a4'
const AESKey=CryptoJS.enc.Utf8.parse(key);
const AESIV=CryptoJS.enc.Utf8.parse(iv)
function AESEncrypt(src) {
    let AESSrc=CryptoJS.enc.Utf8.parse(src)
    let AESEncrypted=CryptoJS.AES.encrypt(AESSrc,AESKey,{
        iv:AESIV,
        mode:CryptoJS.mode.CBC,
        padding:CryptoJS.pad.Pkcs7
    })
    return AESEncrypted.ciphertext.toString();
}
function AESDecrypt(src){
    let AESHex=CryptoJS.enc.Hex.parse(src);
    let AESSrc=CryptoJS.enc.Base64.stringify(AESHex);
    let AESDecrypted=CryptoJS.AES.decrypt(AESSrc,AESKey,{
        iv:AESIV,
        mode:CryptoJS.mode.CBC,
        padding:CryptoJS.pad.Pkcs7
    });
    let StrDecrypted=CryptoJS.enc.Utf8.stringify(AESDecrypted)
    return StrDecrypted.toString();
}
self.addEventListener('message',function (msg) {
    let decrypted=AESDecrypt(msg.data)
    postMessage(decrypted)
})