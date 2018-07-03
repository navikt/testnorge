import { connect } from 'react-redux'
import RedigerGruppe from './RedigerGruppe'
import { createGruppe, updateGruppe } from '~/ducks/grupper'

const mapDispatchToProps = dispatch => ({
	createGruppe: nyGruppe => dispatch(createGruppe(nyGruppe)),
	updateGruppe: (index, gruppe) => dispatch(updateGruppe(index, gruppe))
})

export default connect(
	null,
	mapDispatchToProps
)(RedigerGruppe)
