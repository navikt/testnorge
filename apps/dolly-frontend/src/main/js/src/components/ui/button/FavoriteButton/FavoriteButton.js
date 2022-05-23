import React from 'react'
import Button from '../Button'
import { useCurrentBruker } from '~/utils/hooks/useBruker'

export default function FavoriteButton({ hideLabel, addFavorite, removeFavorite, groupId }) {
	const {
		currentBruker: { favoritter },
	} = useCurrentBruker()

	const isFavorite = favoritter.some((fav) => fav.id === groupId)

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
