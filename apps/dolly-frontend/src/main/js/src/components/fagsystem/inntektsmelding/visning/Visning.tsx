import * as _ from 'lodash-es'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import {
	Bestilling,
	BestillingData,
	Inntekt,
} from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { EnkelInntektsmeldingVisning } from './partials/enkelInntektsmeldingVisning'
import { erGyldig } from '@/components/transaksjonid/GyldigeBestillinger'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { mergeMiljoData } from '@/components/ui/miljoTabs/utils'

interface InntektsmeldingVisningProps {
	liste: Array<BestillingData>
	ident: string
}

export const sjekkManglerInntektsmeldingData = (inntektsmeldingData) => {
	return (
		!inntektsmeldingData ||
		inntektsmeldingData?.length < 1 ||
		inntektsmeldingData?.every((miljoData) => !miljoData.data)
	)
}

export const sjekkManglerInntektsmeldingBestilling = (inntektsmeldingBestilling) => {
	return !inntektsmeldingBestilling || inntektsmeldingBestilling?.length < 1
}

export const InntektsmeldingVisning = ({
	data,
	loading,
	bestillingIdListe,
	tilgjengeligMiljoe,
	bestillinger,
}: InntektsmeldingVisningProps) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'INNTKMELD')

	if (loading) {
		return <Loading label="Laster inntektsmelding-data" />
	}

	if (!data && !bestillinger) {
		return null
	}

	const manglerFagsystemData =
		sjekkManglerInntektsmeldingData(data) && sjekkManglerInntektsmeldingBestilling(bestillinger)

	const miljoerMedData = data?.map((miljoData) => miljoData.data && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer?.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data?.find((miljoData) => miljoData?.data)?.miljo

	const harTransaksjonsidData = data?.every((inntekt) => inntekt?.data?.request)

	const setTransaksjonsidData = () => {
		return data?.map((miljo) => {
			const request = miljo?.data?.request
			const dokument = miljo?.data?.dokument
			return {
				data: {
					dokument: dokument ? dokument : miljo.data,
					request: request
						? request
						: {
								inntekter: bestillinger?.flatMap(
									(bestilling) => bestilling?.data.inntektsmelding.inntekter,
								),
								miljoe: miljo.miljo,
							},
				},
				miljo: miljo.miljo,
			}
		})
	}

	if (!harTransaksjonsidData) {
		data = setTransaksjonsidData()
	}

	const mergetData = mergeMiljoData(data)

	const filteredData =
		tilgjengeligMiljoe && mergetData?.filter((item) => item?.miljo === tilgjengeligMiljoe)

	return (
		<>
			<SubOverskrift label="Inntektsmelding (fra Altinn)" iconKind="inntektsmelding" />
			{manglerFagsystemData ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke inntektsmelding-data på person
				</Alert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData ? filteredData : mergetData}
				>
					<EnkelInntektsmeldingVisning />
				</MiljoTabs>
			)}
		</>
	)
}

InntektsmeldingVisning.filterValues = (bestillinger: Array<Bestilling>, ident: string) => {
	if (!bestillinger) {
		return false
	}

	return bestillinger.filter(
		(bestilling: any) =>
			bestilling.data?.inntektsmelding &&
			!tomBestilling(bestilling.data?.inntektsmelding.inntekter) &&
			erGyldig(bestilling.id, 'INNTKMELD', ident),
	)
}

const tomBestilling = (inntekter: Array<Inntekt>) => {
	const inntekterMedInnhold = inntekter.filter((inntekt) => !_.isEmpty(inntekt))
	return inntekterMedInnhold.length < 1
}
