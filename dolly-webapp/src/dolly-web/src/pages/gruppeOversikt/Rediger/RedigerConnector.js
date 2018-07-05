import { connect } from 'react-redux'
import Rediger from './Rediger'
import { createGruppe, updateGruppe, cancelRedigerOgOpprett } from '~/ducks/grupper'

const mapDispatchToProps = dispatch => ({
	createGruppe: nyGruppe => dispatch(createGruppe(nyGruppe)),
	updateGruppe: (index, gruppe) => dispatch(updateGruppe(index, gruppe)),
	cancelRedigerOgOpprett: () => dispatch(cancelRedigerOgOpprett())
})

export default connect(
	null,
	mapDispatchToProps
)(Rediger)
