import * as _ from 'lodash-es'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Sykemelding } from '../SykemeldingTypes'
import { harGyldigTransaksjonsid } from '@/components/transaksjonid/GyldigeBestillinger'
import { Alert } from '@navikt/ds-react'
import React from 'react'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import Loading from '@/components/ui/loading/Loading'
import { useTsmSykemelding } from '@/utils/hooks/useSykemelding'
import { SykemeldingVisning } from '@/components/fagsystem/sykdom/visning/partials/SykemeldingVisning'
import { DEFAULT_RETRY_COUNT } from '@/pages/gruppe/PersonVisning/PersonVisning'

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

export const SykemeldingPanel = ({
	data,
	ident,
	loading,
	bestillingIdListe,
	tilgjengeligMiljoe,
	bestillinger,
}: Sykemelding) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'SYKEMELDING')
	const { sykemeldinger, loading: nySykemeldingLoading } = useTsmSykemelding(
		ident?.ident,
		bestillinger ? DEFAULT_RETRY_COUNT : 0,
	)

	const hasAnyData =
		(sykemeldinger && sykemeldinger.length > 0) ||
		(bestillinger && bestillinger.length > 0)

	if ((loading || nySykemeldingLoading) && !hasAnyData) {
		return <Loading label="Laster sykemelding-data" />
	}

	if (!bestillinger && sykemeldinger?.length === 0) {
		return null
	}

	const manglerFagsystemData =
		sjekkManglerSykemeldingBestilling(bestillinger) &&
		sykemeldinger?.length === 0

	let render: React.ReactNode

	if (manglerFagsystemData) {
		render = (
			<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
				Fant ikke sykemelding-data på person
			</Alert>
		)
	} else {
		render = <SykemeldingVisning ident={ident} sykemeldinger={sykemeldinger} />
	}

	return (
		<div>
			<SubOverskrift label="Sykemelding" iconKind="sykdom" isWarning={manglerFagsystemData} />
			{render}
		</div>
	)
}

SykemeldingPanel.filterValues = (
	bestillinger: Array<Sykemelding>,
	ident: string,
	allTransaksjonsid?: any[],
) => {
	if (!bestillinger) {
		return null
	}

	return bestillinger.filter(
		(bestilling: any) =>
			bestilling.data?.sykemelding &&
			harGyldigTransaksjonsid(bestilling.id, allTransaksjonsid),
	)
}
