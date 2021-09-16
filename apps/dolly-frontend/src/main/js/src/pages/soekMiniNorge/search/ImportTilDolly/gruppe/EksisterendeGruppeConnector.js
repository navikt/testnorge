import { connect } from 'react-redux'
import _orderBy from 'lodash/orderBy'
import { fetchMineGrupper, loadingGrupper, sokSelectorGruppeOversikt } from '~/ducks/gruppe'
import EksisterendeGruppe from './EksisterendeGruppe'

const mapStateToProps = (state) => ({
	isFetching: loadingGrupper(state),
	gruppeListe: _orderBy(sokSelectorGruppeOversikt(state), 'id', 'desc'),
})

const mapDispatchToProps = {
	fetchMineGrupper,
}

export default connect(mapStateToProps, mapDispatchToProps)(EksisterendeGruppe)
