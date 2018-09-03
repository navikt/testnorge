import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import Navigation from './Navigation'
import { abortBestilling } from '~/ducks/bestilling'

const mapStateToProps = state => ({
	currentPage: state.bestilling.page
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	abortBestilling: () => dispatch(abortBestilling(ownProps.match.params.gruppeId))
})

export default withRouter(
	connect(
		mapStateToProps,
		mapDispatchToProps
	)(Navigation)
)
