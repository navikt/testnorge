import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Button from '../Button'

class FavoriteButton extends PureComponent {
	static propTypes = {
		isFavorite: PropTypes.bool,
		addFavorite: PropTypes.func,
		removeFavorite: PropTypes.func
	}

	state = {
		isHovering: false
	}

	render() {
		const { isFavorite, hideLabel, addFavorite, removeFavorite, className } = this.props
		const { isHovering } = this.state

		return (
			<Button
				className="flexbox--align-center"
				title={isFavorite ? 'Fjern fra favoritter' : 'Legg til som favoritt'}
				kind={isFavorite ? 'star-filled' : 'star'}
				onClick={isFavorite ? removeFavorite : addFavorite}
				onMouseEnter={this._handleOnMouseHover}
			>
				{hideLabel ? (
					isHovering && <p>{isFavorite ? 'FJERN FAVORITT' : 'FAVORISER'}</p>
				) : (
					<p>{isFavorite ? 'FJERN FAVORITT' : 'FAVORISER'}</p>
				)}
			</Button>
		)
	}

	_handleOnMouseHover = () => {
		this.setState({ isHovering: !this.state.isHovering })
	}
}

FavoriteButton.propTypes = {}

export default FavoriteButton
