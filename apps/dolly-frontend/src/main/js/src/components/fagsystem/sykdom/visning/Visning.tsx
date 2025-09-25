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
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useTsmSykemelding } from '@/utils/hooks/useSykemelding'
import { NySykemeldingVisning } from '@/components/fagsystem/sykdom/visning/partials/NySykemeldingVisning'

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
	if (!bestillinger) {
		return null
	}

	return bestillinger?.map((bestilling: SykemeldingSynt | SykemeldingDetaljert, idx: number) => {
		if (!bestilling.erGjenopprettet) {
			const syntSykemelding = _.get(bestilling, 'data.sykemelding.syntSykemelding')
			const detaljertSykemelding = _.get(bestilling, 'data.sykemelding.detaljertSykemelding')

			if (syntSykemelding) {
				return <SyntSykemelding sykemelding={syntSykemelding} idx={idx} key={idx} />
			} else if (detaljertSykemelding) {
				return <DetaljertSykemelding sykemelding={detaljertSykemelding} idx={idx} key={idx} />
			} else {
				return null
			}
		}
	})
}

const VisningAvTransaksjonsId = ({ data }) => {
	if (!data) {
		return null
	}

	const syntSykemelding = _.get(data, 'syntSykemeldingRequest')
	const detaljertSykemelding = _.get(data, 'detaljertSykemeldingRequest')
	const sykemeldingId = _.get(data, 'sykemeldingId')

	if (syntSykemelding) {
		syntSykemelding['sykemeldingId'] = sykemeldingId
		return <SyntSykemelding sykemelding={syntSykemelding} />
	}
	if (detaljertSykemelding) {
		detaljertSykemelding['sykemeldingId'] = sykemeldingId
		return <DetaljertSykemelding sykemelding={detaljertSykemelding} />
	}
}

const Visning = ({ data }) => {
	if (!data) {
		return null
	}
	if (Array.isArray(data)) {
		return (
			<DollyFieldArray header="Sykemelding" data={data} expandable={data.length > 1}>
				{(sykemelding: Sykemelding, idx: number) => (
					<VisningAvTransaksjonsId data={sykemelding} key={idx} />
				)}
			</DollyFieldArray>
		)
	}
	return <VisningAvTransaksjonsId data={data} />
}

export const SykemeldingVisning = ({
	data,
	ident,
	loading,
	bestillingIdListe,
	tilgjengeligMiljoe,
	bestillinger,
}: Sykemelding) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'SYKEMELDING')
	const { sykemeldinger, loading: nySykemeldingLoading } = useTsmSykemelding(ident?.ident)

	if (loading || nySykemeldingLoading) {
		return <Loading label="Laster sykemelding-data" />
	}

	if (!data && !bestillinger && sykemeldinger?.length === 0) {
		return null
	}

	const manglerFagsystemData =
		sjekkManglerSykemeldingData(data) &&
		sjekkManglerSykemeldingBestilling(bestillinger) &&
		sykemeldinger?.length === 0

	const miljoerMedData = data?.map((miljoData) => miljoData.data && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer?.filter((miljo) => !miljoerMedData?.includes(miljo))
	const forsteMiljo = data?.find((miljoData) => miljoData?.data)?.miljo

	const mergeData = () => {
		const mergeMiljo: Array<{ data: any[]; miljo: string }> = []
		data?.forEach((item: any) => {
			const indexOfMiljo = mergeMiljo.findIndex((sykemelding) => sykemelding?.miljo === item?.miljo)
			if (indexOfMiljo >= 0) {
				mergeMiljo[indexOfMiljo].data?.push(item.data)
			} else {
				mergeMiljo.push({
					data: [item.data],
					miljo: item.miljo,
				})
			}
		})
		return mergeMiljo
	}

	const mergetData = mergeData()
	const filteredData =
		tilgjengeligMiljoe && mergetData?.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	let render: React.ReactNode

	if (manglerFagsystemData) {
		render = (
			<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
				Fant ikke sykemelding-data på person
			</Alert>
		)
	} else if (sjekkManglerSykemeldingData(data) && sykemeldinger?.length === 0) {
		render = <VisningAvBestilling bestillinger={bestillinger} ident={ident} />
	} else if (sykemeldinger?.length > 0) {
		render = <NySykemeldingVisning ident={ident} />
	} else {
		render = (
			<MiljoTabs
				bestilteMiljoer={bestilteMiljoer}
				errorMiljoer={errorMiljoer}
				forsteMiljo={forsteMiljo}
				data={filteredData ?? mergetData}
			>
				<Visning data={filteredData} />
			</MiljoTabs>
		)
	}

	return (
		<div>
			<SubOverskrift label="Sykemelding" iconKind="sykdom" isWarning={manglerFagsystemData} />
			{render}
		</div>
	)
}

SykemeldingVisning.filterValues = (bestillinger: Array<Sykemelding>, ident: string) => {
	if (!bestillinger) {
		return null
	}

	return bestillinger.filter(
		(bestilling: any) =>
			bestilling.data?.sykemelding && erGyldig(bestilling.id, 'SYKEMELDING', ident),
	)
}
