import Title from '@/components/Title'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { useTenorOversikt } from '@/utils/hooks/useTenorSoek'
import { SoekForm } from '@/pages/tenorSoek/SoekForm'
import { TreffListe } from '@/pages/tenorSoek/resultatVisning/TreffListe'
import { useEffect, useState } from 'react'

const initialState = {
	// request: {},
	personListe: [],
	side: 0,
	seed: null,
}

export default () => {
	const [request, setRequest] = useState({})
	// const [side, setSide] = useState(0)
	// const [seed, setSeed] = useState(null)

	const [state, setState] = useState(initialState)

	// const { response, loading, error, mutate } = useTenorOversikt(request, 10, side, seed)
	const { response, loading, error, mutate } = useTenorOversikt(request, 10, state.side, state.seed)
	const [personListe, setPersonListe] = useState([])

	console.log('state: ', state) //TODO - SLETT MEG
	// console.log('personListe: ', personListe) //TODO - SLETT MEG
	// console.log('seed: ', seed) //TODO - SLETT MEG
	// console.log('side: ', side) //TODO - SLETT MEG
	console.log('request: ', request) //TODO - SLETT MEG
	// console.log('response: ', response) //TODO - SLETT MEG

	// "kjoenn": "Mann"
	// "sivilstand": "EnkeEllerEnkemann"

	useEffect(() => {
		setState(initialState)
	}, [request])

	// useEffect(() => {
	// 	setPersonListe([])
	// 	setSeed(null)
	// 	setSide(0)
	// 	// mutate()
	// }, [request])

	useEffect(() => {
		if (response && state.personListe?.length === 0) {
			// console.log('response 1: ', response) //TODO - SLETT MEG
			setState({
				...state,
				personListe: response?.data?.data?.personer,
				seed: response?.data?.data?.seed,
			})
		} else if (
			state.personListe?.length > 0 &&
			response?.data?.data?.personer?.length > 0 &&
			state.side > 0
		) {
			// console.log('response 2: ', response) //TODO - SLETT MEG
			setState({ ...state, personListe: [...state.personListe, ...response?.data?.data?.personer] })
		}
	}, [response])

	// useEffect(() => {
	// 	if (response && personListe?.length === 0) {
	// 		// console.log('response 1: ', response) //TODO - SLETT MEG
	// 		setPersonListe(response?.data?.data?.personer)
	// 		setSeed(response?.data?.data?.seed)
	// 	} else if (personListe?.length > 0 && response?.data?.data?.personer?.length > 0 && state.side > 0) {
	// 		// console.log('response 2: ', response) //TODO - SLETT MEG
	// 		setPersonListe([...personListe, ...response?.data?.data?.personer])
	// 	}
	// }, [response])

	// useEffect(() => {
	// 	if (response && personListe?.length === 0) {
	// 		console.log('useeffect 1') //TODO - SLETT MEG
	// 		setPersonListe(response?.data?.data?.personer)
	// 		setSeed(response?.data?.data?.seed)
	// 		setSide((side) => side + 1)
	// 	}
	// }, [response])

	// useEffect(() => {
	// 	if (personListe?.length > 0 && response?.data?.data?.personer?.length > 0) {
	// 		console.log('useeffect 2') //TODO - SLETT MEG
	// 		setPersonListe([...personListe, ...response?.data?.data?.personer])
	// 		// mutate()
	// 	}
	// }, [response])

	useEffect(() => {
		// console.log('personListe?.length: ', personListe?.length) //TODO - SLETT MEG
		// console.log('response?.data?.data?.treff: ', response?.data?.data?.treff) //TODO - SLETT MEG
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
		setState((state) => {
			// if (state.personListe?.length < response?.data?.data?.treff) {
			if (state.side < 19) {
				return { ...state, side: state.side + 1 }
			}
			return state
		})
		// setSide((side) => {
		// 	if (side < 19) {
		// 		return side + 1
		// 	}
		// 	return side
		// })
	}

	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Tenor" />
				{/*<Hjelpetekst placement={bottom}>Blablablah</Hjelpetekst>*/}
			</div>
			<SoekForm
				request={request}
				setRequest={setRequest}
				// setPersonListe={setPersonListe}
				// setSeed={setSeed}
				// setSide={setSide}
				mutate={mutate}
			/>
			<TreffListe
				response={response?.data}
				// personListe={personListe}
				personListe={state.personListe}
				loading={loading}
				error={error}
			/>
		</div>
	)
}
