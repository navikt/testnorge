import Formatters from '~/utils/DataFormatter'

export const getAttributesFromMal = mal => {
	const tpsfKriterier = JSON.parse(mal.tpsfKriterier)
	const bestKriterier = JSON.parse(mal.bestKriterier)

	// console.log('tpsf', tpsfKriterier)
	// console.log('best', bestKriterier)

	let attrArray = []
	attrArray = Object.keys(tpsfKriterier).filter(k => {
		if (k !== 'identtype' && k !== 'relasjoner' && k !== 'regdato') {
			return k
		}
	})

	if (tpsfKriterier.relasjoner) {
		tpsfKriterier.relasjoner.barn && attrArray.push('barn')
		tpsfKriterier.relasjoner.partner && attrArray.push('partner')
	}

	// tpsfKriterier.keys(attr => {
	// 	if(attr === "")
	// });

	// TODO: Nå som disse id-ene er brukt flere steder på prosjektet gjennom mappingen, vurder å lage en constant klasse
	Object.keys(bestKriterier).forEach(reg => {
		attrArray.push(_mapRegistreKey(reg))
	})

	console.log('attrArray :', attrArray)
	return attrArray
}

export const getValuesFromMal = mal => {
	let reduxStateValue = {}
	const tpsfKriterierArray = Object.entries(JSON.parse(mal.tpsfKriterier))

	// console.log('TCL: tpsfKriterier', tpsfKriterier)
	// console.log('TCL: tpsfValuesArray', Object.entries(tpsfKriterier))

	const bestKriterierArray = Object.entries(JSON.parse(mal.bestKriterier))

	console.log('bestKriterier', bestKriterierArray)

	// TODO: Alex - lag rekrusiv funksjon for dette

	_mapValuesToObject(reduxStateValue, tpsfKriterierArray)

	bestKriterierArray.forEach(reg => {
		console.log('reg :', reg)
		// *Spesiell tilfelle for Krr-stub
		let valueArray = _mapRegistreValue(reg[0], reg[1])
		if (Array.isArray(valueArray)) {
			_mapArrayValuesToObject(reduxStateValue, valueArray, reg[0])
		}
	})

	// _mapValuesToObject(reduxStateValue, bestKriterierArray)

	console.log('reduxStateValue :', reduxStateValue)

	return reduxStateValue
}

const _mapValuesToObject = (objectToAssign, valueArray, customKeyPrefix = '') => {
	valueArray.forEach(v => {
		let key = v[0]

		if (key === 'regdato' || key === 'identtype') return
		let value = v[1]
		if (value) {
			// Formater values før vi lager objekt
			value = _formatValueForObject(key, value)
			if (key === 'relasjoner') {
				value.partner &&
					_mapValuesToObject(objectToAssign, Object.entries(value.partner), 'partner_')

				if (value.barn && Array.isArray(value.barn)) {
					// TODO: Samme greia som register? . Alex - lag funksjon

					console.log(value.barn, 'value barn')
					_mapArrayValuesToObject(objectToAssign, value.barn, 'barn', 'barn_')
					// let barnObjectsArray = []
					// value.barn.forEach(v => {
					// 	let barnObject = {}
					// 	_mapValuesToObject(barnObject, Object.entries(v), 'barn_')
					// 	barnObjectsArray.push(barnObject)
					// })
					// Object.assign(objectToAssign, {
					// 	barn: barnObjectsArray
					// })
				}
			} else {
				Object.assign(objectToAssign, {
					[customKeyPrefix + key]: value
				})
			}
		}
	})
}

const _mapArrayValuesToObject = (objectToAssign, valueArray, key, customKeyPrefix = '') => {
	const mappedKey = _mapRegistreKey(key)

	console.log(key, mappedKey)

	let valueArrayObj = []

	valueArray.forEach(v => {
		let childObj = {}
		_mapValuesToObject(childObj, Object.entries(v), customKeyPrefix)
		valueArrayObj.push(childObj)
	})
	Object.assign(objectToAssign, {
		[mappedKey]: valueArrayObj
	})
}

const _formatValueForObject = (key, value) => {
	const dateAttributes = ['foedtFoer', 'foedtEtter', 'doedsdato', 'fom', 'tom', 'gyldigFra']

	if (dateAttributes.includes(key)) {
		value = Formatters.formatDate(value)
	} else if (key === 'egenAnsattDatoFom') {
		value = true
	}

	return value
}

const _mapRegistreKey = key => {
	// TODO: Nå som disse id-ene er brukt flere steder på prosjektet gjennom mappingen, vurder å lage en constant klasse

	switch (key) {
		case 'aareg':
			return 'arbeidsforhold'
		case 'sigrunStub':
			return 'inntekt'
		case 'krrStub':
			return 'krr'
		default:
			return key
	}
}

const _mapRegistreValue = (key, value) => {
	switch (key) {
		case 'aareg':
			return value
		case 'sigrunStub':
			let mappedValue = []
			value.forEach(inntekt => {
				inntekt.grunnlag.forEach(g => {
					mappedValue.push({
						typeinntekt: g.tekniskNavn,
						inntektsaar: inntekt.inntektsaar,
						tjeneste: inntekt.tjeneste,
						beloep: g.verdi
					})
				})
			})
			return mappedValue
		case 'krrStub':
			return [value]
		default:
			return value
	}
}
