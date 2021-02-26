import { FormikProps } from 'formik'

export interface SykemeldingForm {
	formikBag: FormikProps<{}>
}

export type Sykemelding = {
	data: Array<SykemeldingSynt | SykemeldingDetaljert>
}

export type SykemeldingSynt = {
	sykemelding: {
		startDato: string
		orgnummer: string
		arbeidsforholdId: string
	}
	idx: number
	erGjenopprettet?: boolean
}

export type SykemeldingDetaljert = {
	sykemelding: {
		startDato: string
		umiddelbarBistand: boolean
		manglendeTilretteleggingPaaArbeidsplassen: boolean
		hovedDiagnose: Diagnose
		biDiagnoser: Array<Diagnose>
		helsepersonell: Helsepersonell
		arbeidsgiver: Arbeidsgiver
		detaljer: Detaljer
		perioder: Array<Periode>
	}
	idx: number
	erGjenopprettet?: boolean
}

export type Diagnose = {
	diagnose: string
	diagnosekode: string
	system?: string
}

export type Helsepersonell = {
	fornavn: string
	mellomnavn?: string
	etternavn: string
	ident?: string
	fnr?: string
	hprId: string
	samhandlerType: string
}

export type Arbeidsgiver = {
	navn: string
	orgnr?: string
	yrkesbetegnelse?: string
	stillingsprosent?: string
	forretningsAdresse?: {
		adresse: string
		landkode: string
		postnr: string
		poststed: string
	}
}

export type Detaljer = {
	tiltakNav: string
	tiltakArbeidsplass: string
	beskrivHensynArbeidsplassen: string
	arbeidsforEtterEndtPeriode: boolean
}

export type Periode = {
	aktivitet: {
		aktivitet: string
		behandlingsdager: number
		grad: number
		reisetilskudd: boolean
	}
	fom: string
	tom: string
}
