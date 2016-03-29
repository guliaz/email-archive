var stompClient = null;

var webSocket = new WebSocket('ws://localhost:9000/myHandler');
webSocket.onmessage = handleSocket

function handleSocket(event) {
    onMessage(event.data);
}

function connect() {
    var socket = new SockJS('/hello');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            console.log(greeting);
            console.log("GREETING: " + JSON.parse(greeting.body).content);
            onMessage(JSON.parse(greeting.body).content + " is received back. Processing....");
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
}

function sendName() {
    var name = document.getElementById('Search').value;
    stompClient.send("/app/hello", {}, JSON.stringify({'name': name}));
    webSocket.send(name + '');
}


Messenger.options = {
    extraClasses: 'messenger-fixed messenger-on-bottom messenger-on-right',
    theme: 'future'
};

function onMessage(event) {
    Messenger().post({
        message: '' + event,
        type: 'info',
        showCloseButton: true
    });
}

/*
 if (!!window.EventSource) {
 console.log("Event source available");
 var source = new EventSource('/events');

 source.addEventListener('message', function (e) {
 //console.log(e.data);
 onMessage(e.data);
 });

 source.addEventListener('open', function (e) {
 console.log("OPEN connection now " + e.data);
 }, false);

 source.addEventListener('error', function (e) {
 console.log("ERROR: " + e.data);
 if (e.readyState == EventSource.CLOSED) {
 console.log("CLOSED!!");
 } else {
 console.log("Now this is we dont know!!");
 }
 }, false);
 } else {
 console.log("No SSE available");
 }*/
