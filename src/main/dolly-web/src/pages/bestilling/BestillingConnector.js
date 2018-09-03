import { connect } from 'react-redux'
import Bestilling from './Bestilling'
import {
	startBestilling,
	toggleAttribute,
	setValues,
	setValuesAndGoBack,
	setEnvironments,
	setEnvironmentsAndGoBack,
	postBestilling
} from '~/ducks/bestilling'

const mapStateToProps = state => ({
	fetching: state.bestilling.fetching,
	page: state.bestilling.page,
	attributeIds: state.bestilling.attributeIds,
	environments: state.bestilling.environments,
	antall: state.bestilling.antall,
	identtype: state.bestilling.identtype,
	values: state.bestilling.values,
	error: state.bestilling.error
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	startBestilling: (identtype, antall) => dispatch(startBestilling(identtype, antall)),
	toggleAttribute: attributeId => dispatch(toggleAttribute(attributeId)),
	setValues: values => dispatch(setValues(values)),
	setValuesAndGoBack: values => dispatch(setValuesAndGoBack(values)),
	setEnvironments: environmentIds => dispatch(setEnvironments(environmentIds)),
	setEnvironmentsAndGoBack: environmentIds => dispatch(setEnvironmentsAndGoBack(environmentIds)),
	postBestilling: () => dispatch(postBestilling(ownProps.match.params.gruppeId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Bestilling)
