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

interface InntektsmeldingVisningProps {
	liste: Array<BestillingData>
	ident: string
}

export const InntektsmeldingVisning = ({ liste, ident }: InntektsmeldingVisningProps) => {
	//Viser data fra bestillingen
	if (!liste || liste.length < 1) {
		return null
	}

	const getDokumenter = (bestilling: TransaksjonId): Promise<Dokument[]> => {
		return JoarkDokumentService.hentJournalpost(
			bestilling.transaksjonId.dokument.journalpostId,
			bestilling.miljoe,
		).then((journalpost: Journalpost) => {
			return Promise.all(
				journalpost.dokumenter.map((document: Dokument) =>
					JoarkDokumentService.hentDokument(
						bestilling.transaksjonId.dokument.journalpostId,
						document.dokumentInfoId,
						bestilling.miljoe,
						'ORIGINAL',
					).then((dokument: string) => ({
						journalpostId: bestilling.transaksjonId.dokument.journalpostId,
						dokumentInfoId: document.dokumentInfoId,
						dokument,
					})),
				),
			)
		})
	}

	return (
		<LoadableComponentWithRetry
			onFetch={() =>
				DollyApi.getTransaksjonid('INNTKMELD', ident)
					.then(({ data }: { data: Array<TransaksjonId> }) => {
						if (!data) {
							return null
						}
						return data.map((bestilling: TransaksjonId) => {
							return getDokumenter(bestilling).then((response) => {
								if (response) {
									return {
										bestillingId: bestilling.bestillingId,
										miljoe: bestilling.miljoe,
										dokumenter: response,
									}
								}
							})
						})
					})
					.then((data: Array<Promise<any>>) => {
						return Promise.all(data)
					})
			}
			render={(data: Array<Journalpost>) => {
				if (data && data.length > 0) {
					const gyldigeBestillinger = liste.filter((bestilling) =>
						data.find((x) => (x && x.bestillingId ? x.bestillingId === bestilling.id : x)),
					)

					if (gyldigeBestillinger && gyldigeBestillinger.length > 0) {
						return (
							<>
								<SubOverskrift label="Inntektsmelding (fra Altinn)" iconKind="inntektsmelding" />
								{data.length > 5 ? (
									// @ts-ignore
									<Panel heading={`Inntektsmeldinger`}>
										<DollyFieldArray
											ignoreOnSingleElement={true}
											header="Inntektsmelding"
											data={gyldigeBestillinger}
											expandable
										>
											{(inntekter: BestillingData) => (
												<EnkelInntektsmeldingVisning bestilling={inntekter} data={data} />
											)}
										</DollyFieldArray>
									</Panel>
								) : (
									<DollyFieldArray
										ignoreOnSingleElement={true}
										header="Inntektsmelding"
										data={gyldigeBestillinger}
										expandable
									>
										{(inntekter: BestillingData) => (
											<EnkelInntektsmeldingVisning bestilling={inntekter} data={data} />
										)}
									</DollyFieldArray>
								)}
							</>
						)
					}
				}
			}}
			label="Laster inntektsmelding data"
		/>
	)
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
