var stompClient = null;

function setConnected(connected) {
    $("#login").prop("disabled", connected);
    $("#logout").prop("disabled", !connected);
    if (connected) {
        $("#logFeed").show();
    }
    else {
        $("#logFeed").hide();
    }
    $("#logs").html("");
}

function connect() {
    var socket = new SockJS('/stomp-logger-web-socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/queue/logs', function (logs) {
            var listLogs = JSON.parse(logs.body);
            for (var i=0;i<listLogs.length;i++) {
                showLog(listLogs[i].content);
            }
        });
        sendSubscribeRequest();
    });

    displayStompObject();
}

function disconnect() {
    sendUnsubscribeRequest();
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showLog(log) {
    $("#logs").append("<tr><td>" + log + "</td></tr>");
}

function sendSubscribeRequest() {
 stompClient.send("/app/subscribe", {}, JSON.stringify({
  'logfile' : $("#logfile").val()
 }));
}

function sendUnsubscribeRequest() {
 console.log("INSIDE sendUnsubscribeRequest");
 stompClient.send("/app/unsubscribe", {}, JSON.stringify({
  'logfile' : $("#logfile").val()
 }));
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#login" ).click(function() { connect(); });
    $( "#logout" ).click(function() { disconnect(); });
});