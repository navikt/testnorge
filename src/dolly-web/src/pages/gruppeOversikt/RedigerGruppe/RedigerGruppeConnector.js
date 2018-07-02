import { connect } from 'react-redux'
import RedigerGruppe from './RedigerGruppe'
import { createGruppe } from '~/ducks/grupper'

const mapDispatchToProps = dispatch => ({
	createGruppe: nyGruppe => dispatch(createGruppe(nyGruppe))
})

export default connect(
	null,
	mapDispatchToProps
)(RedigerGruppe)
