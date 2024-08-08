console.log("run script");
Java.perform(function(){
const x = Java.use('be.howest.ti.mobilesecurity.MainActivityKt');
x.isAdmin.implementation = function(a,b){return true}
});