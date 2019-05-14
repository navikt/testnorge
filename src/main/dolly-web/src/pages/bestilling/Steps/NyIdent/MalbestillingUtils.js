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
	return { spesreg: 'svalbard' }
	const dateAttributes = ['foedtFoer', 'foedtEtter', 'doedsdato', 'fom', 'tom']

	let reduxStateValue = {}
	const tpsfKriterier = JSON.parse(mal.tpsfKriterier)

	const tpsfValuesArray = Object.entries(tpsfKriterier)

	tpsfValuesArray.forEach(v => {
		if (v[1]) {
			const key = v[0]
			let value = v[1]
			if (dateAttributes.includes(key)) {
				value = Formatters.formatDate(value)
			} else if (key === 'egenAnsattDatoFom') {
				value = true
			}

			Object.assign(reduxStateValue, {
				[key]: value
			})

			// Partner

			// Barn
		}
	})

	// if (k !== 'identtype' && k !== 'relasjoner' && k !== 'regdato') {
	// 	return k
	// }
	// })

	// Object.assign(reduxStateValue, {
	// 	spesreg: 'svalbard'
	// })

	console.log('reduxStateValue :', reduxStateValue)
	return reduxStateValue
}
