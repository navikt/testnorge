import { connect } from 'react-redux'
import BestillingDetaljer from './BestillingDetaljer'
import {
	removeNyBestillingStatus,
	miljoStatusSelector,
	cancelBestilling
} from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'

// const loadingSelector = createLoadingSelector(cancelBestilling)

const mapStateToProps = (state, ownProps) => {
	return {
		miljoeStatusObj: miljoStatusSelector(ownProps.bestilling)
		// isCanceling: loadingSelector(state)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingDetaljer)
