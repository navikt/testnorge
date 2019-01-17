import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import Navigation from './Navigation'
import { actions } from '~/ducks/bestilling'

const mapStateToProps = state => ({
	currentPage: state.currentBestilling.page
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	abortBestilling: () => dispatch(actions.abortBestilling(ownProps.match.params.gruppeId))
})

export default withRouter(
	connect(
		mapStateToProps,
		mapDispatchToProps
	)(Navigation)
)
