import { connect } from 'react-redux'
import BestillingDetaljer from './BestillingDetaljer'
import { miljoStatusSelector, gjenopprettBestilling } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'

// const loadingSelector = createLoadingSelector(cancelBestilling)

const mapStateToProps = (state, ownProps) => {
	return {
		miljoeStatusObj: miljoStatusSelector(ownProps.bestilling)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const bestillingId = ownProps.bestilling.id
	return {
		onGjenopprettBestilling: () => dispatch(gjenopprettBestilling(bestillingId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingDetaljer)
