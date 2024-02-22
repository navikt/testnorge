import Title from '@/components/Title'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { useTenorOversikt } from '@/utils/hooks/useTenorSoek'
import { SoekForm } from '@/pages/tenorSoek/SoekForm'
import { TreffListe } from '@/pages/tenorSoek/resultatVisning/TreffListe'
import { useEffect, useState } from 'react'

export default () => {
	const [request, setRequest] = useState({})
	const [side, setSide] = useState(0)
	const [seed, setSeed] = useState(null)

	const { response, loading, error, mutate } = useTenorOversikt(request, 10, side, seed)
	const [personListe, setPersonListe] = useState([])

	useEffect(() => {
		if (response && personListe?.length === 0) {
			setPersonListe(response?.data?.data?.personer)
			setSeed(response?.data?.data?.seed)
			setSide((side) => side + 1)
		}
	}, [response])

	useEffect(() => {
		if (personListe?.length > 0 && response?.data?.data?.personer?.length > 0) {
			setPersonListe([...personListe, ...response?.data?.data?.personer])
			mutate()
		}
	}, [response])

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
		setSide((side) => {
			if (side < 19) {
				return side + 1
			}
			return side
		})
	}

	console.log('window: ', window.innerHeight) //TODO - SLETT MEG
	console.log('document: ', document.documentElement.offsetHeight) //TODO - SLETT MEG

	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Tenor" />
				{/*<Hjelpetekst placement={bottom}>Blablablah</Hjelpetekst>*/}
			</div>
			<SoekForm request={request} setRequest={setRequest} mutate={mutate} />
			<TreffListe
				response={response?.data}
				personListe={personListe}
				loading={loading}
				error={error}
			/>
		</div>
	)
}
