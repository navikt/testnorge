import { connect } from 'react-redux'
import _get from 'lodash/get'
import {
	fetchTpsfTestbrukere,
	actions,
	sokSelector,
	selectTestbrukerListe
} from '~/ducks/fagsystem'
import { createLoadingSelector } from '~/ducks/loading'
import TestbrukerListe from './TestbrukerListe'

const loadingSelector = createLoadingSelector(actions.getTpsf)
const mapStateToProps = (state, ownProps) => ({
	searchActive: Boolean(state.search),
	testbrukerListe: sokSelector(selectTestbrukerListe(state), state.search),
	isFetching: loadingSelector(state)
})

const mapDispatchToProps = { fetchTpsfTestbrukere }

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TestbrukerListe)
