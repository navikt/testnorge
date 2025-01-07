import { createActions } from 'redux-actions'
import * as _ from 'lodash-es'
import { DollyApi } from '@/service/Api'

export const {
	cancelBestilling,
	gjenopprettBestilling,
	gjenopprettOrganisasjonBestilling,
	removeNyBestillingStatus,
} = createActions(
	{
		cancelBestilling: DollyApi.cancelBestilling,
		gjenopprettBestilling: DollyApi.gjenopprettBestilling,
		gjenopprettOrganisasjonBestilling: DollyApi.gjenopprettOrganisasjonBestilling,
	},
	'removeNyBestillingStatus',
	{ prefix: 'bestillingStatus' },
)

// Henter alle bestillinger som er gjort på en ident
export const getBestillingsListe = (bestillinger, IDer) => {
	const bestillingsListe = []
	for (let id of IDer) {
		const bestilling = {
			data: bestillinger[id]?.bestilling,
			id: id,
			erGjenopprettet: bestillinger[id]?.hasOwnProperty('opprettetFraId'),
			status: bestillinger[id]?.status,
		}
		const suksessMiljoer = successMiljoSelector(bestillinger[id]?.status)
		// Arena-bestillinger brukes i personvisning, skal derfor ikke returnere Arena-bestillinger som har feilet
		if (!bestilling?.hasOwnProperty('arenaforvalter') || suksessMiljoer?.hasOwnProperty('ARENA')) {
			bestillingsListe.push(bestilling)
		}
	}
	return bestillingsListe
}

// Filtrer bestillinger basert på søkestreng
export const sokSelector = (bestillingerById: any, searchStr: string) => {
	const items = Object.values(bestillingerById)
	if (_.isEmpty(searchStr) || _.isEmpty(items)) return items

	return items.filter((item: any) => {
		return item.id?.toString().includes(searchStr) || item.organisasjonNummer?.includes(searchStr)
	})
}

// Object med system som key, og OK-miljøer som value
export const successMiljoSelector = (statusArray, ident = null) => {
	const success_list = statusArray
		?.filter((curr) => !_.isNil(curr))
		?.reduce((acc, curr) => {
			const statuser = curr.statuser.filter((v) => v.melding === 'OK')

			if (statuser.length) {
				// Dette er statuser som er OK
				const detaljert = statuser[0].detaljert
				const envs = detaljert && detaljert.map((v) => v.miljo)
				const identer = detaljert ? detaljert.flatMap((v) => v.identer) : statuser?.[0]?.identer

				if (!ident || identer?.includes(ident)) {
					if (acc[curr.id]) {
						acc[curr.id] = acc[curr.id].concat(envs)
					} else {
						acc[curr.id] = envs
					}
				}
			}

			return acc
		}, {})

	// Filtrer og sorter miljøer
	return _.mapValues(success_list, (v) => _.uniq(v).sort())
}
