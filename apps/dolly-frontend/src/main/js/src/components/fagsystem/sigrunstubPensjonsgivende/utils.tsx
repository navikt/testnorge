export const kodeverkKeyToLabel = (key) => {
	if (!key) {
		return null
	}

	const filteredLabel = key?.replace('pensjonsgivendeInntektAv', '')
	const defaultLabel = filteredLabel?.replace(/([A-Z])/g, ' $1')

	switch (key) {
		case 'datoForFastsetting':
			return 'Dato for fastsetting'
		case 'pensjonsgivendeInntektAvLoennsinntekt':
			return 'Lønnsinntekt'
		case 'pensjonsgivendeInntektAvLoennsinntektBarePensjonsdel':
			return 'Lønnsinntekt bare pensjonsdel'
		case 'pensjonsgivendeInntektAvNaeringsinntekt':
			return 'Næringsinntekt'
		case 'pensjonsgivendeInntektAvNaeringsinntektFraFiskeFangstEllerFamiliebarnehage':
			return 'Næringsinntekt fra fiske/fangst/familiebarnehage'
		case 'skatteordning':
			return 'Skatteordning'
		default:
			return defaultLabel
	}
}

export const getInitialInntekt = (kodeverk, skatteordning) => {
	if (!kodeverk) {
		return null
	}

	const initialInntekt = {}

	for (const [key, value] of Object.entries(kodeverk)) {
		if (key === 'skatteordning') {
			initialInntekt[key] = skatteordning?.[0] || ''
		} else if (value === 'String') {
			initialInntekt[key] = ''
		} else if (value === 'Date') {
			initialInntekt[key] = new Date()
		} else initialInntekt[key] = null
	}

	return initialInntekt
}

export const getFieldSize = (label) => {
	if (!label || label.length < 30) {
		return 'medium'
	}
	if (label.length < 40) {
		return 'large'
	}
	if (label.length < 50) {
		return 'xlarge'
	}
	return 'xxlarge'
}
