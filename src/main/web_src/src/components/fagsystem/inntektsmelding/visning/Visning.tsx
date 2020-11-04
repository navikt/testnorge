import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import {
	Bestilling,
	BestillingData,
	Dokumentinfo,
	Inntekt,
	Journalpost,
	TransaksjonId
} from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { EnkelInntektsmeldingVisning } from './partials/enkelInntektsmeldingVisning'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollyApi } from '~/service/Api'
import { erGyldig } from '~/components/transaksjonid/GyldigeBestillinger'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface InntektsmeldingVisning {
	liste: Array<BestillingData>
	ident: string
}

export const InntektsmeldingVisning = ({ liste, ident }: InntektsmeldingVisning) => {
	//Viser data fra bestillingen
	if (!liste || liste.length < 1) return null

	return (
		<div>
			<ErrorBoundary>
				<LoadableComponent
					onFetch={() =>
						DollyApi.getTransaksjonid('INNTKMELD', ident)
							.then(({ data }: { data: Array<TransaksjonId> }) => {
								return data.map((bestilling: TransaksjonId) => {
									return DollyApi.getInntektsmeldingDokumentinfo(
										bestilling.transaksjonId.journalpostId,
										bestilling.transaksjonId.dokumentInfoId,
										bestilling.miljoe
									)
										.then((response: Dokumentinfo) => {
											if (response) {
												if (response.data[0].feil || !response.data[0].data) {
													return response.data[0]
												}
												return {
													bestillingId: bestilling.bestillingId,
													miljoe: bestilling.miljoe,
													journalpost: response.data[0].data.journalpost,
													skjemainnhold: response.data[1] && response.data[1].Skjemainnhold
												}
											}
										})
										.catch(error => console.error(error))
								})
							})
							.then((data: Array<Promise<any>>) => {
								return Promise.all(data)
							})
					}
					render={(data: Array<Journalpost>) => {
						if (data && data.length > 0) {
							const gyldigeBestillinger = liste.filter(bestilling =>
								data
									.filter(dokument => dokument != undefined && dokument.journalpost != null)
									.find(x => (x && x.bestillingId ? x.bestillingId === bestilling.id : x))
							)
							if (gyldigeBestillinger && gyldigeBestillinger.length > 0) {
								return (
									<ErrorBoundary>
										<>
											<SubOverskrift
												label="Inntektsmelding (fra Altinn)"
												iconKind="inntektsmelding"
											/>
											{gyldigeBestillinger.length > 1 ? (
												<DollyFieldArray
													header="Inntektsmelding"
													data={gyldigeBestillinger}
													expandable
												>
													{(inntekter: BestillingData) => {
														return (
															<EnkelInntektsmeldingVisning bestilling={inntekter} data={data} />
														)
													}}
												</DollyFieldArray>
											) : (
												<EnkelInntektsmeldingVisning
													bestilling={gyldigeBestillinger[0]}
													data={data}
												/>
											)}
										</>
									</ErrorBoundary>
								)
							} else return null
						} else return null
					}}
				/>
			</ErrorBoundary>
		</div>
	)
}

InntektsmeldingVisning.filterValues = (bestillinger: Array<Bestilling>, ident: string) => {
	if (!bestillinger) return false

	return bestillinger.filter(
		(bestilling: any) =>
			bestilling.data.inntektsmelding &&
			!tomBestilling(bestilling.data.inntektsmelding.inntekter) &&
			erGyldig(bestilling.id, 'INNTKMELD', ident)
	)
}

const tomBestilling = (inntekter: Array<Inntekt>) => {
	const inntekterMedInnhold = inntekter.filter(inntekt => !_isEmpty(inntekt))
	return inntekterMedInnhold.length < 1
}
