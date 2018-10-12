import { connect } from 'react-redux'
import BestillingOversikt from './BestillingOversikt'
import { getBestillingStatus } from '~/ducks/bestillingStatus'

const mapStateToProps = (state, ownProps) => ({})

const mapDispatchToProps = (dispatch, ownProps) => ({})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingOversikt)
