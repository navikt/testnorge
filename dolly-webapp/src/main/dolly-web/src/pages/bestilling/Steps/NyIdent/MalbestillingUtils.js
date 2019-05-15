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
		switch (reg) {
			case 'aareg':
				attrArray.push('arbeidsforhold')
				break
			case 'sigrunStub':
				attrArray.push('inntekt')
				break
			case 'krrStub':
				attrArray.push('krr')
			default:
				break
		}
	})

	console.log('attrArray :', attrArray)
	return attrArray
}

export const getValuesFromMal = mal => {
	const dateAttributes = ['foedtFoer', 'foedtEtter', 'doedsdato', 'fom', 'tom']

	let reduxStateValue = {}
	const tpsfKriterier = JSON.parse(mal.tpsfKriterier)
	console.log('TCL: tpsfKriterier', tpsfKriterier)

	const tpsfValuesArray = Object.entries(tpsfKriterier)

	console.log('TCL: tpsfValuesArray', tpsfValuesArray)

	// TODO: Alex - lag rekrusiv funksjon for dette

	_mapValuesToObject(reduxStateValue, tpsfValuesArray)

	console.log('reduxStateValue :', reduxStateValue)
	return reduxStateValue
}

const _mapValuesToObject = (objectToAssign, valueArray, keyPrefix = '') => {
	valueArray.forEach(v => {
		const key = v[0]
		if (key === 'regdato') return

		let value = v[1]
		if (value) {
			// Formater values før vi lager objekt
			value = _formatValueForObject(key, value)
			if (key === 'relasjoner') {
				value.partner &&
					_mapValuesToObject(objectToAssign, Object.entries(value.partner), 'partner_')

				if (value.barn && value.barn.length > 0) {
					let barnObjectsArray = []
					value.barn.forEach(v => {
						let barnObject = {}
						_mapValuesToObject(barnObject, Object.entries(v), 'barn_')
						barnObjectsArray.push(barnObject)
					})
					Object.assign(objectToAssign, {
						barn: barnObjectsArray
					})
				}
			} else {
				Object.assign(objectToAssign, {
					[keyPrefix + key]: value
				})
			}
		}
	})
}

const _formatValueForObject = (key, value) => {
	const dateAttributes = ['foedtFoer', 'foedtEtter', 'doedsdato', 'fom', 'tom']

	if (dateAttributes.includes(key)) {
		value = Formatters.formatDate(value)
	} else if (key === 'egenAnsattDatoFom') {
		value = true
	}

	return value
}
