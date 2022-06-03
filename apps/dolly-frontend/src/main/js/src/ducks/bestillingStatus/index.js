import { createActions } from 'redux-actions'
import _isNil from 'lodash/isNil'
import _mapValues from 'lodash/mapValues'
import _uniq from 'lodash/uniq'
import { DollyApi } from '~/service/Api'

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
	{ prefix: 'bestillingStatus' }
)

// Henter alle bestillinger som er gjort på en ident
export const getBestillingsListe = (bestillinger, IDer) => {
	const bestillingsListe = []
	for (let id of IDer) {
		const bestilling = {
			data: bestillinger[id].bestilling,
			id: id,
			erGjenopprettet: bestillinger[id].hasOwnProperty('opprettetFraId'),
		}
		const suksessMiljoer = successMiljoSelector(bestillinger[id].status)
		// Arena-bestillinger brukes i personvisning, skal derfor ikke returnere Arena-bestillinger som har feilet
		if (!bestilling.hasOwnProperty('arenaforvalter') || suksessMiljoer.hasOwnProperty('ARENA')) {
			bestillingsListe.push(bestilling)
		}
	}
	return bestillingsListe
}

// Filtrer bestillinger basert på søkestreng
export const sokSelector = (bestillingerById, searchStr) => {
	const items = Object.values(bestillingerById)
	if (!searchStr || !items) return items

	return items.filter(({ listedata }) => {
		const searchValues = listedata.filter((v) => !_isNil(v)).map((v) => v.toString().toLowerCase())
		return searchValues.some((v) => v.includes(searchStr.toLowerCase()))
	})
}

// Object med system som key, og OK-miljøer som value
export const successMiljoSelector = (statusArray) => {
	const success_list = statusArray
		.filter((curr) => !_isNil(curr))
		.reduce((acc, curr) => {
			const statuser = curr.statuser.filter((v) => v.melding === 'OK')

			if (statuser.length) {
				// Dette er statuser som er OK
				const detaljert = statuser[0].detaljert
				const envs = detaljert && detaljert.map((v) => v.miljo)

				if (acc[curr.id]) {
					acc[curr.id] = acc[curr.id].concat(envs)
				} else {
					acc[curr.id] = envs
				}
			}

			return acc
		}, {})

	// Filtrer og sorter miljøer
	return _mapValues(success_list, (v) => _uniq(v).sort())
}
