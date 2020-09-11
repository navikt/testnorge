import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import {
	Inntektsmelding,
	Inntekt
} from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import ArbeidsforholdVisning from './partials/arbeidsforholdVisning'
import OmsorgspengerVisning from './partials/omsorgspengerVisning'
import RefusjonVisning from './partials/refusjonVisning'
import SykepengerVisning from './partials/sykepengerVisning'
import PleiepengerVisning from './partials/pleiepengerVisning'
import NaturalytelseVisning from './partials/naturalytelseVisning'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollyApi } from '~/service/Api'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'

interface InntektsmeldingVisning {
	liste: Array<Inntektsmelding>
	ident: string
}

type EnkelInntektsmelding = {
	bestilling: BestillingData
	data: Array<Journalpost>
}

type TransaksjonId = {
	miljoe: string
	transaksjonId: {
		journalpostId: string
		dokumentInfoId: string
	}
	bestillingId: string
}

type Dokumentinfo = {
	data: Array<any>
}

type Journalpost = {
	bestillingId: number
	miljoe: string
	journalpost: {
		dokumenter: Array<any>
		journalpostId: string
	}
	skjemainnhold: Inntekt
}

type BestillingData = {
	data: Array<any>
	erGjenopprettet: boolean
	id: number
}

const getHeader = (data: Inntekt) => {
	const arbeidsgiver = data.arbeidsgiver
		? data.arbeidsgiver.virksomhetsnummer
		: data.arbeidsgiverPrivat
		? data.arbeidsgiverPrivat.arbeidsgiverFnr
		: ''
	return `Inntekt (${arbeidsgiver})`
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
											{(inntekter: Array<Inntektsmelding>) => {
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

const EnkelInntektsmeldingVisning = ({ bestilling, data }: EnkelInntektsmelding) => {
	// Sjekk om bestillingid i bestilling er i data
	const journalpostidPaaBestilling = data.filter(id => id.bestillingId === bestilling.id)

	return (
		<>
			<DollyFieldArray
				header="Inntekt"
				getHeader={getHeader}
				data={bestilling.data.inntektsmelding.inntekter}
				expandable={bestilling.data.inntektsmelding.inntekter.length > 1}
			>
				{(inntekt: Inntekt, idx: number) => (
					<>
						<div className="person-visning_content" key={idx}>
							<TitleValue
								title="Årsak til innsending"
								value={Formatters.codeToNorskLabel(inntekt.aarsakTilInnsending)}
							/>
							<TitleValue title="Ytelse" value={Formatters.codeToNorskLabel(inntekt.ytelse)} />
							<TitleValue
								title="Virksomhet (orgnr)"
								value={inntekt.arbeidsgiver && inntekt.arbeidsgiver.orgnummer}
							/>
							<TitleValue
								title="Opplysningspliktig virksomhet"
								value={inntekt.arbeidsgiver && inntekt.arbeidsgiver.virksomhetsnummer}
							/>
							<TitleValue
								title="Innsendingstidspunkt"
								value={Formatters.formatDate(inntekt.avsendersystem.innsendingstidspunkt)}
							/>
							<TitleValue
								title="Privat arbeidsgiver"
								value={inntekt.arbeidsgiverPrivat && inntekt.arbeidsgiverPrivat.arbeidsgiverFnr}
							/>
							<TitleValue title="Har nær relasjon" value={inntekt.naerRelasjon} />
							<TitleValue
								title="Startdato foreldrepenger"
								value={Formatters.formatDate(inntekt.startdatoForeldrepengeperiode)}
							/>
						</div>
						<ArbeidsforholdVisning data={inntekt.arbeidsforhold} />
						<OmsorgspengerVisning data={inntekt.omsorgspenger} />
						<RefusjonVisning data={inntekt.refusjon} />
						<SykepengerVisning data={inntekt.sykepengerIArbeidsgiverperioden} />
						<PleiepengerVisning data={inntekt.pleiepengerPerioder} />
						<NaturalytelseVisning
							data={inntekt.gjenopptakelseNaturalytelseListe}
							header="Gjenopptagekse av naturalytelse"
						/>
						<NaturalytelseVisning
							data={inntekt.opphoerAvNaturalytelseListe}
							header="Opphør av naturalytelse"
						/>
					</>
				)}
			</DollyFieldArray>
			<DollyFieldArray data={journalpostidPaaBestilling} header="Journalpost-ID" nested>
				{(id: Journalpost, idx: number) => {
					const [viserSkjemainnhold, vis, skjul] = useBoolean(false)
					return (
						<div key={idx} className="person-visning_content">
							<TitleValue title="Miljø" value={id.miljoe.toUpperCase()} />
							<TitleValue title="Journalpost-id" value={id.journalpost.journalpostId} />
							<TitleValue
								title="Dokumentinfo-id"
								value={id.journalpost.dokumenter[0].dokumentInfoId}
							/>
							{!viserSkjemainnhold && (
								<Button onClick={vis} kind="">
									VIS SKJEMAINNHOLD
								</Button>
							)}
							{viserSkjemainnhold && (
								<Button onClick={skjul} kind="">
									SKJUL SKJEMAINNHOLD
								</Button>
							)}
							{viserSkjemainnhold && (
								<div
									className="person-visning_content"
									style={{ whiteSpace: 'pre', color: '#59514B' }}
								>
									{JSON.stringify(id.skjemainnhold, null, 4)}
								</div>
							)}
						</div>
					)
				}}
			</DollyFieldArray>
		</>
	)
}

type Bestilling = {
	inntektsmelding?: Array<Inntektsmelding>
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
