import { connect } from 'react-redux'
import Beskrivelse from './Beskrivelse'
import { updateBeskrivelse, getIdentByIdSelector } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector([updateBeskrivelse])

const mapStateToProps = (state, ownProps) => ({
	isUpdatingBeskrivelse: loadingSelector(state),
	gruppe: getIdentByIdSelector(state, ownProps.ident)
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId, ident } = ownProps
	return {
		updateBeskrivelse: beskrivelse =>
			dispatch(updateBeskrivelse(gruppeId, { ident: ident, beskrivelse: beskrivelse }))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Beskrivelse)
