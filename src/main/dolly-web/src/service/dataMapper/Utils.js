export const createHeader = (label, width) => {
	return {
		width: width,
		label: label
	}
}

export const relasjonTranslator = relasjon => {
	switch (relasjon) {
		case 'EKTEFELLE':
			return 'Partner'
		case 'MOR':
			return 'Mor'
		case 'FAR':
			return 'Far'
		case 'BARN':
			return 'Barn'
		case 'FOEDSEL':
			return 'Barn'
		default:
			return 'Ukjent relasjon'
	}
}

export function mapBestillingId(testIdent) {
	if (!testIdent) return null
	return {
		header: 'Tidligere bestilling-ID',
		data: [
			{
				id: 'bestillingID',
				value: testIdent.bestillingId.slice(1).join(', ')
			}
		]
	}
}
