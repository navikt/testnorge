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
		const { isFavorite, addFavorite, removeFavorite } = this.props
		if (isFavorite)
			return <Button title="Fjern fra favoritter" kind="star-filled" onClick={removeFavorite} />

		return <Button title="Legg til som favoritt" kind="star" onClick={addFavorite} />
	}
}

FavoriteButton.propTypes = {}

export default FavoriteButton
