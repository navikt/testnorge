import { connect } from 'react-redux'
import Gruppe from './Gruppe'
import { getGrupper } from '~/ducks/grupper'
import { getGruppe } from '~/ducks/gruppe'
import { getTpsfBruker } from '~/ducks/testBruker'

const mapStateToProps = state => ({
	fetching: state.gruppe.fetching,
	gruppe: state.gruppe.data,
	testbrukere: state.testbruker.items,
	testbrukerFetching: state.testbruker.fetching
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getGruppe: () => dispatch(getGruppe(ownProps.match.params.gruppeId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Gruppe)
