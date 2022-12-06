import { connect } from 'react-redux'
import { addFavorite, removeFavorite } from '@/ducks/bruker'
import FavoriteButton from './FavoriteButton'

const mapDispatchToProps = (dispatch, ownProps) => ({
	addFavorite: () => dispatch(addFavorite(ownProps.groupId)),
	removeFavorite: () => dispatch(removeFavorite(ownProps.groupId)),
})

export default connect(null, mapDispatchToProps)(FavoriteButton)
