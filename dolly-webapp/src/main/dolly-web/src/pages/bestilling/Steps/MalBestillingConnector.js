import { connect } from 'react-redux'
import { actions } from '~/ducks/bestilling'
import Malbestilling from './Malbestilling'

const mapStateToProps = state => ({
	maler: state.currentBestilling.maler
	// eksisterendeIdentListe: state.currentBestilling.eksisterendeIdentListe,
	// ugyldigIdentListe: state.currentBestilling.ugyldigIdentListe
})

const mapDispatchToProps = dispatch => ({
	getBestillingMaler: () => {
		dispatch(actions.getBestillingMaler())
	},
	checkAttributeArray: array => {
		dispatch(actions.checkAttributeArray(array))
	},
	setValues: values => {
		dispatch(actions.setValues(values))
	}
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Malbestilling)
