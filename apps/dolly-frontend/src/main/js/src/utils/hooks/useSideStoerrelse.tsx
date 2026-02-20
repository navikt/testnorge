import { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { setSideStoerrelse } from '@/ducks/finnPerson'
import { sideStoerrelseLocalStorageKey } from '@/pages/gruppeOversikt/GruppeOversikt'

type RootState = {
	finnPerson: {
		sideStoerrelse: number
	}
}

export const useSideStoerrelse = () => {
	const dispatch = useDispatch()
	const sideStoerrelse = useSelector((state: RootState) => state.finnPerson.sideStoerrelse)

	useEffect(() => {
		localStorage.setItem(sideStoerrelseLocalStorageKey, sideStoerrelse.toString())
	}, [sideStoerrelse])

	const updateSideStoerrelse = (newSize: number) => {
		dispatch(setSideStoerrelse(newSize))
	}

	return {
		sideStoerrelse,
		updateSideStoerrelse,
	}
}
