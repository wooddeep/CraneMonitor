var net = require('net');

const PORT = 1733;
const HOST = '0.0.0.0';

const gdata = [
    "[[[% 1N 0N  0.88N 51.51N  0.00N  0.00N 0N#% 2N 0N  0.88N 31.51N  0.00N  0.00N 0N#% 3N 0N  0.48N 21.51N  0.00N  0.00N 0N#]]]",
    "[[[% 1N 0N  0.88N 51.51N  0.00N  0.00N 0N#% 2N 0N  0.87N 33.51N  0.00N  0.00N 0N#% 3N 0N  0.47N 22.51N  0.00N  0.00N 0N#]]]",
    "[[[% 1N 0N  0.88N 51.51N  0.00N  0.00N 0N#% 2N 0N  0.86N 35.51N  0.00N  0.00N 0N#% 3N 0N  0.46N 23.51N  0.00N  0.00N 0N#]]]",
    "[[[% 1N 0N  0.88N 51.51N  0.00N  0.00N 0N#% 2N 0N  0.85N 37.51N  0.00N  0.00N 0N#% 3N 0N  0.45N 24.51N  0.00N  0.00N 0N#]]]",
    "[[[% 1N 0N  0.88N 51.51N  0.00N  0.00N 0N#% 2N 0N  0.86N 35.51N  0.00N  0.00N 0N#% 3N 0N  0.46N 23.51N  0.00N  0.00N 0N#]]]",
    "[[[% 1N 0N  0.88N 51.51N  0.00N  0.00N 0N#% 2N 0N  0.87N 33.51N  0.00N  0.00N 0N#% 3N 0N  0.47N 22.51N  0.00N  0.00N 0N#]]]",
    "[[[% 1N 0N  0.88N 51.51N  0.00N  0.00N 0N#% 2N 0N  0.88N 31.51N  0.00N  0.00N 0N#% 3N 0N  0.48N 21.51N  0.00N  0.00N 0N#]]]"
]

var index = 0;
var clientHandler = function(socket){

    //客户端发送数据的时候触发data事件
  socket.on('data', function dataHandler(data) {//data是客户端发送给服务器的数据
    console.log(socket.remoteAddress, socket.remotePort, 'send', data.toString());

    //服务器向客户端发送消息
    //socket.write(data);
    //index = index + 1;
    //console.log(gdata[parseInt(index) % 7]);
    //socket.write(gdata[parseInt(index) % 7]);
    //socket.write('"[[[% 1N 0N  0.88N 51.51N  0.00N  0.00N 0N#% 2N 0N  0.88N 31.51N  0.00N  0.00N 0N#% 3N 0N  0.48N 21.51N  0.00N  0.00N 0N#]]]"')
    setInterval(() => {
        index = index + 1;
        //console.log(gdata[parseInt(index) % 7]);
        socket.write(gdata[parseInt(index) % 7]);
    }, 1000);
  });

    //当对方的连接断开以后的事件
  socket.on('close', function(){
    console.log(socket.remoteAddress, socket.remotePort, 'disconnected');
  })

      //当对方的连接断开以后的事件
  socket.on('error', function(){
    console.log(socket.remoteAddress, socket.remotePort, 'disconnected');
  })

};

//创建TCP服务器的实例
//传入的参数是：监听函数clientHandler
var app = net.createServer(clientHandler);

app.listen(PORT, HOST);
console.log('tcp server running on tcp://', HOST, ':', PORT);