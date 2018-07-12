import { connect } from 'react-redux'
import Rediger from './Rediger'
import { createGruppe, updateGruppe, closeRedigerOgOpprett } from '~/ducks/grupper'

const mapStateToProps = state => ({
	currentUserId: state.bruker.brukerData.navIdent
})

const mapDispatchToProps = dispatch => ({
	createGruppe: nyGruppe => dispatch(createGruppe(nyGruppe)),
	updateGruppe: (id, values) => dispatch(updateGruppe(id, values)),
	closeRedigerOgOpprett: () => dispatch(closeRedigerOgOpprett())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Rediger)
