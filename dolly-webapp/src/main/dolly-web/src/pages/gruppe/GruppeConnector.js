import { connect } from 'react-redux'
import Gruppe from './Gruppe'
import { getGruppe, showCreateOrEditGroup } from '~/ducks/gruppe'

const mapStateToProps = state => ({
	fetching: state.gruppe.fetching,
	gruppe: state.gruppe.data,
	createOrUpdateId: state.gruppe.createOrUpdateId,
	testbrukere: state.testbruker.items,
	testbrukerFetching: state.testbruker.fetching
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getGruppe: () => dispatch(getGruppe(ownProps.match.params.gruppeId)),
	createGroup: () => dispatch(showCreateOrEditGroup(-1))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Gruppe)
