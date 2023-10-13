import * as _ from 'lodash-es'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import {
	Bestilling,
	BestillingData,
	Inntekt,
	TransaksjonId,
} from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { EnkelInntektsmeldingVisning } from './partials/enkelInntektsmeldingVisning'
import { DollyApi } from '@/service/Api'
import { erGyldig } from '@/components/transaksjonid/GyldigeBestillinger'
import JoarkDokumentService, {
	Dokument,
	Journalpost,
} from '@/service/services/JoarkDokumentService'
import LoadableComponentWithRetry from '@/components/ui/loading/LoadableComponentWithRetry'
import Panel from '@/components/ui/panel/Panel'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import inntekt from '@/components/inntektStub/validerInntekt/Inntekt'

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

const InntektsmeldingListe = ({ data }) => {
	if (!data) {
		return null
	}
	console.log('data: ', data) //TODO - SLETT MEG
	return (
		<DollyFieldArray
			ignoreOnSingleElement={true}
			header="Inntektsmelding"
			data={data.request?.inntekter}
			expandable
		>
			{(inntekter: BestillingData) => (
				<EnkelInntektsmeldingVisning bestilling={inntekter} data={data} />
			)}
		</DollyFieldArray>
	)
}

// export const InntektsmeldingVisning = ({ liste, ident }: InntektsmeldingVisningProps) => {
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

	// const filteredData =
	// 	tilgjengeligMiljoe && data?.filter((item) => item.miljo === tilgjengeligMiljoe)

	const harTransaksjonsidData = data?.some((inntekt) => inntekt?.data?.request)

	const setTransaksjonsidData = () => {
		return data.map((miljo) => {
			return {
				data: {
					dokument: miljo.data,
					request: {
						//TODO: Hent inntektsmelding fra alle bestillinger
						inntekter: bestillinger?.[0]?.data.inntektsmelding.inntekter,
						miljoe: miljo.miljo,
					},
				},
				miljo: miljo.miljo,
			}
		})
	}
	console.log('data: ', data) //TODO - SLETT MEG
	if (!harTransaksjonsidData) {
		data = setTransaksjonsidData()
	}

	const mergeData = () => {
		const mergeMiljo = []
		data.forEach((item) => {
			const indexOfMiljo = mergeMiljo.findIndex((inntekt) => inntekt?.miljo === item?.miljo)
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
	// console.log('mergetData: ', mergetData) //TODO - SLETT MEG

	const filteredData =
		tilgjengeligMiljoe && mergetData?.filter((item) => item.miljo === tilgjengeligMiljoe)

	// const getDokumenter = (bestilling: TransaksjonId): Promise<Dokument[]> => {
	// 	const journalpostId =
	// 		bestilling.transaksjonId.dokument?.journalpostId || bestilling.transaksjonId.journalpostId
	// 	return JoarkDokumentService.hentJournalpost(journalpostId, bestilling.miljoe).then(
	// 		(journalpost: Journalpost) => {
	// 			return Promise.all(
	// 				journalpost.dokumenter.map((document: Dokument) =>
	// 					JoarkDokumentService.hentDokument(
	// 						journalpostId,
	// 						document.dokumentInfoId,
	// 						bestilling.miljoe,
	// 						'ORIGINAL',
	// 					).then((dokument: string) => ({
	// 						journalpostId,
	// 						dokumentInfoId: document.dokumentInfoId,
	// 						dokument,
	// 					})),
	// 				),
	// 			)
	// 		},
	// 	)
	// }

	// console.log('data xxxxx: ', data) //TODO - SLETT MEG
	// console.log('mergetData: ', mergetData) //TODO - SLETT MEG
	// console.log('bestillinger: ', bestillinger) //TODO - SLETT MEG
	return (
		<>
			<SubOverskrift label="Inntektsmelding (fra Altinn)" iconKind="inntektsmelding" />
			{manglerFagsystemData ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke inntektsmelding-data på person
				</Alert>
			) : sjekkManglerInntektsmeldingData(data) ? (
				<p>Vis bestillingsdata her</p>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData ? filteredData : mergetData}
				>
					{/*<InntektsmeldingListe data={filteredData ? filteredData : data} />*/}
					<EnkelInntektsmeldingVisning />
				</MiljoTabs>
			)}
			{/*{data.length > 5 ? (*/}
			{/*	// @ts-ignore*/}
			{/*	<Panel heading="Inntektsmeldinger">*/}
			{/*		<DollyFieldArray*/}
			{/*			ignoreOnSingleElement={true}*/}
			{/*			header="Inntektsmelding"*/}
			{/*			data={filteredData ? filteredData : data}*/}
			{/*			expandable*/}
			{/*		>*/}
			{/*			{(inntekter: BestillingData) => (*/}
			{/*				<EnkelInntektsmeldingVisning bestilling={inntekter} data={data} />*/}
			{/*			)}*/}
			{/*		</DollyFieldArray>*/}
			{/*	</Panel>*/}
			{/*) : (*/}
			{/*	<DollyFieldArray*/}
			{/*		ignoreOnSingleElement={true}*/}
			{/*		header="Inntektsmelding"*/}
			{/*		data={filteredData ? filteredData : data}*/}
			{/*		expandable*/}
			{/*	>*/}
			{/*		{(inntekter: BestillingData) => (*/}
			{/*			<EnkelInntektsmeldingVisning bestilling={inntekter} data={data} />*/}
			{/*		)}*/}
			{/*	</DollyFieldArray>*/}
			{/*)}*/}
		</>
	)

	// return (
	// 	<LoadableComponentWithRetry
	// 		onFetch={() =>
	// 			DollyApi.getTransaksjonid('INNTKMELD', ident)
	// 				.then(({ data }: { data: Array<TransaksjonId> }) => {
	// 					if (!data) {
	// 						return null
	// 					}
	// 					return data.map((bestilling: TransaksjonId) => {
	// 						return getDokumenter(bestilling).then((response) => {
	// 							if (response) {
	// 								return {
	// 									bestillingId: bestilling.bestillingId,
	// 									miljoe: bestilling.miljoe,
	// 									dokumenter: response,
	// 								}
	// 							}
	// 						})
	// 					})
	// 				})
	// 				.then((data: Array<Promise<any>>) => {
	// 					return Promise.all(data)
	// 				})
	// 		}
	// 		render={(data: Array<Journalpost>) => {
	// 			if (data && data.length > 0) {
	// 				const gyldigeBestillinger = liste.filter((bestilling) =>
	// 					data.find((x) => (x && x.bestillingId ? x.bestillingId === bestilling.id : x)),
	// 				)
	//
	// 				if (gyldigeBestillinger && gyldigeBestillinger.length > 0) {
	// 					return (
	// 						<>
	// 							<SubOverskrift label="Inntektsmelding (fra Altinn)" iconKind="inntektsmelding" />
	// 							{data.length > 5 ? (
	// 								// @ts-ignore
	// 								<Panel heading={`Inntektsmeldinger`}>
	// 									<DollyFieldArray
	// 										ignoreOnSingleElement={true}
	// 										header="Inntektsmelding"
	// 										data={gyldigeBestillinger}
	// 										expandable
	// 									>
	// 										{(inntekter: BestillingData) => (
	// 											<EnkelInntektsmeldingVisning bestilling={inntekter} data={data} />
	// 										)}
	// 									</DollyFieldArray>
	// 								</Panel>
	// 							) : (
	// 								<DollyFieldArray
	// 									ignoreOnSingleElement={true}
	// 									header="Inntektsmelding"
	// 									data={gyldigeBestillinger}
	// 									expandable
	// 								>
	// 									{(inntekter: BestillingData) => (
	// 										<EnkelInntektsmeldingVisning bestilling={inntekter} data={data} />
	// 									)}
	// 								</DollyFieldArray>
	// 							)}
	// 						</>
	// 					)
	// 				}
	// 			}
	// 		}}
	// 		label="Laster inntektsmelding data"
	// 	/>
	// )
}

InntektsmeldingVisning.filterValues = (bestillinger: Array<Bestilling>, ident: string) => {
	if (!bestillinger) {
		return false
	}

	return bestillinger.filter(
		(bestilling: any) =>
			bestilling.data.inntektsmelding &&
			!tomBestilling(bestilling.data.inntektsmelding.inntekter) &&
			erGyldig(bestilling.id, 'INNTKMELD', ident),
	)
}

const tomBestilling = (inntekter: Array<Inntekt>) => {
	const inntekterMedInnhold = inntekter.filter((inntekt) => !_.isEmpty(inntekt))
	return inntekterMedInnhold.length < 1
}
