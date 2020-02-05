import { connect } from 'react-redux'
import FavoriteButton from './FavoriteButton'
import { addFavorite, removeFavorite } from '~/ducks/bruker'

const mapStateToProps = (state, ownProps) => ({
	isFavorite: state.bruker.brukerData.favoritter.some(fav => fav.id === ownProps.groupId)
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	addFavorite: () => dispatch(addFavorite(ownProps.groupId)),
	removeFavorite: () => dispatch(removeFavorite(ownProps.groupId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(FavoriteButton)
