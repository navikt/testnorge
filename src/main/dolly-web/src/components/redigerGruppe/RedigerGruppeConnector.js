import { connect } from 'react-redux'
import RedigerGruppe from './RedigerGruppe'
import { createGruppe, updateGruppe } from '~/ducks/grupper'
import { closeCreateOrEdit } from '~/ducks/gruppe'

const mapStateToProps = state => ({
	createOrUpdateFetching: state.grupper.createOrUpdateFetching,
	currentUserId: state.bruker.brukerData.navIdent,
	error: state.grupper.error
})

const mapDispatchToProps = dispatch => ({
	createGruppe: nyGruppe => dispatch(createGruppe(nyGruppe)),
	updateGruppe: (id, values) => dispatch(updateGruppe(id, values)),
	onCancel: () => dispatch(closeCreateOrEdit())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerGruppe)
