import { connect } from 'react-redux'
import GruppeOversikt from './GruppeOversikt'
import { getGrupper } from '~/ducks/grupper'

const mapStateToProps = state => ({
	isFetching: state.grupper.isFetching,
	grupper: state.grupper.items,
	error: state.grupper.error
})

const mapDispatchToProps = dispatch => ({
	getGrupper: () => dispatch(getGrupper())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(GruppeOversikt)
