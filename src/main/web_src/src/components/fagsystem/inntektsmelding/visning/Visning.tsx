import React from 'react'
import _set from 'lodash/set'
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
import JournalpostidVisning from '~/components/journalpostid/journalpostidVisning'

interface InntektsmeldingVisning {
	liste: Array<Inntektsmelding>
	ident: string
}

type EnkelInntektsmelding = {
	data: Array<Inntekt>
	ident: string
}

const getHeader = (data: Inntekt) => {
	const arbeidsgiver = data.arbeidsgiver
		? data.arbeidsgiver.virksomhetsnummer
		: data.arbeidsgiverPrivat
		? data.arbeidsgiverPrivat.arbeidsgiverFnr
		: ''
	return `Inntekt (${arbeidsgiver})`
}

const getSortedListe = (liste: Inntektsmelding[]) => {
	const sortedListe = []
	for (let i = 0; i < liste.length; i++) {
		sortedListe.push(
			liste[i].inntekter.slice().sort(function(a: Inntekter, b: Inntekter) {
				const datoA = new Date(a.avsendersystem.innsendingstidspunkt)
				const datoB = new Date(b.avsendersystem.innsendingstidspunkt)

				return datoA < datoB ? 1 : datoA > datoB ? -1 : 0
			})
		)
	}
	return sortedListe
}

export const InntektsmeldingVisning = ({ liste, ident }: InntektsmeldingVisning) => {
	//Viser data fra bestillingen
	if (!liste || liste.length < 1) return null

	const sortedListe = getSortedListe(liste)

	return (
		<div>
			<SubOverskrift label="Inntektsmelding (fra Altinn)" iconKind="inntektsmelding" />
			{liste.length > 1 ? (
				<DollyFieldArray header="Inntektsmeldinger" data={sortedListe} nested>
					{(inntekter: Inntekter[]) => (
						<EnkelInntektsmeldingVisning data={inntekter} ident={ident} />
					)}
				</DollyFieldArray>
			) : (
				<EnkelInntektsmeldingVisning data={sortedListe[0]} ident={ident} />
			)}
		</div>
	)
}

const EnkelInntektsmeldingVisning = ({ data, ident }: EnkelInntektsmelding) => (
	<DollyFieldArray header="Inntekt" getHeader={getHeader} data={data} expandable={data.length > 1}>
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
				<JournalpostidVisning system="INNTKMELD" ident={ident} />
			</>
		)}
	</DollyFieldArray>
)

type Bestilling = {
	inntektsmelding?: Array<Inntektsmelding>
}

InntektsmeldingVisning.filterValues = (bestillinger: Array<Bestilling>) => {
	if (!bestillinger) return false

	return bestillinger
		.map((bestilling: any) => bestilling.inntektsmelding)
		.filter(
			(inntektsmelding: Inntektsmelding) =>
				inntektsmelding && !tomBestilling(inntektsmelding.inntekter)
		)
}

const tomBestilling = (inntekter: Array<Inntekt>) => {
	const inntekterMedInnhold = inntekter.filter(inntekt => !_isEmpty(inntekt))
	return inntekterMedInnhold.length < 1
}
