export function mapBestillingData(bestillingData) {
	// TODO: Alex - Split strengen

	// console.log(bestillingData, 'data')
	// const dataTpsfArray = bestillingData.tpsfKriterier
	// 	.substring(1, bestillingData.tpsfKriterier.length - 1)
	// 	.split(',')

	const tpsfKriterier = JSON.parse(bestillingData.tpsfKriterier)

	// console.log(tpsfKriterier, 'objekt')

	if (!bestillingData) return null
	return [
		{
			label: 'Identtype',
			value: tpsfKriterier.identtype
		},
		{
			label: 'Antall',
			value: bestillingData.antallIdenter
		},
		{
			label: 'Sist Oppdatert',
			value: bestillingData.sistOppdatert
		},
		{
			label: 'Språk',
			value: bestillingData.sprakKode
		}
	]
}
