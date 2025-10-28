import * as _ from 'lodash-es'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Bestilling, Inntekt } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { EnkelInntektsmeldingVisning } from './partials/enkelInntektsmeldingVisning'
import { erGyldig } from '@/components/transaksjonid/GyldigeBestillinger'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { mergeMiljoData } from '@/components/ui/miljoTabs/utils'

interface InntektsmeldingVisningProps {
	liste?: any[]
	data?: any[]
	ident?: string
	loading?: boolean
	bestillingIdListe?: any
	tilgjengeligMiljoe?: string
	bestillinger?: any[]
}

export const sjekkManglerInntektsmeldingData = (inntektsmeldingData: any[] | undefined) => {
	return (
		!inntektsmeldingData ||
		inntektsmeldingData.length < 1 ||
		inntektsmeldingData.every((miljoData: any) => !miljoData.data)
	)
}

export const sjekkManglerInntektsmeldingBestilling = (inntektsmeldingBestilling: any) => {
	return !inntektsmeldingBestilling || inntektsmeldingBestilling.length < 1
}

export const InntektsmeldingVisning = ({
	liste,
	data: dataProp,
	loading,
	bestillingIdListe,
	tilgjengeligMiljoe,
	bestillinger,
}: InntektsmeldingVisningProps) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'INNTKMELD')
	let data: any[] | undefined = liste || dataProp

	if (loading) {
		return <Loading label="Laster inntektsmelding-data" />
	}

	if (!data && !bestillinger) {
		return null
	}

	const manglerFagsystemData =
		sjekkManglerInntektsmeldingData(data) && sjekkManglerInntektsmeldingBestilling(bestillinger)

	const miljoerMedData = data?.map((miljoData: any) => miljoData.data && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer?.filter((miljo: string) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data?.find((miljoData: any) => miljoData?.data)?.miljo

	const harTransaksjonsidData = data?.every((inntekt: any) => inntekt?.data?.request)

	const setTransaksjonsidData = () => {
		return data?.map((miljo: any) => {
			const request = miljo?.data?.request
			const dokument = miljo?.data?.dokument
			return {
				data: {
					dokument: dokument ? dokument : miljo.data,
					request: request
						? request
						: {
								inntekter: bestillinger?.flatMap(
									(bestilling: any) => bestilling?.data.inntektsmelding.inntekter,
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

	const mergetData: any[] | undefined = data ? mergeMiljoData(data as any[]) : undefined

	const filteredData =
		tilgjengeligMiljoe && mergetData?.filter((item: any) => item?.miljo === tilgjengeligMiljoe)

	return (
		<>
			<SubOverskrift label="Inntektsmelding (fra Altinn)" iconKind="inntektsmelding" />
			{manglerFagsystemData ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke inntektsmelding-data på person
				</Alert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer as any}
					errorMiljoer={errorMiljoer as any}
					forsteMiljo={forsteMiljo as any}
					data={(filteredData ? filteredData : mergetData) as any}
				>
					<EnkelInntektsmeldingVisning
						data={(filteredData ? filteredData : mergetData)?.map((m: any) => m.data) || []}
					/>
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
