import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import Bestilling from './Bestilling'
import { actions, sendBestilling } from '~/ducks/bestilling'

const mapStateToProps = state => {
	return {
		page: state.currentBestilling.page,
		attributeIds: state.currentBestilling.attributeIds,
		environments: state.currentBestilling.environments,
		antall: state.currentBestilling.antall,
		identtype: state.currentBestilling.identtype,
		values: state.currentBestilling.values,
		identOpprettesFra: state.currentBestilling.identOpprettesFra,
		eksisterendeIdentListe: state.currentBestilling.eksisterendeIdentListe,
		maler: state.currentBestilling.maler,
		currentMal: state.currentBestilling.currentMal
	}
}

const mapDispatchToProps = (dispatch, ownProps) => ({
	...bindActionCreators(actions, dispatch),
	sendBestilling: () => dispatch(sendBestilling(ownProps.match.params.gruppeId)),
	setIdentOpprettesFra: identOpprettesFra =>
		dispatch(actions.setIdentOpprettesFra(identOpprettesFra))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Bestilling)
