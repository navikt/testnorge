import _get from 'lodash/get'
import _intersection from 'lodash/intersection'

const succcesFailedAvvik = bestilling => {
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

	// Fjern duplikater, og legg de i et eget Avvik-array
	const avvik = _intersection(grouped.success, grouped.failed)

	return {
		avvik,
		success: grouped.success.filter(o => !avvik.includes(o)),
		failed: grouped.failed.filter(o => !avvik.includes(o))
	}
}

const miljoeStatusSelector = bestilling => {
	if (!bestilling) return null

	const temp = succcesFailedAvvik(bestilling)

	const obj = {
		successEnvs: temp.success,
		failedEnvs: temp.failed,
		avvikEnvs: temp.avvik
	}

	console.log(obj)

	return obj
}

export default miljoeStatusSelector
