import { connect } from 'react-redux'
import { actions } from '~/ducks/redigertePersoner'
import { withRouter } from 'react-router-dom'
import { PdlfVisning } from '~/components/fagsystem'

const mapStateToProps = (state, ownProps) => {
	// console.log('state: ', state) //TODO - SLETT MEG
	return {
		tmpPersoner: state?.redigertePersoner?.data,
	}
}

const mapDispetchToProps = (dispatch, ownProps) => {
	// console.log('ownProps: ', ownProps) //TODO - SLETT MEG
	return {
		getPdlForvalter: () => dispatch(actions.hentPerson(ownProps.data?.person?.ident)),
	}
}

export default withRouter(connect(mapStateToProps, mapDispetchToProps)(PdlfVisning))
