import { connect } from 'react-redux'
import BestillingListe from './BestillingListe'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import Formatters from '~/utils/DataFormatter'

//TODO: Move to ducks when bestilling-status is written
// Selector + mapper
const sokSelector = (items, searchStr) => {
	if (!items) return null
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item => {
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

const mapStateToProps = (state, ownProps) => ({
	bestillinger: sokSelector(mapItems(ownProps.bestillingListe), state.search)
})

export default connect(mapStateToProps)(BestillingListe)
