import { connect } from 'react-redux'
import Gruppe from './Gruppe'
import { getGrupper } from '~/ducks/grupper'
import { getTpsfBruker } from '~/ducks/testBruker'

const mapStateToProps = (state, ownProps) => ({
	fetching: state.grupper.fetching,
	gruppe:
		state.grupper.items &&
		state.grupper.items.find(v => String(v.id) === ownProps.match.params.gruppeId),
	testbrukere: state.testbruker.items,
	testbrukerFetching: state.testbruker.fetching
})

const mapDispatchToProps = dispatch => ({
	getGrupper: () => dispatch(getGrupper()),
	getTpsfBruker: brukerListe => dispatch(getTpsfBruker(brukerListe))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Gruppe)
