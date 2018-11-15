import { connect } from 'react-redux'
import Gruppe from './Gruppe'
import { getBestillingStatus } from '~/ducks/gruppe'

const loadingSelector = createLoadingSelector(getGruppe)

const mapStateToProps = state => ({
	isFetching: loadingSelector(state),
	gruppeArray: state.gruppe.data,
	createOrUpdateId: state.gruppe.createOrUpdateId
})

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		getBestillingStatus: () => dispatch(getGruppe(getBestillingStatus))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingStatus)
