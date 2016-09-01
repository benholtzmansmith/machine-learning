const express = require('express');
const app = express();

app.use(express.static('.'));

app.get('/', function (req, res) {
  res.sendFile('index.html', {root: __dirname });
});

app.get('/train', function (req, res){
	setTimeout(function(){res.sendStatus(200)}, 5000)
})

app.listen(3000, function () {
  console.log('Starting Server on: http://127.0.0.1:3000/');
});