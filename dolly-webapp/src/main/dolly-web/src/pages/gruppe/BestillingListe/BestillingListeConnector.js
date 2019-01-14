import { connect } from 'react-redux'
import BestillingListe from './BestillingListe'
import { sokSelector } from '~/ducks/bestillingStatus'

const mapStateToProps = (state, ownProps) => {
	console.log(ownProps.bestillingListe, 'beslist')
	return {
		searchActive: Boolean(state.search),
		bestillinger: sokSelector(ownProps.bestillingListe.data, state.search)
	}
}

export default connect(mapStateToProps)(BestillingListe)
