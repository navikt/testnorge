import { connect } from 'react-redux'
import { actions } from '~/ducks/bestilling'
import EksisterendeIdent from './EksisterendeIdent'

const mapStateToProps = state => ({
	eksisterendeIdentListe: state.currentBestilling.eksisterendeIdentListe,
	ugyldigIdentListe: state.currentBestilling.ugyldigIdentListe
})

const mapDispatchToProps = dispatch => ({
	setIdentLister: identLister => {
		dispatch(actions.setIdentLister(identLister))
	}
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(EksisterendeIdent)
