import { connect } from 'react-redux'
import { actions } from '@/ducks/gruppe'
import { createLoadingSelector } from '@/ducks/loading'
import { createErrorMessageSelector } from '@/ducks/errors'
import RedigerGruppe from '@/components/redigerGruppe/RedigerGruppe'

const loadingSelector = createLoadingSelector([actions.createGruppe, actions.updateGruppe])
const errorSelector = createErrorMessageSelector([actions.createGruppe, actions.updateGruppe])

const mapStateToProps = (state) => ({
	createOrUpdateFetching: loadingSelector(state),
	error: errorSelector(state),
})

const mapDispatchToProps = {
	createGruppe: actions.create,
	updateGruppe: actions.update,
}

export default connect(mapStateToProps, mapDispatchToProps)(RedigerGruppe)
