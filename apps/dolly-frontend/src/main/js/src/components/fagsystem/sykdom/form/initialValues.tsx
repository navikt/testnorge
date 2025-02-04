import { Diagnose } from '@/components/fagsystem/sykdom/SykemeldingTypes'
import { addDays } from 'date-fns'
import { getRandomValue } from '@/components/fagsystem/utils'
import { SelectOptionsGyldigeDiagnoser } from '@/components/fagsystem/sykdom/form/partials/SelectOptionsDiagnoser'

const randomDiagnose = getRandomValue(SelectOptionsGyldigeDiagnoser())

export const initialValuesDetaljertSykemelding = {
	detaljertSykemelding: {
		arbeidsgiver: {
			navn: '',
			stillingsprosent: 100,
			yrkesbetegnelse: '',
		},
		biDiagnoser: [] as Array<Diagnose>,
		detaljer: {
			arbeidsforEtterEndtPeriode: false,
			beskrivHensynArbeidsplassen: '',
			tiltakArbeidsplass: '',
			tiltakNav: '',
		},
		hovedDiagnose: {
			diagnose: randomDiagnose?.diagnoseNavn || '',
			diagnosekode: randomDiagnose?.value || '',
			system: randomDiagnose ? '2.16.578.1.12.4.1.1.7170' : '',
		},
		helsepersonell: {
			etternavn: '',
			fornavn: '',
			hprId: '',
			ident: '',
			mellomnavn: '',
			samhandlerType: '',
		},
		manglendeTilretteleggingPaaArbeidsplassen: false,
		mottaker: {
			navn: '',
			orgNr: null,
			adresse: {
				by: '',
				gate: '',
				land: '',
				postnummer: '',
			},
		},
		perioder: [
			{
				aktivitet: {
					aktivitet: null as unknown as string,
					behandlingsdager: 0,
					grad: 0,
					reisetilskudd: false,
				},
				fom: addDays(new Date(), -7),
				tom: addDays(new Date(), -1),
			},
		],
		kontaktMedPasient: {
			kontaktDato: null,
			begrunnelseIkkeKontakt: 'Ikke behov for kontakt',
		},
		startDato: new Date(),
		umiddelbarBistand: false,
	},
}
