import Title from '@/components/Title'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { useTenorOversikt } from '@/utils/hooks/useTenorSoek'
import { SoekForm } from '@/pages/tenorSoek/SoekForm'
import { TreffListe } from '@/pages/tenorSoek/resultatVisning/TreffListe'
import { useEffect, useState } from 'react'

export default () => {
	const [request, setRequest] = useState({}) // Evt. bruk null for aa ikke hente data ved oppstart
	const [antall, setAntall] = useState(10)
	const [side, setSide] = useState(0)
	const [seed, setSeed] = useState(null)
	const { response, loading, error, mutate } = useTenorOversikt(request, antall, side, seed)

	// const [tmpPersoner, setTmpPersoner] = useState(response?.data?.data?.personer || null)
	const [personListe, setPersonListe] = useState([])

	//TODO lag personliste-hook og slå sammen lister i treffliste

	// const [responseListe, setResponseListe] = useState([response?.data])
	// console.log('request: ', request) //TODO - SLETT MEG

	// const personListe = response?.data?.data?.personer || []
	// console.log('response xxx: ', response) //TODO - SLETT MEG
	// console.log('personListe: ', personListe) //TODO - SLETT MEG
	// console.log('seed: ', seed) //TODO - SLETT MEG

	useEffect(() => {
		if (response && personListe?.length === 0) {
			console.log('Set personliste 1') //TODO - SLETT MEG
			setPersonListe(response?.data?.data?.personer)
			setSeed(response?.data?.data?.seed)
			setSide((side) => side + 1)
		}
	}, [response])

	// useEffect(() => {
	// 	setTmpPersoner(response?.data?.data?.personer)
	// }, [side])

	useEffect(() => {
		if (personListe?.length > 0 && response?.data?.data?.personer?.length > 0) {
			// setPersonListe([...personListe, ...tmpPersoner])
			console.log('Set personliste 2') //TODO - SLETT MEG
			setPersonListe([...personListe, ...response?.data?.data?.personer])
			mutate()
		}
	}, [response])

	// useEffect(() => {
	// 	if (personListe?.length > 0 && response?.data?.data?.personer?.length > 0) {
	// 		setPersonListe([...personListe, ...response?.data?.data?.personer])
	// 		mutate()
	// 	}
	// }, [tmpPersoner])

	useEffect(() => {
		window.addEventListener('scroll', handleScroll)
		return () => window.removeEventListener('scroll', handleScroll)
	}, [])

	const handleScroll = () => {
		if (
			window.innerHeight + document.documentElement.scrollTop !==
			document.documentElement.offsetHeight
		) {
			return
		}
		// setSide(response?.data?.data?.nesteSide)
		// const test = (side) => side
		// console.log('test: ', test) //TODO - SLETT MEG
		// if (side < 4) {
		setSide((side) => {
			console.log('side: ', side) //TODO - SLETT MEG
			if (side < 19) {
				return side + 1
			}
			return side
		})
		// console.log('side: ', side) //TODO - SLETT MEG
		console.log('laster flere...: ') //TODO - SLETT MEG
		// }
		// setSeed(seed || response?.data?.data?.seed)
		// console.log('seed: ', seed) //TODO - SLETT MEG
		// console.log('side: ', side) //TODO - SLETT MEG
		// mutate()
	}
	//TODO: Naa faar vi bare en liste med 110 personer fordi endepunktet blir kalt 2 ganger pr side?
	console.log('personListe: ', personListe) //TODO - SLETT MEG
	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Tenor" />
				{/*<Hjelpetekst placement={bottom}>Blablablah</Hjelpetekst>*/}
			</div>
			<SoekForm request={request} setRequest={setRequest} mutate={mutate} />
			<TreffListe
				response={response?.data}
				side={side}
				setSide={setSide}
				setSeed={setSeed}
				personListe={personListe}
				setPersonListe={setPersonListe}
				loading={loading}
				error={error}
			/>
		</div>
	)
}
