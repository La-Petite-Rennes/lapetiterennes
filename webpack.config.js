const path = require('path');

const config = {
    entry: './src/main/webapp/scripts/app/app.js'
    output: {
        path: path.resolve(__dirname, 'dist');
        finename: 'scripts/app/app.js'
    }
};

module.exports = config;
