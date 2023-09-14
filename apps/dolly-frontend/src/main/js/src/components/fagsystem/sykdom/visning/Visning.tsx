import * as _ from 'lodash-es'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { SyntSykemelding } from './partials/SyntSykemelding'
import { DetaljertSykemelding } from './partials/DetaljertSykemelding'
import { Sykemelding, SykemeldingDetaljert, SykemeldingSynt } from '../SykemeldingTypes'
import { erGyldig } from '@/components/transaksjonid/GyldigeBestillinger'
import { Alert } from '@navikt/ds-react'
import React from 'react'

export const sjekkManglerSykemeldingData = (sykemeldingData) => {
	return (
		!sykemeldingData ||
		sykemeldingData?.length < 1 ||
		sykemeldingData?.every((miljoData) => !miljoData.data)
	)
}

export const sjekkManglerSykemeldingBestilling = (sykemeldingBestilling) => {
	return !sykemeldingBestilling || sykemeldingBestilling?.length < 1
}

export const SykemeldingVisning = ({
	data,
	loading,
	bestillingIdListe,
	tilgjengeligMiljoe,
	bestillinger,
}: Sykemelding) => {
	// Viser foreløpig bestillingsdata
	// if ((!data || data.length < 1) && (!bestillinger || bestillinger.length < 1)) {
	// 	return null
	// }
	console.log('data: ', data) //TODO - SLETT MEG
	console.log('bestillinger: ', bestillinger) //TODO - SLETT MEG
	if (!data && !bestillinger) {
		return null
	}

	const manglerFagsystemData =
		sjekkManglerSykemeldingData(data) && sjekkManglerSykemeldingBestilling(bestillinger)

	return (
		<div>
			<SubOverskrift label="Sykemelding" iconKind="sykdom" isWarning={manglerFagsystemData} />
			{manglerFagsystemData ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke sykemelding-data på person
				</Alert>
			) : (
				// TODO: Fortsett her - sjekk manglerSykemeldingData og vis evt på gammel måte, ellers miljøvisning
				data.map((item: SykemeldingSynt | SykemeldingDetaljert, idx: number) => {
					const syntSykemelding = _.get(item, 'data.syntSykemeldingRequest')
					const detaljertSykemelding = _.get(item, 'data.detaljertSykemeldingRequest')

					return syntSykemelding ? (
						<SyntSykemelding sykemelding={syntSykemelding} idx={idx} key={idx} />
					) : detaljertSykemelding ? (
						<DetaljertSykemelding sykemelding={detaljertSykemelding} idx={idx} key={idx} />
					) : null
				})
			)}
		</div>
	)
}

SykemeldingVisning.filterValues = (bestillinger: Array<Sykemelding>, ident: string) => {
	if (!bestillinger) {
		return null
	}
	console.log('bestillinger: ', bestillinger) //TODO - SLETT MEG
	return bestillinger.filter(
		(bestilling: any) =>
			bestilling.data.sykemelding && erGyldig(bestilling.id, 'SYKEMELDING', ident),
	)
}
