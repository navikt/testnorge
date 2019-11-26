import { connect } from 'react-redux'
import Beskrivelse from './Beskrivelse'
import { updateBeskrivelse } from '~/ducks/gruppe'

const mapStateToProps = (state, ownProps) => (
	{
	gruppe: state.gruppe.data[0],
	ident: state.gruppe.data[0].identer.find(ident => ident.ident === ownProps.ident),
	beskrivelse: state.gruppe.data[0].identer.find(ident => ident.ident === ownProps.ident).beskrivelse
	}
)

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId, ident } = ownProps;
	console.log('ownProps :', ownProps);
	return {
		updateBeskrivelse: (beskrivelse) => dispatch(updateBeskrivelse(gruppeId, {ident: ident, beskrivelse: beskrivelse}))
		
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Beskrivelse)
