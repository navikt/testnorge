import { connect } from 'react-redux'
import { updateBeskrivelse, getIdentByIdSelector } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import { Beskrivelse } from './Beskrivelse'

const loadingSelector = createLoadingSelector([updateBeskrivelse])

const mapStateToProps = (state, ownProps) => ({
	isUpdatingBeskrivelse: loadingSelector(state),
	beskrivelse: getIdentByIdSelector(state, ownProps.ident).beskrivelse
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
