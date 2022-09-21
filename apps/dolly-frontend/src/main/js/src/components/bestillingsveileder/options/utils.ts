import { initialValues as initialValuesInntektstub } from '~/components/fagsystem/inntektstub/form/Form'
import {
	initialPermisjon,
	initialUtenlandsopphold,
	initialValues as initialValuesAareg,
} from '~/components/fagsystem/aareg/form/initialValues'
import { initialValues as initialValuesInntektsmelding } from '~/components/fagsystem/inntektsmelding/form/Form'
import { initialValues as initialValuesInst } from '~/components/fagsystem/inst/form/Form'
import { initialDoedfoedtBarn } from '~/components/fagsystem/pdlf/form/initialValues'

export const initialValues = {
	inntektstub: initialValuesInntektstub,
	aareg: initialValuesAareg,
	permisjon: initialPermisjon,
	utenlandsopphold: initialUtenlandsopphold,
	inntektsmelding: initialValuesInntektsmelding,
	instdata: initialValuesInst,
	barnDoedfoedt: initialDoedfoedtBarn,
	boadresse: {
		bolignr: '',
		flyttedato: null as string,
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
		arbeidsOmfang: null as string,
		harArbeidsAdgang: 'JA',
		periode: {
			fra: null as Date,
			til: null as Date,
		},
		typeArbeidsadgang: null as string,
		hjemmel: '',
		forklaring: null as string,
	},
	udistub: [
		{
			eosEllerEFTABeslutningOmOppholdsrettPeriode: {
				fra: null as Date,
				til: null as Date,
			},
			eosEllerEFTABeslutningOmOppholdsrettEffektuering: null as Date,
			eosEllerEFTABeslutningOmOppholdsrett: '',
		},
		{
			eosEllerEFTAVedtakOmVarigOppholdsrettPeriode: {
				fra: null as Date,
				til: null as Date,
			},
			eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering: null as Date,
			eosEllerEFTAVedtakOmVarigOppholdsrett: '',
		},
		{
			eosEllerEFTAOppholdstillatelsePeriode: {
				fra: null as Date,
				til: null as Date,
			},
			eosEllerEFTAOppholdstillatelseEffektuering: null as Date,
			eosEllerEFTAOppholdstillatelse: '',
		},
		{
			oppholdSammeVilkaar: {
				oppholdSammeVilkaarPeriode: {
					eosEllerEFTAOppholdstillatelsePeriode: {
						fra: null as Date,
						til: null as Date,
					},
				},
				oppholdSammeVilkaarEffektuering: null as Date,
				oppholdstillatelseVedtaksDato: null as Date,
				oppholdstillatelseType: '',
			},
		},
	],
	barn: {
		identtype: 'FNR',
		kjonn: '',
		barnType: '',
		partnerNr: null as string,
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
		statsborgerskapRegdato: null as Date,
		statsborgerskapTildato: null as Date,
	},
	utvandretTil: {
		utvandretTilLand: '',
		utvandretTilLandFlyttedato: null as Date,
	},
	innvandretFra: {
		innvandretFraLand: '',
		innvandretFraLandFlyttedato: null as Date,
	},
}
