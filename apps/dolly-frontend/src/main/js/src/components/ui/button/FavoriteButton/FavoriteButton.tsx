import React from 'react'
import Button from '../Button'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { REGEX_BACKEND_BRUKER, useMatchMutate } from '~/utils/hooks/useMutate'

function mutateUserOptimisticly(mutate, response) {
	return mutate(REGEX_BACKEND_BRUKER, response)
}

export default function FavoriteButton({ hideLabel, addFavorite, removeFavorite, groupId }) {
	const {
		currentBruker: { favoritter },
	} = useCurrentBruker()

	const mutate = useMatchMutate()

	if (!favoritter) {
		return null
	}

	const isFavorite = favoritter.some((fav) => fav.id === groupId)

	const handleClick = () =>
		isFavorite
			? removeFavorite(groupId).then((resp) => mutateUserOptimisticly(mutate, resp?.value?.data))
			: addFavorite(groupId).then((resp) => mutateUserOptimisticly(mutate, resp?.value?.data))

	return (
		<Button
			title={isFavorite ? 'Fjern fra favoritter' : 'Legg til som favoritt'}
			iconSize={hideLabel && 18}
			kind={isFavorite ? 'star-filled' : 'star'}
			onClick={handleClick}
		>
			{!hideLabel && (isFavorite ? 'FJERN FAVORITT' : 'FAVORISER')}
		</Button>
	)
}
