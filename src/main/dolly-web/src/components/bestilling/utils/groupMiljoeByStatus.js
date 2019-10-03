import _get from 'lodash/get'
import _intersection from 'lodash/intersection'

export default function groupMiljoByStatus(bestilling) {
	if (!bestilling) return null

	const miljoListe = arr => arr.map(a => a.miljo)

	// For hvert fagsystem (id / navn)
	const grouped = bestilling.status.reduce(
		(acc, curr) => {
			// For hver av fagsystemets statuser
			curr.statuser.forEach(status => {
				const miljo = status.identer ? [curr.navn] : miljoListe(status.detaljert)

				if (status.melding === 'OK') {
					acc.success = acc.success.concat(miljo)
				} else {
					acc.failed = acc.failed.concat(miljo)
				}
			})

			return acc
		},
		{
			success: [],
			failed: []
		}
	)

	// Unique values
	const success = [...new Set(grouped.success)]
	const failed = [...new Set(grouped.failed)]

	// Fjern duplikater, og legg de i et eget Avvik-array
	const avvik = _intersection(success, failed)

	return {
		successEnvs: success.filter(o => !avvik.includes(o)),
		failedEnvs: failed.filter(o => !avvik.includes(o)),
		avvikEnvs: avvik
	}
}
