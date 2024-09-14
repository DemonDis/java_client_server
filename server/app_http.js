var express = require('express');
var app = express();

app.get('/', function (req, res) {
    res.send('<b>My</b> first express http server');
});

// 1) Add a route that answers to all request types
app.route('/article')
.get(function(req, res) {
    res.send('Get the article');
})
.post(function(req, res) {
    res.send('Add an article');
})
.put(function(req, res) {
    res.send('Update the article');
});

// 2) Use a wildcard for a route
// answers to : theANYman, thebatman, thesuperman
app.get('/the*man', function(req, res) {
    res.send('the*man');
});

// 3) Use regular expressions in routes
// responds to : batmobile, batwing, batcave, batarang
app.get(/bat/, function(req, res) {
    res.send('/bat/');
});

app.use(function(req, res, next) {
    res.status(404).send("Sorry, that route doesn't exist. Have a nice day :)");
});

app.listen(3000, function () {
    console.log('Example app listening on port 3000.');
});
