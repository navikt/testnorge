import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import Bestilling from './Bestilling'
import { actions, sendBestilling } from '~/ducks/bestilling'
import { getEnvironments } from '~/ducks/environments'

const mapStateToProps = state => ({
	page: state.bestilling.page,
	attributeIds: state.bestilling.attributeIds,
	environments: state.bestilling.environments,
	antall: state.bestilling.antall,
	identtype: state.bestilling.identtype,
	values: state.bestilling.values
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	...bindActionCreators(actions, dispatch),
	sendBestilling: () => dispatch(sendBestilling(ownProps.match.params.gruppeId)),
	getEnvironments: () => dispatch(getEnvironments())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Bestilling)
