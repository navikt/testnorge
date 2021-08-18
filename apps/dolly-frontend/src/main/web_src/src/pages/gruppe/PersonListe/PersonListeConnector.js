import { connect } from 'react-redux'
import {
	actions as actionList,
	fetchTpsfPersoner,
	selectPersonListe,
	sokSelector
} from '~/ducks/fagsystem'
import { actions } from '~/ducks/gruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import personListe from './PersonListe'

const loadingSelector = createLoadingSelector([
	actions.getById,
	actionList.getTpsf,
	getBestillinger
])
const mapStateToProps = (state, ownProps) => {
	return {
		personListe: sokSelector(selectPersonListe(state), state.search),
		gruppeInfo: state.gruppe.gruppeInfo,
		identer: state.gruppe.ident,
		isFetching: loadingSelector(state),
		visPerson: state.finnPerson.visPerson
	}
}

const mapDispatchToProps = { fetchTpsfPersoner }

export default connect(mapStateToProps, mapDispatchToProps)(personListe)
