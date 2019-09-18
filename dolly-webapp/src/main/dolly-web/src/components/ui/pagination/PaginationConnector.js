import { connect } from 'react-redux'
import Pagination from './Pagination'

const mapStateToProps = state => ({
	search: state.search
})

export default connect(mapStateToProps)(Pagination)
