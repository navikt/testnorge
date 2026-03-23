import { useCallback, useEffect } from 'react'
import { useNavigate } from 'react-router'
import { DollyApi } from '@/service/Api'
import { Option } from '@/service/SelectOptionsOppslag'
import { useReduxDispatch, useReduxSelector } from '@/utils/hooks/useRedux'
import { setGruppeNavigerTil } from '@/ducks/finnPerson'
import { SoekTypeValg, GroupedOption } from './NavigeringTypes'

type ResponsGruppe = {
	data: [
		{
			navn: string
			id: number
		},
	]
}

export const soekGrupper = async (tekst: string): Promise<Option[]> => {
	if (!tekst) {
		return []
	}
	return DollyApi.getGrupperFragment(tekst).then((response: ResponsGruppe) => {
		if (!response?.data || response?.data?.length < 1) {
			return []
		}
		return response.data?.map((resp) => ({
			value: resp.id,
			label: `Gruppe ${resp.id} - ${resp.navn}`,
		}))
	})
}

export const useGruppeSearch = () => {
	const dispatch = useReduxDispatch()
	const navigate = useNavigate()
	const gruppe = useReduxSelector((state) => state.finnPerson.navigerTilGruppe)

	useEffect(() => {
		if (gruppe && !window.location.pathname.includes(`/${gruppe}`)) {
			navigate(`/gruppe/${gruppe}`, { replace: true })
		}
	}, [gruppe, navigate])

	const search = useCallback(async (tekst: string): Promise<GroupedOption> => {
		const grupper = await soekGrupper(tekst)
		return {
			label: 'Grupper',
			options:
				grupper?.map((g) => ({
					...g,
					value: String(g.value),
					type: SoekTypeValg.GRUPPE,
				})) ?? [],
		}
	}, [])

	const handleSelect = useCallback(
		(value: string) => {
			dispatch(setGruppeNavigerTil(value))
		},
		[dispatch],
	)

	return { search, handleSelect }
}
