import { connect } from 'react-redux'
import { actions } from '~/ducks/bestilling'
import NyIdent from './NyIdent'

const mapStateToProps = (state, ownProps) => ({
	maler: state.currentBestilling.maler,
	mal: state.currentBestilling.maler.find(m => m.malBestillingNavn === ownProps.malBestillingNavn)

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
)(NyIdent)
