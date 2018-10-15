import { connect } from 'react-redux'
import BestillingListe from './BestillingListe'
import { sokSelector } from '~/ducks/bestillingStatus'

const mapStateToProps = (state, ownProps) => ({
	bestillinger: sokSelector(ownProps.bestillingListe, state.search)
})

export default connect(mapStateToProps)(BestillingListe)
