const express = require('express');
const cors = require('cors');
const { initBack } = require('./src/lib/back');

require('dotenv').config();

const app = express();
app.use(express.json({ limit: '50mb' }));
app.use(cors({
    origin: '*'
}));

initBack(app)
