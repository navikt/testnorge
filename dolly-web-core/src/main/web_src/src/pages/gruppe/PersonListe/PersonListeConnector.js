import { connect } from 'react-redux'
import _get from 'lodash/get'
import { fetchTpsfPersoner, actions, sokSelector, selectPersonListe } from '~/ducks/fagsystem'
import { createLoadingSelector } from '~/ducks/loading'
import personListe from './PersonListe'

const loadingSelector = createLoadingSelector(actions.getTpsf)
const mapStateToProps = (state, ownProps) => ({
	searchActive: Boolean(state.search),
	personListe: sokSelector(selectPersonListe(state), state.search),
	isFetching: loadingSelector(state)
})

const mapDispatchToProps = { fetchTpsfPersoner }

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(personListe)
