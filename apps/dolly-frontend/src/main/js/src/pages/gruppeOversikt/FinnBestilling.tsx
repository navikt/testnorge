import { useCallback } from 'react'
import { DollyApi } from '@/service/Api'
import { Option } from '@/service/SelectOptionsOppslag'
import { useReduxDispatch } from '@/utils/hooks/useRedux'
import { navigerTilBestilling } from '@/ducks/finnPerson'
import { SoekTypeValg, GroupedOption } from './NavigeringTypes'

type ResponsBestilling = {
	data: [
		{
			navn: string
			id: number
		},
	]
}

export const soekBestillinger = async (tekst: string): Promise<Option[]> => {
	if (!tekst) {
		return []
	}
	return DollyApi.getBestillingerFragment(tekst.replaceAll('#', '')).then(
		(response: ResponsBestilling) => {
			if (!response?.data || response?.data?.length < 1) {
				return []
			}
			return response.data?.map((resp) => ({
				value: resp.id,
				label: `#${resp.id} - ${resp.navn}`,
			}))
		},
	)
}

export const useBestillingSearch = () => {
	const dispatch = useReduxDispatch()

	const search = useCallback(async (tekst: string): Promise<GroupedOption> => {
		const bestillinger = await soekBestillinger(tekst)
		return {
			label: 'Bestillinger',
			options:
				bestillinger?.map((bestilling) => ({
					...bestilling,
					value: String(bestilling.value),
					type: SoekTypeValg.BESTILLING,
				})) ?? [],
		}
	}, [])

	const handleSelect = useCallback(
		(value: string) => {
			dispatch(navigerTilBestilling(value))
		},
		[dispatch],
	)

	return { search, handleSelect }
}
