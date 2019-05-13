// disse er ikke attributtene, de
// const _excludeListe =

export const getAttributesFromMal = mal => {
	console.log('mal :', mal)
	const tpsfKriterier = JSON.parse(mal.tpsfKriterier)
	const bestKriterier = JSON.parse(mal.bestKriterier)

	let identtype = ''
	console.log(tpsfKriterier)
	console.log(bestKriterier)
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
			case 'krrStub':
				attrArray.push('krr')
			default:
				break
		}
	})

	console.log('attrArray :', attrArray)
	return attrArray
}

export const getValuesFromMal = (tpsfKriterier, bestKriterie) => {
	let reduxStateValue = []
	console.log('get Values from Mal')
}
