import Title from '@/components/Title'
import { useTenorOversiktOrganisasjoner } from '@/utils/hooks/useTenorSoek'
import { useEffect, useState } from 'react'
import { TreffListeOrg } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/TreffListeOrg'
import { SoekFormOrg } from '@/pages/organisasjoner/OrganisasjonTenorSoek/SoekFormOrg'

const initialState = {
	organisasjonListe: [],
	side: 0,
	seed: null,
	nesteSide: null,
}

export const OrganisasjonTenorSoekPage = () => {
	const [request, setRequest] = useState({})
	const [state, setState] = useState<any>(initialState)
	const { response, loading, error, mutate } = useTenorOversiktOrganisasjoner(
		request,
		10,
		state.side,
		state.seed,
	)

	useEffect(() => {
		if (response) {
			const organisasjoner = response.data?.data?.organisasjoner || []
			const nextState = {
				...state,
				organisasjonListe:
					state.side > 0 ? [...state.organisasjonListe, ...organisasjoner] : organisasjoner,
				seed: response.data?.data?.seed,
				nesteSide: response.data?.data?.nesteSide,
			}
			setState(nextState)
		}
	}, [response])

	useEffect(() => {
		window.addEventListener('scroll', handleScroll)
		return () => window.removeEventListener('scroll', handleScroll)
	}, [])

	const handleScroll = () => {
		if (
			document.documentElement.scrollHeight - document.documentElement.scrollTop >
			document.documentElement.clientHeight
		) {
			return
		}
		setState((state: any) => {
			if (state.side < 19 && state.nesteSide) {
				return { ...state, side: state.nesteSide }
			}
			return state
		})
	}

	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter organisasjoner i Tenor" />
			</div>
			<SoekFormOrg setRequest={setRequest} mutate={mutate} />
			<TreffListeOrg
				response={response?.data}
				organisasjonListe={state.organisasjonListe}
				loading={loading}
				error={error}
			/>
		</div>
	)
}
