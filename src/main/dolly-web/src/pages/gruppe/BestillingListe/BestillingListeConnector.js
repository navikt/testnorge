import { connect } from 'react-redux'
import BestillingListe from './BestillingListe'
import { sokSelector } from '~/ducks/bestillingStatus'

const mapStateToProps = (state, ownProps) => {
	return {
		searchActive: Boolean(state.search),
		bestillinger: sokSelector(ownProps.bestillingListe.data, state.search)
	}
}

export default connect(mapStateToProps)(BestillingListe)
