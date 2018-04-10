const mockDataSchema = require('./mockDataSchema');
const jsf = require('json-schema-faker');
const fs = require('fs');

console.log(mockDataSchema);
const json = JSON.stringify(jsf(mockDataSchema));
console.log(json);

fs.writeFile("./src/mock-api/mock-data/db.json", json, function (err) {
    if (err) {
        return console.log(err);
    } else {
        console.log("Mock data generated.");
    }
});