import { connect } from 'react-redux'
import { actions } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import { Beskrivelse } from './Beskrivelse'

const loadingSelector = createLoadingSelector([actions.updateBeskrivelse])

const mapStateToProps = state => ({
	isUpdatingBeskrivelse: loadingSelector(state)
})

const mapDispatchToProps = { updateBeskrivelse: actions.updateBeskrivelse }

export default connect(mapStateToProps, mapDispatchToProps)(Beskrivelse)
