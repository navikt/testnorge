import * as _ from 'lodash-es'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { SyntSykemelding } from './partials/SyntSykemelding'
import { DetaljertSykemelding } from './partials/DetaljertSykemelding'
import { Sykemelding, SykemeldingDetaljert, SykemeldingSynt } from '../SykemeldingTypes'
import { erGyldig } from '@/components/transaksjonid/GyldigeBestillinger'
import { Alert } from '@navikt/ds-react'
import React from 'react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import Loading from '@/components/ui/loading/Loading'

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

const VisningAvBestilling = ({ bestillinger }) => {
	return bestillinger.map((bestilling: SykemeldingSynt | SykemeldingDetaljert, idx: number) => {
		if (!bestilling.erGjenopprettet) {
			const syntSykemelding = _.get(bestilling, 'data.sykemelding.syntSykemelding')
			const detaljertSykemelding = _.get(bestilling, 'data.sykemelding.detaljertSykemelding')

			return syntSykemelding ? (
				<SyntSykemelding sykemelding={syntSykemelding} idx={idx} key={idx} />
			) : detaljertSykemelding ? (
				<DetaljertSykemelding sykemelding={detaljertSykemelding} idx={idx} key={idx} />
			) : null
		}
	})
}

const VisningAvTransaksjonsId = ({ data }) => {
	if (!data) {
		return null
	}

	const syntSykemelding = _.get(data, 'syntSykemeldingRequest')
	const detaljertSykemelding = _.get(data, 'detaljertSykemeldingRequest')

	return syntSykemelding ? (
		<SyntSykemelding sykemelding={syntSykemelding} />
	) : detaljertSykemelding ? (
		<DetaljertSykemelding sykemelding={detaljertSykemelding} />
	) : null
}

export const SykemeldingVisning = ({
	data,
	loading,
	bestillingIdListe,
	tilgjengeligMiljoe,
	bestillinger,
}: Sykemelding) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'SYKEMELDING')

	if (loading) {
		return <Loading label="Laster sykemelding-data" />
	}

	if (!data && !bestillinger) {
		return null
	}

	const manglerFagsystemData =
		sjekkManglerSykemeldingData(data) && sjekkManglerSykemeldingBestilling(bestillinger)

	const miljoerMedData = data?.map((miljoData) => miljoData.data && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer?.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data?.find((miljoData) => miljoData?.data)?.miljo

	const filteredData =
		tilgjengeligMiljoe && data?.filter((item) => item.miljo === tilgjengeligMiljoe)

	console.log('data: ', data) //TODO - SLETT MEG
	console.log('bestillinger: ', bestillinger) //TODO - SLETT MEG

	return (
		<div>
			<SubOverskrift label="Sykemelding" iconKind="sykdom" isWarning={manglerFagsystemData} />
			{manglerFagsystemData ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke sykemelding-data på person
				</Alert>
			) : sjekkManglerSykemeldingData(data) ? (
				<VisningAvBestilling bestillinger={bestillinger} />
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData ? filteredData : data}
				>
					<VisningAvTransaksjonsId />
				</MiljoTabs>
			)}
		</div>
	)
}

SykemeldingVisning.filterValues = (bestillinger: Array<Sykemelding>, ident: string) => {
	if (!bestillinger) {
		return null
	}

	return bestillinger.filter(
		(bestilling: any) =>
			bestilling.data.sykemelding && erGyldig(bestilling.id, 'SYKEMELDING', ident),
	)
}
