import { initialValues as initialValuesInntektstub } from '~/components/fagsystem/inntektstub/form/Form'
import { initialValues as initialValuesAareg } from '~/components/fagsystem/aareg/form/initialValues'
import {
	initialPermisjon,
	initialUtenlandsopphold
} from '~/components/fagsystem/aareg/form/initialValues'
import { initialValues as initialValuesInntektsmelding } from '~/components/fagsystem/inntektsmelding/form/Form'
import { initialValues as initialValuesInst } from '~/components/fagsystem/inst/form/Form'

export const initialValues = {
	inntektstub: initialValuesInntektstub,
	aareg: initialValuesAareg,
	permisjon: initialPermisjon,
	utenlandsopphold: initialUtenlandsopphold,
	inntektsmelding: initialValuesInntektsmelding,
	instdata: initialValuesInst,
	kontaktinformasjonForDoedsbo: {
		adressat: { adressatType: '' },
		adresselinje1: '',
		adresselinje2: '',
		postnummer: '',
		poststedsnavn: '',
		landkode: 'NOR',
		skifteform: '',
		utstedtDato: ''
	},
	arbeidsadgang: {
		arbeidsOmfang: null,
		harArbeidsAdgang: 'JA',
		periode: {
			fra: null,
			til: null
		},
		typeArbeidsadgang: null
	},
	udistub: [
		{
			eosEllerEFTABeslutningOmOppholdsrettPeriode: {
				fra: null,
				til: null
			},
			eosEllerEFTABeslutningOmOppholdsrettEffektuering: null,
			eosEllerEFTABeslutningOmOppholdsrett: ''
		},
		{
			eosEllerEFTAVedtakOmVarigOppholdsrettPeriode: {
				fra: null,
				til: null
			},
			eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering: null,
			eosEllerEFTAVedtakOmVarigOppholdsrett: ''
		},
		{
			eosEllerEFTAOppholdstillatelsePeriode: {
				fra: null,
				til: null
			},
			eosEllerEFTAOppholdstillatelseEffektuering: null,
			eosEllerEFTAOppholdstillatelse: ''
		},
		{
			oppholdSammeVilkaar: {
				oppholdSammeVilkaarPeriode: {
					fra: null,
					til: null
				},
				oppholdSammeVilkaarEffektuering: null,
				oppholdstillatelseVedtaksDato: null,
				oppholdstillatelseType: ''
			}
		}
	],
	partnere: {
		identtype: 'FNR',
		kjonn: '',
		sivilstander: [{ sivilstand: '', sivilstandRegdato: null }],
		harFellesAdresse: false,
		spesreg: '',
		utenFastBopel: false,
		statsborgerskap: '',
		statsborgerskapRegdato: ''
	},
	barn: {
		identtype: 'FNR',
		kjonn: '',
		barnType: '',
		partnerNr: null,
		borHos: '',
		erAdoptert: false,
		spesreg: '',
		utenFastBopel: false,
		statsborgerskap: '',
		statsborgerskapRegdato: ''
	},
	statborgerskap:{
		statborgerskap:'',
		statsborgerskapRegdato: ''
	},
	utvandretTil:{
		utvandretTilLand:'',
		utvandretTilLandFlyttedato: ''
	},
	innvandretFra:{
		innvandretFraLand:'',
		innvandretFraLandFlyttedato: ''
	}
}