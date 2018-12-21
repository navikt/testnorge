import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Button from '../Button'

class FavoriteButton extends PureComponent {
	static propTypes = {
		isFavorite: PropTypes.bool,
		addFavorite: PropTypes.func,
		removeFavorite: PropTypes.func
	}

	render() {
		const { isFavorite, addFavorite, removeFavorite, className } = this.props
		return (
			<Button
				className="flexbox--align-center"
				title={isFavorite ? 'Fjern fra favoritter' : 'Legg til som favoritt'}
				kind={isFavorite ? 'star-filled' : 'star'}
				onClick={isFavorite ? removeFavorite : addFavorite}
			>
				{isFavorite ? 'FJERN FAVORITT' : 'FAVORISER'}
			</Button>
		)

		// return (
		// 	<Button
		// 		className="flexbox--align-center"
		// 		title="Legg til som favoritt"
		// 		kind="star"
		// 		onClick={addFavorite}
		// 		className={className}
		// 	>
		// 		FAVORISER
		// 	</Button>
		// )
	}
}

FavoriteButton.propTypes = {}

export default FavoriteButton
