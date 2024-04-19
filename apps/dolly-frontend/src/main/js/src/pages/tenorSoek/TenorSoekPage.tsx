import Title from '@/components/Title'
import { useTenorOversikt } from '@/utils/hooks/useTenorSoek'
import { SoekForm } from '@/pages/tenorSoek/SoekForm'
import { TreffListe } from '@/pages/tenorSoek/resultatVisning/TreffListe'
import { useEffect, useState } from 'react'

const initialState = {
	personListe: [],
	side: 0,
	seed: null,
	nesteSide: null,
}

export default () => {
	const [request, setRequest] = useState({})
	const [state, setState] = useState<any>(initialState)
	const { response, loading, error, mutate } = useTenorOversikt(request, 10, state.side, state.seed)
	const [markertePersoner, setMarkertePersoner] = useState([])

	useEffect(() => {
		setState(initialState)
	}, [request])

	useEffect(() => {
		if (response?.data?.data?.personer?.length === 0) {
			setState({
				...state,
				personListe: [],
				nesteSide: response?.data?.data?.nesteSide,
			})
		} else if (response && state.personListe?.length === 0) {
			setState({
				...state,
				personListe: response?.data?.data?.personer,
				seed: response?.data?.data?.seed,
				nesteSide: response?.data?.data?.nesteSide,
			})
		} else if (state.personListe?.length > 0 && response?.data?.data?.personer?.length > 0) {
			if (state.side > 0) {
				setState({
					...state,
					personListe: [...state.personListe, ...response?.data?.data?.personer],
					nesteSide: response?.data?.data?.nesteSide,
				})
			} else {
				setState({
					...state,
					personListe: response?.data?.data?.personer,
					nesteSide: response?.data?.data?.nesteSide,
				})
			}
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
				<Title title="Søk etter personer i Tenor" />
			</div>
			<SoekForm setRequest={setRequest} setMarkertePersoner={setMarkertePersoner} mutate={mutate} />
			<TreffListe
				response={response?.data}
				personListe={state.personListe}
				markertePersoner={markertePersoner}
				setMarkertePersoner={setMarkertePersoner}
				loading={loading}
				error={error}
			/>
		</div>
	)
}
