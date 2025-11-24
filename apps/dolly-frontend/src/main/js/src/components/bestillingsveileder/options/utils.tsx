import { initialValues as initialValuesInntektstub } from '@/components/fagsystem/inntektstub/form/Form'
import {
	initialArbeidsforhold,
	initialPermisjon,
	initialUtenlandsopphold,
} from '@/components/fagsystem/aareg/form/initialValues'
import { initialValues as initialValuesInntektsmelding } from '@/components/fagsystem/inntektsmelding/form/Form'
import { initialValues as initialValuesInst } from '@/components/fagsystem/inst/form/Form'
import { initialDoedfoedtBarn } from '@/components/fagsystem/pdlf/form/initialValues'

export const initialValues = {
	inntektstub: initialValuesInntektstub,
	aareg: initialArbeidsforhold,
	permisjon: initialPermisjon,
	utenlandsopphold: initialUtenlandsopphold,
	inntektsmelding: initialValuesInntektsmelding,
	instdata: initialValuesInst,
	barnDoedfoedt: initialDoedfoedtBarn,
	boadresse: {
		bolignr: '',
		flyttedato: null as unknown as string,
	},
	kontaktinformasjonForDoedsbo: {
		adressat: { adressatType: '' },
		adresselinje1: '',
		adresselinje2: '',
		postnummer: '',
		poststedsnavn: '',
		landkode: 'NOR',
		skifteform: '',
		utstedtDato: '',
	},
	arbeidsadgang: {
		arbeidsOmfang: null as unknown as string,
		harArbeidsAdgang: 'JA',
		periode: {
			fra: null as unknown as Date,
			til: null as unknown as Date,
		},
		typeArbeidsadgang: null as unknown as string,
		hjemmel: '',
		forklaring: null as unknown as string,
	},
	udistub: [
		{
			eosEllerEFTABeslutningOmOppholdsrettPeriode: {
				fra: null as unknown as Date,
				til: null as unknown as Date,
			},
			eosEllerEFTABeslutningOmOppholdsrettEffektuering: null as unknown as Date,
			eosEllerEFTABeslutningOmOppholdsrett: '',
		},
		{
			eosEllerEFTAVedtakOmVarigOppholdsrettPeriode: {
				fra: null as unknown as Date,
				til: null as unknown as Date,
			},
			eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering: null as unknown as Date,
			eosEllerEFTAVedtakOmVarigOppholdsrett: '',
		},
		{
			eosEllerEFTAOppholdstillatelsePeriode: {
				fra: null as unknown as Date,
				til: null as unknown as Date,
			},
			eosEllerEFTAOppholdstillatelseEffektuering: null as unknown as Date,
			eosEllerEFTAOppholdstillatelse: '',
		},
		{
			oppholdSammeVilkaar: {
				oppholdSammeVilkaarPeriode: {
					eosEllerEFTAOppholdstillatelsePeriode: {
						fra: null as unknown as Date,
						til: null as unknown as Date,
					},
				},
				oppholdSammeVilkaarEffektuering: null as unknown as Date,
				oppholdstillatelseVedtaksDato: null as unknown as Date,
				oppholdstillatelseType: '',
			},
		},
	],
	barn: {
		identtype: 'FNR',
		kjonn: '',
		barnType: '',
		partnerNr: null as unknown as string,
		borHos: '',
		erAdoptert: false,
		spesreg: '',
		utenFastBopel: false,
		statsborgerskap: '',
		statsborgerskapRegdato: '',
		statsborgerskapTildato: '',
	},
	foreldre: {
		identtype: 'FNR',
		kjonn: '',
		harFellesAdresse: false,
		foreldreType: '',
		spesreg: '',
		utenFastBopel: false,
		statsborgerskap: '',
		statsborgerskapRegdato: '',
		statsborgerskapTildato: '',
	},
	statborgerskap: {
		statborgerskap: '',
		statsborgerskapRegdato: null as unknown as Date,
		statsborgerskapTildato: null as unknown as Date,
	},
	utvandretTil: {
		utvandretTilLand: '',
		utvandretTilLandFlyttedato: null as unknown as Date,
	},
	innvandretFra: {
		innvandretFraLand: '',
		innvandretFraLandFlyttedato: null as unknown as Date,
	},
}
