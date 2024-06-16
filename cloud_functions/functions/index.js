// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.database();


exports.makeRevenue = functions.database.ref('/instructorPurchasedCourses/{instructorId}')
    .onWrite((snap, context) => {

     var documentRef = db.ref("/instructors/"+context.params.instructorId); 

     documentRef.once('value').then(documentSnapshot => {

  if (documentSnapshot.exists) {

    let field = documentSnapshot.val().monthly_revenue;

     documentRef.update({monthly_revenue: field+250});
  }

  return null;

});
    });

exports.increaseCourse = functions.database.ref('/courses/{courseId}')
    .onCreate((snap, context) => {
      let instructorId = snap.val().author_id;
        var documentRef = db.ref("/instructors/"+instructorId); 
     documentRef.once('value').then(documentSnapshot => {

  if (documentSnapshot.exists) {

    let field = documentSnapshot.val().total_courses;

     documentRef.update({total_courses: field+1});
  }

 
return null;
      });

           
    });

exports.getprice = functions.database.ref('/purchasedCourses/{customerId}')
    .onWrite((snap, context) => {

     var documentRef = db.ref("/purchasedCourses/"+context.params.customerId); 

     documentRef.orderByKey().limitToLast(1).once("child_added").then(documentSnapshot => {

  if (documentSnapshot.exists) {

    let instructorID = documentSnapshot.val().courseAuthorID;
    var price = documentSnapshot.val().coursePrice;
    price = (price*70)/100;
    var revenue = db.ref("/instructors/"+instructorID);
    revenue.once("value").then(docsi =>{
    if (docsi.exists) {
        let courses = docsi.val().total_purchased_courses;
        revenue.update({total_purchased_courses:courses+1});
        let monthly_revenue = docsi.val().monthly_revenue;
        if(documentSnapshot.val().isCoupon == true){
          price  = (price * 90)/100;
        }
        else if(docsi.val().Marketing == true){
        price = (price * 40) /100;
        }
        else{
          price = (price*60)/100;
        }
        revenue.update({monthly_revenue:monthly_revenue+price});
      }
      return null;
    });

  }

  return null;

});
    });


exports.decreaseCourse = functions.database.ref('/courses/{courseId}')
    .onDelete((snap, context) => {
      let instructorId = snap.val().author_id;
        var documentRef = db.ref("/instructors/"+instructorId);
     documentRef.once('value').then(documentSnapshot => {

  if (documentSnapshot.exists) {

    let field = documentSnapshot.val().total_courses;

     documentRef.update({total_courses: field-1});
  }


return null;
      });


    });


exports.credits = functions.database.ref('/commentLikes/{uid}')
    .onWrite((snap, context) => {

var count = 0;
var kuchbhi = db.ref("/commentLikes/"+context.params.uid).orderByKey().once("value").then(function(dsnap){
dsnap.forEach(function(childern) {
        count += dsnap.child(childern.key).numChildren();
        console.log(childern.key);
});

if(count%5==0){
   count/=5;
var documentRef = db.ref("/users_testing/"+context.params.uid);
documentRef.update({cerdit:count});
}


return null;
});
    });




exports.generateRandomCoupon = functions.database.ref('/courses/{courseId}')
    .onCreate((snap, context) => {
      //first we will read course name
      var kuchbhi = db.ref("/courses/"+context.params.courseId);
      kuchbhi.once("value").then(function(dsnap){
      var name = dsnap.val().name;
      name = name.replace(/\s/g, '_'); 
      console.log(name);
      //replace space with '_'
      var coup = name+Math.round(Math.random() * (10000 - 99999)+10000);
      //than we will generate a random course coupon
      //than update the coupon node
      return kuchbhi.update({promocode:coup});
     
});

      

});


// Take the text parameter passed to this HTTP endpoint and insert it into 
// Cloud Firestore under the path /messages/:documentId/original
exports.CompletedText = functions.https.onRequest(async (req, res) => {
  // Grab the text parameter.
  const uid = req.query.uid;
  const cid = req.query.cid;
  const chname = req.query.chname;
  const chid = req.query.chid;
  const mno  = req.query.mno;
  var docR = db.ref('/Results/'+cid+'/'+chid+'/'+chname+'/'+uid).once('value').then(function(docr){
  var score = docr.val();
  var doct = db.ref('/tests/'+cid+'/'+chid).once('value').then(function(doct){
  var total = doct.child(chname).numChildren();

  console.log(score);
  console.log(total);
  if(score >= (total/2)){
    console.log('iniside');
  var documentRef = db.ref('/completed/'+uid+'/'+cid);
  documentRef.once('value').then(documentSnapshot => {

  if (documentSnapshot.exists) {

    let field = documentSnapshot.val().text+1;
     if(field == mno){
     documentRef.update({text: field});}
     }
  return res.json({'result':'Done!!'})
 });
}
return null;
});
  return null;
});
  return null;
});

exports.CompletedVideo = functions.https.onRequest(async (req, res) => {
 // Grab the text parameter.
  const uid = req.query.uid;
  const cid = req.query.cid;
  const chname = req.query.chname;
  const chid = req.query.chid;
  const mno  = req.query.mno;
  var docR = db.ref('/Results/'+cid+'/'+chid+'/'+chname+'/'+uid).once('value').then(function(docr){
  var score = docr.val();
  var doct = db.ref('/tests/'+cid+'/'+chid).once('value').then(function(doct){
  var total = doct.child(chname).numChildren();
  if(score >= (total/2)){
    console.log('iniside');
  var documentRef = db.ref('/completed/'+uid+'/'+cid);
  documentRef.once('value').then(documentSnapshot => {

  if (documentSnapshot.exists) {

    let field = documentSnapshot.val().Video + 1;
    if(field == mno){
     documentRef.update({Video: field});
   }

  }
  return res.json({'result':'Done!!'})
 });
}
return null;
});
  return null;
});
  return null;
});



exports.addMessage = functions.https.onRequest(async (req, res) => {
// [END addMessageTrigger]
  // Grab the text parameter.
  const original = req.query.text;
  console.log(original);
  // [START adminSdkPush]
  // Push the new message into the Realtime Database using the Firebase Admin SDK.
  // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
  return res.json({"result":"done"});
});
  //


exports.refReward = functions.database.ref('/referralProg/{uid}')
    .onWrite((snap, context) => {

    var docR = db.ref('/referralProg/'+context.params.uid).once('value').then(function(docr){

        var score = docr.val().referred_number_users;
        if(score == 10){
          var documentRef = db.ref("/referralProg/"+context.params.uid);
          documentRef.update({referred_number_users:0});
          documentRef.update({referred_code:true});
        }
        return null;
      });
    });