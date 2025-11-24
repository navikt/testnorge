import Button from '../Button'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { REGEX_BACKEND_BRUKER, useMatchMutate } from '@/utils/hooks/useMutate'
import { useDispatch } from 'react-redux'
import { addFavorite, removeFavorite } from '@/ducks/bruker'

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
			title={isFavorite ? 'Fjern fra favoritter' : 'Legg til som favoritt'}
			iconSize={hideLabel && 18}
			kind={isFavorite ? 'star-filled' : 'star'}
			onClick={handleClick}
		>
			{!hideLabel && (isFavorite ? 'FJERN FAVORITT' : 'FAVORISER')}
		</Button>
	)
}
