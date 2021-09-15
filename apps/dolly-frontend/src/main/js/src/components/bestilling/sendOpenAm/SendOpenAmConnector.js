import { connect } from 'react-redux'
import SendOpenAm from './SendOpenAm'
import { getBestillinger } from '~/ducks/bestillingStatus'

const mapDispatchToProps = (dispatch, ownProps) => ({
	getBestillinger: () => dispatch(getBestillinger(ownProps.bestilling.gruppeId)),
})

export default connect(null, mapDispatchToProps)(SendOpenAm)
