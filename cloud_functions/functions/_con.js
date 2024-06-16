//const functions = require('firebase-functions');
var serviceAccount = require("/home/yash/firebase/reveune/serviceAccount.json");
// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://voltstudy-dcdd8.firebaseio.com"
});

var db = admin.database();
var count = 0;
var key;
var childData;
var docR = db.ref('/Results/-MB4QWSkAtbQeVTi9wOW/-MB4QkG3tb8AEw9dAKM7/T&D System Test/bvxPSWtFPkg3PYU4Ot44Q7ElHrp1').once('value').then(function(docr){
    var score = docr.val();
    

    var doct = db.ref('/tests/-MB4QWSkAtbQeVTi9wOW/-MB4QkG3tb8AEw9dAKM7').once('value').then(function(doct){
    var total = doct.child('T&D System Test').numChildren();
    console.log(score);
    console.log(total);
    	console.log(score >= (total/2));
});

});