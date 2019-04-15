import { connect } from 'react-redux'
import BestillingListe from './BestillingListe'
import { sokSelector } from '~/ducks/bestillingStatus/utils'
import { getEnvironments } from '~/ducks/environments'

const mapStateToProps = (state, ownProps) => {
	return {
		searchActive: Boolean(state.search),
		bestillinger: sokSelector(ownProps.bestillingListe.data, state.search)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => ({
	getEnvironments: () => dispatch(getEnvironments())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingListe)
