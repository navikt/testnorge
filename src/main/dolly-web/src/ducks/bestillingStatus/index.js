import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import Formatters from '~/utils/DataFormatter'
import success from '~/utils/SuccessAction'
import { actions as bestillingActions } from '~/ducks/bestilling'

export const getBestillinger = createAction('GET_BESTILLINGER', async gruppeID => {
	let res = await DollyApi.getBestillinger(gruppeID)
	return res
})

export const removeNyBestillingStatus = createAction('REMOVE_NY_BESTILLING_STATUS')

// ny-array holder oversikt over nye bestillinger i en session
const initialState = { ny: [] }

export const cancelBestilling = createAction('CANCEL_BESTILLING', async id => {
	let res = await DollyApi.cancelBestilling(id)
	return res
})

export const gjenopprettBestilling = createAction('GJENOPPRETT_BESTILLING', async (id, envs) => {
	let res = await DollyApi.gjenopprettBestilling(id, envs)
	return res
})

export default handleActions(
	{
		[success(getBestillinger)](state, action) {
			const { data } = action.payload
			const nyeBestillinger = data.filter(bestilling => {
				if (!bestilling.ferdig) return true
			})
			let idListe = []
			nyeBestillinger.forEach(bestilling => {
				if (!state.ny.find(id => id == bestilling.id)) idListe.push(bestilling.id)
			})
			return {
				...state,
				data,
				ny: idListe.length > 0 ? [...state.ny, ...idListe] : state.ny
			}
		},

		// [success(bestillingActions.postBestilling)](state, action) {
		// 	return { ...state, ny: [...state.ny, action.payload.data.id] }
		// },

		// [success(gjenopprettBestilling)](state, action) {
		// 	return { ...state, ny: [...state.ny, action.payload.data.id] }
		// },

		// [success(cancelBestilling)](state, action) {
		// 	return { ...state, ny: state.ny.filter(id => id !== action.payload.id) }
		// }

		[removeNyBestillingStatus](state, action) {
			return { ...state, ny: state.ny.filter(id => id !== action.payload) }
		}
	},
	initialState
)

// Selector + mapper
export const sokSelector = (items, searchStr) => {
	if (!items) return null
	const mappedItems = mapItems(items)

	if (!searchStr) return mappedItems

	const query = searchStr.toLowerCase()
	return mappedItems.filter(item => {
		const searchValues = [
			_get(item, 'id'),
			_get(item, 'antallIdenter'),
			_get(item, 'sistOppdatert'),
			_get(item, 'environments'),
			_get(item, 'ferdig')
		]
			.filter(v => !_isNil(v))
			.map(v => v.toString().toLowerCase())

		return searchValues.some(v => v.includes(query))
	})
}

// TODO: Flytt saanne medtoder i egen fil. Klassen begynner aa vaere litt stor?
export const getAaregSuccessEnv = bestilling => {
	let envs = []
	bestilling.aaregStatus &&
		bestilling.aaregStatus.length > 0 &&
		bestilling.aaregStatus.forEach(status => {
			if (status.statusMelding === 'OK') {
				envs = Object.keys(status.environmentIdentsForhold)
			}
		})

	return envs
}

const antallIdenterOpprettetFunk = bestilling => {
	let identArray = []
	bestilling.tpsfStatus &&
		bestilling.tpsfStatus.map(status => {
			Object.keys(status.environmentIdents).map(miljo => {
				status.environmentIdents[miljo].map(ident => {
					!identArray.includes(ident) && identArray.push(ident)
				})
			})
		})
	return identArray.length
}

const bestillingIkkeFerdig = item => !item.ferdig

const mapItems = items => {
	if (!items) return null
	return items.map(item => {
		return {
			...item,
			id: item.id.toString(),
			antallIdenter: item.antallIdenter.toString(),
			sistOppdatert: Formatters.formatDate(item.sistOppdatert),
			ferdig: item.stoppet
				? 'Stoppet'
				: bestillingIkkeFerdig(item)
					? 'Pågår'
					: harIkkeIdenter(item)
						? 'Feilet'
						: avvikStatus(item)
							? 'Avvik'
							: 'Ferdig'
		}
	})
}

const avvikStatus = item => {
	let avvik = false
	item.tpsfStatus &&
		item.tpsfStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.aaregStatus &&
		item.aaregStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.krrStubStatus &&
		item.krrStubStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.sigrunStubStatus &&
		item.sigrunStubStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.feil && (avvik = true)
	return avvik
}

const harIkkeIdenter = item => {
	let feilet = true
	item.tpsfStatus && (feilet = false)
	return feilet
}
