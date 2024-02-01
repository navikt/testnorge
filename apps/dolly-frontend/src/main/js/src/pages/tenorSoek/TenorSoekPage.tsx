import Title from '@/components/Title'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { useTenorOversikt } from '@/utils/hooks/useTenorSoek'
import { SoekForm } from '@/pages/tenorSoek/SoekForm'
import { TreffListe } from '@/pages/tenorSoek/resultatVisning/TreffListe'
import { useState } from 'react'

export default () => {
	const [request, setRequest] = useState({}) // Evt. bruk null for aa ikke hente data ved oppstart
	const { response, loading, error, mutate } = useTenorOversikt(request)

	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Tenor" />
				<Hjelpetekst placement={bottom}>Blablablah</Hjelpetekst>
			</div>
			<SoekForm request={request} setRequest={setRequest} mutate={mutate} />
			<TreffListe response={response?.data} loading={loading} error={error} />
		</div>
	)
}
