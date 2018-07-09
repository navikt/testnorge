import { connect } from 'react-redux'
import Gruppe from './Gruppe'
import { getGrupper } from '~/ducks/grupper'

const mapStateToProps = (state, ownProps) => ({
	fetching: state.grupper.fetching,
	gruppe:
		state.grupper.items &&
		state.grupper.items.find(v => String(v.id) === ownProps.match.params.gruppeId)
})

const mapDispatchToProps = dispatch => ({
	getGrupper: () => dispatch(getGrupper())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Gruppe)
