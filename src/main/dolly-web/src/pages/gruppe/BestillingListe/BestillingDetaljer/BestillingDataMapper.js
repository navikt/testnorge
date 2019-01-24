export function mapBestillingData(bestillingData) {
	// TODO: Alex - Split strengen

	console.log(bestillingData, 'data')
	console.log(bestillingData.antallIdenter, 'antall')
	const dataTpsfArray = bestillingData.tpsfKriterier
		.substring(1, bestillingData.tpsfKriterier.length - 1)
		.split(',')

	console.log(dataTpsfArray)

	if (!bestillingData) return null
	return {
		data: [
			{
				label: 'Språk',
				value: bestillingData.sprakKode
			}
		]
	}
}
