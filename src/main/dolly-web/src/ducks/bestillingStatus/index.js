import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import Formatters from '~/utils/DataFormatter'
import success from '~/utils/SuccessAction'

export const getBestillingStatus = createAction(
	'GET_BESTILLING_STATUS',
	DollyApi.getBestillingStatus
)

const initialState = {}

export default handleActions(
	{
		[success(getBestillingStatus)](state, action) {
			return { ...state, [action.payload.data.id]: action.payload.data }
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

const mapItems = items => {
	if (!items) return null
	return items.map(item => {
		return {
			...item,
			id: item.id.toString(),
			antallIdenter: item.antallIdenter.toString(),
			sistOppdatert: Formatters.formatDate(item.sistOppdatert),
			environments: Formatters.arrayToString(item.environments),
			ferdig: item.ferdig ? 'Ferdig' : 'Pågår'
		}
	})
}
