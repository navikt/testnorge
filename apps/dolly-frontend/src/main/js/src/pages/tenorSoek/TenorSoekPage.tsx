import Title from '@/components/Title'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { useTenorDomain, useTenorSoek } from '@/utils/hooks/useTenorSoek'
import { SoekForm } from '@/pages/tenorSoek/SoekForm'
import { TreffListe } from '@/pages/tenorSoek/resultatVisning/TreffListe'
import { useState } from 'react'

export default () => {
	// const request = {
	// 	inntektAordningen: {
	// 		beskrivelse: 'ReiseKostMedOvernattingPaaHybelMedKokEllerPrivat',
	// 	},
	// }
	//
	// const { response, loading: isLoading, error: isError } = useTenorSoek('Noekkelinfo', request)
	// console.log('response: ', response) //TODO - SLETT MEG

	// const [request, setRequest] = useState(null)
	// const { response, loading, error, mutate } = useTenorSoek('Noekkelinfo', request)
	// console.log('response: ', response) //TODO - SLETT MEG

	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Tenor" />
				<Hjelpetekst placement={bottom}>Blablablah</Hjelpetekst>
			</div>
			{/*<SoekForm request={request} setRequest={setRequest}/>*/}
			<SoekForm />
			{/*<TreffListe />*/}
		</div>
	)
}
