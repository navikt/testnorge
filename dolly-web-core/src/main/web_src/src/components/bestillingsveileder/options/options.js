const TYPE = Object.freeze({
	NY_BESTILLING: 'NY_BESTILLING',
	NY_BESTILLING_FRA_MAL: 'NY_BESTILLING_FRA_MAL',
	OPPRETT_FRA_IDENTER: 'OPPRETT_FRA_IDENTER',
	LEGG_TIL: 'LEGG_TIL'
})

export const BVOptions = ({
	antall = 1,
	identtype = 'FNR',
	mal,
	opprettFraIdenter,
	leggTilPaaFnr,
	data
} = {}) => {
	let initialValues = {
		antall,
		environments: []
	}

	let bestType = TYPE.NY_BESTILLING

	if (mal) {
		bestType = TYPE.NY_BESTILLING_FRA_MAL
		initialValues = Object.assign(initialValues, mal.bestilling)
	}

	if (opprettFraIdenter) {
		bestType = TYPE.OPPRETT_FRA_IDENTER
		initialValues.antall = opprettFraIdenter.length
	}

	if (leggTilPaaFnr) {
		bestType = TYPE.LEGG_TIL
	}

	return {
		initialValues,
		antall,
		identtype,
		mal,
		opprettFraIdenter,
		leggTilPaaFnr,
		data,
		is: {
			nyBestilling: bestType === TYPE.NY_BESTILLING,
			nyBestillingFraMal: bestType === TYPE.NY_BESTILLING_FRA_MAL,
			opprettFraIdenter: bestType === TYPE.OPPRETT_FRA_IDENTER,
			leggTil: bestType === TYPE.LEGG_TIL
		}
	}
}
