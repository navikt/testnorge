import { connect } from 'react-redux'
import { GjenopprettGruppe } from './GjenopprettGruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'

const mapDispatchToProps = (dispatch, ownProps) => {
	const { id } = ownProps.gruppe
	return {
		getBestillinger: () => dispatch(getBestillinger(id)),
	}
}

export default connect(null, mapDispatchToProps)(GjenopprettGruppe)
