import { connect } from 'react-redux'
import UtenFastBopel from './UtenFastBopel'
import { actions } from '~/ducks/bestilling'

const mapStateToProps = state => ({
	attributeIds: state.currentBestilling.attributeIds,
	values: state.currentBestilling.values
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	toggleAttribute: attributeId => dispatch(actions.toggleAttribute(attributeId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(UtenFastBopel)
