var express = require('express');
var app = express();

app.use(express.json({
    inflate: true,
    limit: '100kb',
    reviver: null,
    strict: true,
    type: 'application/json',
    verify: undefined
}));
app.use(express.urlencoded({ extended: true }));

app.get('/', function (req, res) {
    res.send('<b>My</b> first express http server');
});

app.route('/article')
.get(function(req, res) {
    console.log('Client прислал', req.query);
    res.send('Get the article');
})
.post(function(req, res) {
    res.send(`Server получил и вернул то же: ${JSON.stringify(req.body)}`);
    console.log('Client прислал', req.body);
})
.put(function(req, res) {
    res.send('Update the article');
});

app.get('/the*man', function(req, res) {
    res.send('the*man');
});

app.get(/bat/, function(req, res) {
    res.send('/bat/');
});

app.use(function(req, res, next) {
    res.status(404).send("Sorry, that route doesn't exist. Have a nice day :)");
});

app.listen(3000, function () {
    console.log('Example app listening on port 3000.');
});
