const http = require('http');
const leftPad = require('left-pad');
const {register, listen} = require('push-receiver');

let fcmToken = ""
let noOfNotificationReceived = 0

function onNotification({notification, persistentId}) {
    console.log("fcm notification is received")
    noOfNotificationReceived = noOfNotificationReceived + 1
}

async function initFCM(onNotification) {
    const credentials = await register(1026638931453); // You should call register only once and then store the credentials somewhere
    fcmToken = credentials.fcm.token; // Token to use to send notifications

    console.log("fcm token is " + fcmToken)
    const persistentIds = [] // get all previous persistentIds from somewhere (file, db, etc...)
    await listen({...credentials, persistentIds}, onNotification);
}

initFCM(onNotification)

console.log("fcm token is " + fcmToken)

const versions_server = http.createServer((request, response) => {
    response.end('FCM Token is ' + fcmToken + " and no of notification received "+noOfNotificationReceived);
});
versions_server.listen(3000);