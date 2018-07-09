import { connect } from 'react-redux'
import Rediger from './Rediger'
import { createGruppe, updateGruppe, cancelRedigerOgOpprett } from '~/ducks/grupper'

const mapStateToProps = state => ({
	currentUserId: state.bruker.brukerData.navIdent
})

const mapDispatchToProps = dispatch => ({
	createGruppe: nyGruppe => dispatch(createGruppe(nyGruppe)),
	updateGruppe: (index, gruppe) => dispatch(updateGruppe(index, gruppe)),
	cancelRedigerOgOpprett: () => dispatch(cancelRedigerOgOpprett())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Rediger)
