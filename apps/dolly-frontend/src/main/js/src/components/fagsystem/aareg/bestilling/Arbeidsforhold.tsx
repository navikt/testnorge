import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React, { useState } from 'react'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showKodeverkLabel } from '@/utils/DataFormatter'
import { AdresseKodeverk, ArbeidKodeverk } from '@/config/kodeverk'
import DollyKjede from '@/components/dollyKjede/DollyKjede'
import {
	Aareg,
	PermisjonValues,
	PermitteringValues,
	Timeloennet,
	Utenlands,
} from '@/components/fagsystem/aareg/AaregTypes'

type ArbeidsforholdData = {
	arbeidsforholdListe: Array<Aareg>
}

type ArbeidsforholdVisningData = {
	arbeidsforhold: Aareg
	harAmelding: boolean
}

type TimerMedTimeloennetData = {
	timerMedTimeloennet?: Array<Timeloennet>
	harAmelding: boolean
}

type UtlandsoppholdData = {
	utenlandsopphold?: Array<Utenlands>
	harAmelding: boolean
}

type PermisjonData = {
	permisjon?: Array<PermisjonValues>
	harAmelding: boolean
}

type PermitteringData = {
	permittering?: Array<PermitteringValues>
	harAmelding: boolean
}

const TimerMedTimeloennet = ({ timerMedTimeloennet, harAmelding }: TimerMedTimeloennetData) => {
	if (!timerMedTimeloennet || timerMedTimeloennet.length < 1) {
		return null
	}
	return (
		<DollyFieldArray
			header="Timer med timelønnet"
			data={timerMedTimeloennet}
			whiteBackground={harAmelding}
			nested
		>
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

const Utenlandsopphold = ({ utenlandsopphold, harAmelding }: UtlandsoppholdData) => {
	if (!utenlandsopphold || utenlandsopphold.length < 1) {
		return null
	}
	return (
		<DollyFieldArray
			header="Utenlandsopphold"
			data={utenlandsopphold}
			whiteBackground={harAmelding}
			nested
		>
			{(opphold: Utenlands, idx: number) => (
				<React.Fragment key={idx}>
					<TitleValue
						title="Land"
						value={showKodeverkLabel(AdresseKodeverk.ArbeidOgInntektLand, opphold?.land)}
					/>
					<TitleValue title="Opphold fra" value={formatDate(opphold?.periode?.fom)} />
					<TitleValue title="Opphold til" value={formatDate(opphold?.periode?.tom)} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const Permisjon = ({ permisjon, harAmelding }: PermisjonData) => {
	if (!permisjon || permisjon.length < 1) {
		return null
	}
	return (
		<DollyFieldArray header="Permisjon" data={permisjon} whiteBackground={harAmelding} nested>
			{(perm: PermisjonValues, idx: number) => (
				<React.Fragment key={idx}>
					<TitleValue
						title="Permisjonstype"
						value={showKodeverkLabel(
							ArbeidKodeverk.PermisjonsOgPermitteringsBeskrivelse,
							perm?.permisjon,
						)}
					/>
					<TitleValue title="Permisjon fra" value={formatDate(perm?.permisjonsPeriode?.fom)} />
					<TitleValue title="Permisjon til" value={formatDate(perm?.permisjonsPeriode?.tom)} />
					<TitleValue title="Permisjonsprosent" value={perm?.permisjonsprosent} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const Permittering = ({ permittering, harAmelding }: PermitteringData) => {
	if (!permittering || permittering.length < 1) {
		return null
	}
	return (
		<DollyFieldArray header="Permittering" data={permittering} whiteBackground={harAmelding} nested>
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

const ArbeidsforholdVisning = ({ arbeidsforhold, harAmelding }: ArbeidsforholdVisningData) => {
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
				value={showKodeverkLabel(
					ArbeidKodeverk.Arbeidsforholdstyper,
					arbeidsforhold?.arbeidsforholdstype,
				)}
			/>
			<TitleValue title="Arbeidsgiver ident" value={arbeidsforhold?.arbeidsgiver?.ident} />
			<TitleValue title="Ansatt fra" value={formatDate(arbeidsforhold?.ansettelsesPeriode?.fom)} />
			<TitleValue title="Ansatt til" value={formatDate(arbeidsforhold?.ansettelsesPeriode?.tom)} />
			<TitleValue
				title="Sluttårsak"
				value={showKodeverkLabel(
					ArbeidKodeverk.SluttaarsakAareg,
					arbeidsforhold?.ansettelsesPeriode?.sluttaarsak,
				)}
			/>
			<TitleValue title="NAV arbeidsforholdsperiode" value={periode && formatDate(periode)} />
			<TitleValue
				title="Yrke"
				value={showKodeverkLabel(ArbeidKodeverk.Yrker, arbeidsforhold?.arbeidsavtale?.yrke)}
			/>
			<TitleValue
				title="Ansettelsesform"
				value={showKodeverkLabel(
					ArbeidKodeverk.AnsettelsesformAareg,
					arbeidsforhold?.arbeidsavtale?.ansettelsesform,
				)}
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
				value={showKodeverkLabel(
					ArbeidKodeverk.Arbeidstidsordninger,
					arbeidsforhold?.arbeidsavtale?.arbeidstidsordning,
				)}
			/>
			<TitleValue
				title="Avtalt arbeidstimer per uke"
				value={arbeidsforhold?.arbeidsavtale?.avtaltArbeidstimerPerUke}
			/>
			<TitleValue
				title="Skipsregister"
				value={showKodeverkLabel(
					ArbeidKodeverk.Skipsregistre,
					arbeidsforhold.fartoy?.[0]?.skipsregister,
				)}
			/>
			<TitleValue
				title="Fartøystype"
				value={showKodeverkLabel(ArbeidKodeverk.Skipstyper, arbeidsforhold.fartoy?.[0]?.skipstype)}
			/>
			<TitleValue
				title="Fartsområde"
				value={showKodeverkLabel(
					ArbeidKodeverk.Fartsomraader,
					arbeidsforhold.fartoy?.[0]?.fartsomraade,
				)}
			/>
			<TimerMedTimeloennet
				timerMedTimeloennet={arbeidsforhold?.antallTimerForTimeloennet}
				harAmelding={harAmelding}
			/>
			<Utenlandsopphold
				utenlandsopphold={arbeidsforhold?.utenlandsopphold}
				harAmelding={harAmelding}
			/>
			<Permisjon permisjon={arbeidsforhold?.permisjon} harAmelding={harAmelding} />
			<Permittering permittering={arbeidsforhold?.permittering} harAmelding={harAmelding} />
		</React.Fragment>
	)
}

export const Arbeidsforhold = ({ arbeidsforholdListe }: ArbeidsforholdData) => {
	const [selectedIndex, setSelectedIndex] = useState(0)

	if (!arbeidsforholdListe || arbeidsforholdListe.length < 1) {
		return null
	}

	const harAmelding = arbeidsforholdListe[0]?.amelding?.length > 0

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Arbeidsforhold (Aareg)</BestillingTitle>
				<DollyFieldArray header="Arbeidsforhold" data={arbeidsforholdListe}>
					{(arbeidsforhold: Aareg, idx: number) => {
						return (
							<React.Fragment key={idx}>
								{harAmelding ? (
									<>
										<TitleValue
											title="Type arbeidsforhold"
											value={showKodeverkLabel(
												ArbeidKodeverk.Arbeidsforholdstyper,
												arbeidsforhold?.arbeidsforholdstype,
											)}
										/>
										<TitleValue
											title="F.o.m. kalendermåned"
											value={formatDate(arbeidsforhold?.genererPeriode?.fom)}
										/>
										<TitleValue
											title="T.o.m. kalendermåned"
											value={formatDate(arbeidsforhold?.genererPeriode?.tom)}
										/>
										<div className="flexbox--full-width" style={{ margin: '10px 0' }}>
											<DollyKjede
												objectList={arbeidsforhold?.genererPeriode?.periode}
												itemLimit={10}
												selectedIndex={selectedIndex}
												setSelectedIndex={setSelectedIndex}
												isLocked={false}
											/>
										</div>
										<DollyFieldArray
											data={arbeidsforhold?.amelding[selectedIndex]?.arbeidsforhold}
											nested
										>
											{(ameldingArbeidsforhold: Aareg, idy: number) => {
												return (
													<ArbeidsforholdVisning
														arbeidsforhold={ameldingArbeidsforhold}
														key={`${idx}_${idy}`}
														harAmelding={harAmelding}
													/>
												)
											}}
										</DollyFieldArray>
									</>
								) : (
									<ArbeidsforholdVisning
										arbeidsforhold={arbeidsforhold}
										key={idx}
										harAmelding={harAmelding}
									/>
								)}
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
