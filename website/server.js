const express = require('express');
const app = express();

app.use(express.static('.'));

app.get('/', function (req, res) {
  res.send('intex.html');
});

app.listen(3000, function () {
  console.log('Starting Server');
});