import { connect } from 'react-redux'
import Beskrivelse from './Beskrivelse'
import { updateBeskrivelse, getIdentByIdSelector } from '~/ducks/gruppe'

const mapStateToProps = (state, ownProps) => ({
	gruppe: getIdentByIdSelector(state, ownProps.ident)
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId, ident } = ownProps
	console.log('ownProps :', ownProps)
	return {
		updateBeskrivelse: beskrivelse =>
			dispatch(updateBeskrivelse(gruppeId, { ident: ident, beskrivelse: beskrivelse }))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Beskrivelse)
