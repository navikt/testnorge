import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { REGEX_BACKEND_BRUKER, useMatchMutate } from '@/utils/hooks/useMutate'
import { useDispatch } from 'react-redux'
import { addFavorite, removeFavorite } from '@/ducks/bruker'
import { Button } from '@navikt/ds-react'
import { StarFillIcon, StarIcon } from '@navikt/aksel-icons'
import React from 'react'

export default function FavoriteButton({ hideLabel, groupId }: any) {
	const dispatch = useDispatch()

	const { currentBruker } = useCurrentBruker()

	const mutate = useMatchMutate()

	if (!currentBruker?.favoritter) {
		return null
	}

	const isFavorite = currentBruker?.favoritter.some((fav: any) => fav.id === groupId)

	const handleClick = () =>
		isFavorite
			? dispatch(removeFavorite(groupId)).then(() => mutate(REGEX_BACKEND_BRUKER))
			: dispatch(addFavorite(groupId)).then(() => mutate(REGEX_BACKEND_BRUKER))

	return (
		<Button
			size="xsmall"
			variant="tertiary"
			icon={isFavorite ? <StarFillIcon aria-hidden /> : <StarIcon aria-hidden />}
			title={isFavorite ? 'Fjern fra favoritter' : 'Legg til som favoritt'}
			onClick={handleClick}
		>
			{!hideLabel && (isFavorite ? 'Fjern favoritt' : 'Favoriser')}
		</Button>
	)
}
