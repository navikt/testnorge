import React from 'react'
import Loading from '@/components/ui/loading/Loading'
import Icon from '@/components/ui/icon/Icon'
import * as _ from 'lodash-es'
import { BrregErFrVisning } from '@/components/fagsystem/skatteetaten/visning/BrregErFrVisning'
import { InntektVisning } from '@/components/fagsystem/skatteetaten/visning/InntektVisning'
import { TjenestepensjonsavtaleVisning } from '@/components/fagsystem/skatteetaten/visning/TjenestepensjonsavtaleVisning'
import { SkattemeldingVisning } from '@/components/fagsystem/skatteetaten/visning/SkattemeldingVisning'
import { Alert, Link } from '@navikt/ds-react'

type SkatteetatenVisningProps = {
	data: {
		tenorRelasjoner: any
	}
	loading: boolean
}

// Midlertidig visning av data fra Tenor. Skrives kanskje om naar det er behov for aa vise flere Tenor-data.
export const SkatteetatenVisning = ({ data, loading }: SkatteetatenVisningProps) => {
	if (loading) {
		return <Loading label="Laster Tenor-data ..." />
	}

	const tenorRelasjoner = data?.tenorRelasjoner
	if (!data || !tenorRelasjoner) {
		return null
	}

	const tjenestepensjonavtaleListe = tenorRelasjoner.tjenestepensjonavtale
	const harTjenestepensjonavtale = tjenestepensjonavtaleListe?.length > 0

	const harDagligLederRolle = _.get(tenorRelasjoner, 'brreg-er-fr')?.length > 0

	const skattemeldingListe = tenorRelasjoner.skattemelding
	const harSkattemelding = skattemeldingListe?.length > 0

	const inntektListe = tenorRelasjoner.inntekt
	const harInntekt = inntektListe?.length > 0

	const getApiLink = () => {
		if (harTjenestepensjonavtale) {
			return 'https://skatteetaten.github.io/api-dokumentasjon/api/tjenestepensjonsavtale'
		} else if (harSkattemelding) {
			return 'https://skatteetaten.github.io/api-dokumentasjon/api/skattemelding'
		} else if (harInntekt) {
			return 'https://skatteetaten.github.io/api-dokumentasjon/api/inntekt'
		} else {
			return 'https://skatteetaten.github.io/api-dokumentasjon/api/aksjebeholdning'
		}
	}

	return (
		<div style={{ margin: '15px 0 20px 0' }}>
			<div className="sub-overskrift" style={{ backgroundColor: '#4B797A', color: '#fff' }}>
				<Icon fontSize={'1.5rem'} kind="tenor" />
				<h3>Data fra Tenor</h3>
			</div>
			<Alert inline variant="info" size="small" style={{ marginBottom: '15px' }}>
				Du kan hente utfyllende persondata for personen via Skatteetatens API-er, se mer informasjon{' '}
				<Link href={getApiLink()} target="_blank">
					her
				</Link>
			</Alert>
			<TjenestepensjonsavtaleVisning tpListe={tjenestepensjonavtaleListe} />
			<BrregErFrVisning harDagligLederRolle={harDagligLederRolle} />
			<SkattemeldingVisning skattemeldingListe={skattemeldingListe} />
			<InntektVisning inntektListe={inntektListe} />
		</div>
	)
}
