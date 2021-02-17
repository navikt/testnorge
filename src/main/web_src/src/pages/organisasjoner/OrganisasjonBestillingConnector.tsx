import { connect } from 'react-redux'
import { sokSelector } from '~/ducks/bestillingStatus'
import OrganisasjonBestilling from './OrganisasjonBestilling'

const mapStateToProps = state => ({
	bestillinger: sokSelector(state, state.search)
})

export default connect(mapStateToProps)(OrganisasjonBestilling)
