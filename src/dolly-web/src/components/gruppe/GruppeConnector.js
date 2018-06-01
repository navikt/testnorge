import { connect } from 'react-redux'
import { updateGruppeSuccess } from '~/ducks/grupper'
import Gruppe from './Gruppe'

const mapStateToProps = state => ({
	grupperState: state.gruppe
})

const mapDispatchToProps = dispatch => ({
	updateGruppeSuccess: respons => dispatch(updateGruppeSuccess(respons))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Gruppe)
