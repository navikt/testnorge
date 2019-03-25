import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import Bestilling from './Bestilling'
import { actions, sendBestilling } from '~/ducks/bestilling'
import { getEnvironments } from '~/ducks/environments'

const mapStateToProps = state => ({
	page: state.currentBestilling.page,
	attributeIds: state.currentBestilling.attributeIds,
	environments: state.currentBestilling.environments,
	antall: state.currentBestilling.antall,
	identtype: state.currentBestilling.identtype,
	values: state.currentBestilling.values,
	identOpprettesFra: state.currentBestilling.identOpprettesFra || 'nyIdent',
	eksisterendeIdentListe: state.currentBestilling.eksisterendeIdentListe
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	...bindActionCreators(actions, dispatch),
	sendBestilling: () => dispatch(sendBestilling(ownProps.match.params.gruppeId)),
	getEnvironments: () => dispatch(getEnvironments()),
	setIdentOpprettesFra: prop => dispatch(actions.setIdentOpprettesFra(prop))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Bestilling)
