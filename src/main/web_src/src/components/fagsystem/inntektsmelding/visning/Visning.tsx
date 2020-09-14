import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import {
	Inntekt,
	TransaksjonId,
	Dokumentinfo,
	Journalpost,
	Bestilling,
	BestillingData
} from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { EnkelInntektsmeldingVisning } from './partials/enkelInntektsmeldingVisning'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollyApi } from '~/service/Api'

interface InntektsmeldingVisning {
	liste: Array<BestillingData>
	ident: string
}

export const InntektsmeldingVisning = ({ liste, ident }: InntektsmeldingVisning) => {
	//Viser data fra bestillingen
	if (!liste || liste.length < 1) return null

	return (
		<div>
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
										return {
											bestillingId: bestilling.bestillingId,
											miljoe: bestilling.miljoe,
											journalpost: response.data[0].data.journalpost,
											skjemainnhold: response.data[1].Skjemainnhold
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
							data.find(x => x.bestillingId === bestilling.id)
						)
						if (gyldigeBestillinger) {
							return (
								<>
									<SubOverskrift label="Inntektsmelding (fra Altinn)" iconKind="inntektsmelding" />
									{gyldigeBestillinger.length > 1 ? (
										<DollyFieldArray header="Inntektsmelding" data={gyldigeBestillinger} expandable>
											{(inntekter: BestillingData) => {
												return <EnkelInntektsmeldingVisning bestilling={inntekter} data={data} />
											}}
										</DollyFieldArray>
									) : (
										data.find(x => x.bestillingId === gyldigeBestillinger[0].id) && (
											<EnkelInntektsmeldingVisning
												bestilling={gyldigeBestillinger[0]}
												data={data}
											/>
										)
									)}
								</>
							)
						} else return null
					} else return null
				}}
			/>
		</div>
	)
}

InntektsmeldingVisning.filterValues = (bestillinger: Array<Bestilling>) => {
	if (!bestillinger) return false

	return bestillinger.filter(
		(bestilling: any) =>
			bestilling.data.inntektsmelding && !tomBestilling(bestilling.data.inntektsmelding.inntekter)
	)
}

const tomBestilling = (inntekter: Array<Inntekt>) => {
	const inntekterMedInnhold = inntekter.filter(inntekt => !_isEmpty(inntekt))
	return inntekterMedInnhold.length < 1
}
