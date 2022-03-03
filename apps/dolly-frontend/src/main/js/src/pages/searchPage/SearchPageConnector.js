import { connect } from 'react-redux'
import SearchPage from './SearchPage'

const mapStateToProps = (state, ownProps) => ({
	brukertype: state.bruker.brukerData.brukertype,
})

export default connect(mapStateToProps)(SearchPage)
