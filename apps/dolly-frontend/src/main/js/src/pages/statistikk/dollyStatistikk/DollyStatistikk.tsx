import Loading from '@/components/ui/loading/Loading'
import { useCurrentBrukerStatistikk } from '@/utils/hooks/useDollyStatistikk'
import React from 'react'
import { GuidePanel } from '@navikt/ds-react'
import cypress from '@/assets/img/cypress.png'

export default ({ brukerId }: { brukerId: string }) => {
	const { dollyStatistikk, loading } = useCurrentBrukerStatistikk(brukerId)
	if (loading) {
		return <Loading label={'Laster Statistikk...'} />
	}
	return (
		<>
			<GuidePanel illustration={<img alt="Profilbilde" src={cypress} />}>
				Du har bestilt hele {dollyStatistikk.antallIdenter} identer gjennom Dolly, fordelt på{' '}
				{dollyStatistikk.antallBestillinger} bestillinger! Bra jobba, kanskje en dag er du like rå
				på testing som Betsy!
			</GuidePanel>
		</>
	)
}
