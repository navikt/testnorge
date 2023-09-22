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
