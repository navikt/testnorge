import React from 'react'
import Button from '../Button'

export default function FavoriteButton({ isFavorite, hideLabel, addFavorite, removeFavorite }) {
	return (
		<Button
			title={isFavorite ? 'Fjern fra favoritter' : 'Legg til som favoritt'}
			iconSize={hideLabel && 18}
			kind={isFavorite ? 'star-filled' : 'star'}
			onClick={isFavorite ? removeFavorite : addFavorite}
		>
			{!hideLabel && (isFavorite ? 'FJERN FAVORITT' : 'FAVORISER')}
		</Button>
	)
}
