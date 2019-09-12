import { connect } from 'react-redux'
import SearchField from './SearchField'
import { setSearchText } from '~/ducks/search'

const mapStateToProps = state => ({
	searchText: state.search
})

const mapDispatchToProps = dispatch => ({
	setSearchText: text => dispatch(setSearchText(text))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SearchField)
