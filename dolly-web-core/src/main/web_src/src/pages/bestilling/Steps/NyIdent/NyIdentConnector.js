import { connect } from 'react-redux'
import { actions } from '~/ducks/bestilling'
import NyIdent from './NyIdent'

const mapStateToProps = (state, ownProps) => ({
	maler: state.currentBestilling.maler,
	mal: state.currentBestilling.maler.find(m => m.malBestillingNavn === ownProps.malBestillingNavn)
})

const mapDispatchToProps = dispatch => ({
	getBestillingMaler: () => {
		dispatch(actions.getBestillingMaler())
	},
	setBestillingFraMal: payload => {
		dispatch(actions.setBestillingFraMal(payload))
	},
	setValues: values => {
		dispatch(actions.setValues(values))
	}
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(NyIdent)
