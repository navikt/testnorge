const mockDataSchema = require('./mockDataSchema');
const jsf = require('json-schema-faker');
const fs = require('fs');

const json = JSON.stringify(jsf(mockDataSchema));

fs.writeFile("./src/mock-api/mock-data/db.json", json, function (err) {
    if (err) {
        return console.log(err);
    } else {
        console.log("Mock data generated.");
    }
});