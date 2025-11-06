import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { AdresseKodeverk, ArbeidKodeverk } from '@/config/kodeverk'
import {
	Aareg,
	PermisjonValues,
	PermitteringValues,
	Timeloennet,
	Utenlands,
} from '@/components/fagsystem/aareg/AaregTypes'
import { Alert } from '@navikt/ds-react'

type ArbeidsforholdData = {
	arbeidsforholdListe: Array<Aareg>
}

type ArbeidsforholdVisningData = {
	arbeidsforhold: Aareg
}

type TimerMedTimeloennetData = {
	timerMedTimeloennet?: Array<Timeloennet>
}

type UtlandsoppholdData = {
	utenlandsopphold?: Array<Utenlands>
}

type PermisjonData = {
	permisjon?: Array<PermisjonValues>
}

type PermitteringData = {
	permittering?: Array<PermitteringValues>
}

const TimerMedTimeloennet = ({ timerMedTimeloennet }: TimerMedTimeloennetData) => {
	if (!timerMedTimeloennet || timerMedTimeloennet.length < 1) {
		return null
	}
	return (
		<DollyFieldArray header="Timer med timelønnet" data={timerMedTimeloennet} nested>
			{(timer: Timeloennet, idx: number) => (
				<React.Fragment key={idx}>
					<TitleValue title="Antall timer" value={timer?.antallTimer} />
					<TitleValue title="Periode fra" value={formatDate(timer?.periode?.fom)} />
					<TitleValue title="Periode til" value={formatDate(timer?.periode?.tom)} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const Utenlandsopphold = ({ utenlandsopphold }: UtlandsoppholdData) => {
	if (!utenlandsopphold || utenlandsopphold.length < 1) {
		return null
	}
	return (
		<DollyFieldArray header="Utenlandsopphold" data={utenlandsopphold} nested>
			{(opphold: Utenlands, idx: number) => (
				<React.Fragment key={idx}>
					<TitleValue
						title="Land"
						value={opphold?.land}
						kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
					/>
					<TitleValue title="Opphold fra" value={formatDate(opphold?.periode?.fom)} />
					<TitleValue title="Opphold til" value={formatDate(opphold?.periode?.tom)} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const Permisjon = ({ permisjon }: PermisjonData) => {
	if (!permisjon || permisjon.length < 1) {
		return null
	}
	return (
		<DollyFieldArray header="Permisjon" data={permisjon} nested>
			{(perm: PermisjonValues, idx: number) => (
				<React.Fragment key={idx}>
					<TitleValue
						title="Permisjonstype"
						value={perm?.permisjon}
						kodeverk={ArbeidKodeverk.PermisjonsOgPermitteringsBeskrivelse}
					/>
					<TitleValue title="Permisjon fra" value={formatDate(perm?.permisjonsPeriode?.fom)} />
					<TitleValue title="Permisjon til" value={formatDate(perm?.permisjonsPeriode?.tom)} />
					<TitleValue title="Permisjonsprosent" value={perm?.permisjonsprosent} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const Permittering = ({ permittering }: PermitteringData) => {
	if (!permittering || permittering.length < 1) {
		return null
	}
	return (
		<DollyFieldArray header="Permittering" data={permittering} nested>
			{(perm: PermitteringValues, idx: number) => (
				<React.Fragment key={idx}>
					<TitleValue
						title="Permittering fra"
						value={formatDate(perm?.permitteringsPeriode?.fom)}
					/>
					<TitleValue
						title="Permittering til"
						value={formatDate(perm?.permitteringsPeriode?.tom)}
					/>
					<TitleValue title="Permitteringsprosent" value={perm?.permitteringsprosent} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const ArbeidsforholdVisning = ({ arbeidsforhold }: ArbeidsforholdVisningData) => {
	const periode =
		arbeidsforhold?.navArbeidsforholdPeriode?.year &&
		arbeidsforhold?.navArbeidsforholdPeriode?.monthValue
			? new Date(
					arbeidsforhold?.navArbeidsforholdPeriode?.year,
					arbeidsforhold?.navArbeidsforholdPeriode?.monthValue,
				)
			: null

	return (
		<React.Fragment>
			<TitleValue title="Organisasjonsnummer" value={arbeidsforhold?.arbeidsgiver?.orgnummer} />
			<TitleValue
				title="Type arbeidsforhold"
				value={arbeidsforhold?.arbeidsforholdstype}
				kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
			/>
			<TitleValue title="Arbeidsgiver ident" value={arbeidsforhold?.arbeidsgiver?.ident} />
			<TitleValue title="Ansatt fra" value={formatDate(arbeidsforhold?.ansettelsesPeriode?.fom)} />
			<TitleValue title="Ansatt til" value={formatDate(arbeidsforhold?.ansettelsesPeriode?.tom)} />
			<TitleValue
				title="Sluttårsak"
				value={arbeidsforhold?.ansettelsesPeriode?.sluttaarsak}
				kodeverk={ArbeidKodeverk.SluttaarsakAareg}
			/>
			<TitleValue title="NAV arbeidsforholdsperiode" value={periode && formatDate(periode)} />
			<TitleValue
				title="Yrke"
				value={arbeidsforhold?.arbeidsavtale?.yrke}
				kodeverk={ArbeidKodeverk.Yrker}
			/>
			<TitleValue
				title="Ansettelsesform"
				value={arbeidsforhold?.arbeidsavtale?.ansettelsesform}
				kodeverk={ArbeidKodeverk.AnsettelsesformAareg}
			/>
			<TitleValue
				title="Stillingsprosent"
				value={
					arbeidsforhold.arbeidsavtale?.stillingsprosent === 0
						? '0'
						: arbeidsforhold.arbeidsavtale?.stillingsprosent
				}
			/>
			<TitleValue
				title="Endringsdato stillingsprosent"
				value={formatDate(arbeidsforhold?.arbeidsavtale?.endringsdatoStillingsprosent)}
			/>
			<TitleValue
				title="Endringsdato lønn"
				value={formatDate(arbeidsforhold?.arbeidsavtale?.sisteLoennsendringsdato)}
			/>
			<TitleValue
				title="Arbeidstidsordning"
				value={arbeidsforhold?.arbeidsavtale?.arbeidstidsordning}
				kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
			/>
			<TitleValue
				title="Avtalt arbeidstimer per uke"
				value={arbeidsforhold?.arbeidsavtale?.avtaltArbeidstimerPerUke}
			/>
			<TitleValue
				title="Skipsregister"
				value={arbeidsforhold.fartoy?.[0]?.skipsregister}
				kodeverk={ArbeidKodeverk.Skipsregistre}
			/>
			<TitleValue
				title="Fartøystype"
				value={arbeidsforhold.fartoy?.[0]?.skipstype}
				kodeverk={ArbeidKodeverk.Skipstyper}
			/>
			<TitleValue
				title="Fartsområde"
				value={arbeidsforhold.fartoy?.[0]?.fartsomraade}
				kodeverk={ArbeidKodeverk.Fartsomraader}
			/>
			<TimerMedTimeloennet timerMedTimeloennet={arbeidsforhold?.antallTimerForTimeloennet} />
			<Utenlandsopphold utenlandsopphold={arbeidsforhold?.utenlandsopphold} />
			<Permisjon permisjon={arbeidsforhold?.permisjon} />
			<Permittering permittering={arbeidsforhold?.permittering} />
		</React.Fragment>
	)
}

export const Arbeidsforhold = ({ arbeidsforholdListe }: ArbeidsforholdData) => {
	if (!arbeidsforholdListe || arbeidsforholdListe.length < 1) {
		return null
	}

	const harAmelding = arbeidsforholdListe[0]?.amelding?.length > 0

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Arbeidsforhold (Aareg)</BestillingTitle>
				{harAmelding ? (
					<Alert variant="warning" size="small" style={{ marginBottom: '20px' }}>
						Arbeidsforholdet inneholder A-melding, som ikke lenger er støttet.
					</Alert>
				) : (
					<DollyFieldArray header="Arbeidsforhold" data={arbeidsforholdListe}>
						{(arbeidsforhold: Aareg, idx: number) => {
							return (
								<React.Fragment key={idx}>
									<ArbeidsforholdVisning arbeidsforhold={arbeidsforhold} key={idx} />
								</React.Fragment>
							)
						}}
					</DollyFieldArray>
				)}
			</ErrorBoundary>
		</div>
	)
}
