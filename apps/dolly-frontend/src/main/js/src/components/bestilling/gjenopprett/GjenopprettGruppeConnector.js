import { connect } from 'react-redux'
import { GjenopprettGruppe } from './GjenopprettGruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'

const mapStateToProps = (state) => ({
	brukertype: state.bruker.brukerData.brukertype,
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { id } = ownProps.gruppe
	return {
		getBestillinger: () => dispatch(getBestillinger(id)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(GjenopprettGruppe)
