import { connect } from 'react-redux'
import { actions } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import { LeggTilRelasjon } from './LeggTilRelasjon'
import {
	getBestillinger,
	nyeBestillingerSelector,
	removeNyBestillingStatus,
	cancelBestilling
} from '~/ducks/bestillingStatus'

const loadingSelector = createLoadingSelector([actions.updateBeskrivelse])

const mapStateToProps = state => {
	return {
		identer: state.gruppe.ident,
		gruppeId: Object.keys(state.gruppe.byId)[0],
		identInfo: state.fagsystem.tpsf,
		isUpdatingBeskrivelse: loadingSelector(state)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		updateBeskrivelse: actions.updateBeskrivelse,
		getBestillinger: gruppeId => dispatch(getBestillinger(gruppeId))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(LeggTilRelasjon)
