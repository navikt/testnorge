import { connect } from 'react-redux'
import { setSearchText } from '~/ducks/search'
import SearchField from './SearchField'

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
