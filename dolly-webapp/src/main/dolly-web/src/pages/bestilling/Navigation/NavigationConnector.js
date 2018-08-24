import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import Navigation from './Navigation'
import { nextPage, prevPage, abortBestilling } from '~/ducks/bestilling'

const mapStateToProps = state => ({
	currentPage: state.bestilling.page
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	nextPage: () => dispatch(nextPage()),
	prevPage: () => dispatch(prevPage()),
	abortBestilling: () => dispatch(abortBestilling(ownProps.match.params.gruppeId))
})

export default withRouter(
	connect(
		mapStateToProps,
		mapDispatchToProps
	)(Navigation)
)
