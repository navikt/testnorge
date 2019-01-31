export function mapBestillingData(bestillingData) {
	if (!bestillingData) return null
	const data = []

	data.push({
		label: 'Antall',
		value: bestillingData.antallIdenter
	})

	// Gamle bestillinger har ikke tpsfKriterie
	if (bestillingData.tpsfKriterier) {
		const tpsfKriterier = bestillingData.tpsfKriterier && JSON.parse(bestillingData.tpsfKriterier)

		data.push(
			{
				label: 'Identtype',
				value: tpsfKriterier.identtype
			},
			{
				label: 'Språk',
				value: tpsfKriterier.sprakKode
			},
			{
				label: 'Statsborgerskap',
				value: tpsfKriterier.statsborgerskap
			}
		)
	}

	// 2 siste elementer å vise
	data.push(
		{
			label: 'Sist Oppdatert',
			value: bestillingData.sistOppdatert
		},
		{
			label: 'Gjenopprett fra',
			value: bestillingData.opprettetFraId && 'Bestilling #' + bestillingData.opprettetFraId
		}
	)

	return data
}
