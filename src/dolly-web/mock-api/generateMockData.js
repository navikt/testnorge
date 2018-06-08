const mockDataSchema = require('./mockDataSchema')
const jsf = require('json-schema-faker')
const fs = require('fs')

jsf.extend('faker', function() {
	var faker = require('faker')

	faker.locale = 'nb_NO'
	return faker
})

jsf.option({
	alwaysFakeOptionals: true
})

const json = JSON.stringify(jsf(mockDataSchema))

fs.writeFile('./mock-api/mock-data/db.json', json, function(err) {
	if (err) {
		return console.log(err)
	} else {
		console.log('Mock data generated.')
	}
})
