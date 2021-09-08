import { connect } from 'react-redux'
import { LeggTilRelasjon } from './LeggTilRelasjon'
import { getBestillinger } from '~/ducks/bestillingStatus'

const mapStateToProps = (state) => {
	return {
		identer: state.gruppe.ident,
		gruppeId: Object.keys(state.gruppe.byId)[0],
		identInfo: state.fagsystem.tpsf,
	}
}

const mapDispatchToProps = (dispatch, ownProps) => ({
	getBestillinger: (gruppeId) => dispatch(getBestillinger(gruppeId)),
})

export default connect(mapStateToProps, mapDispatchToProps)(LeggTilRelasjon)
