import * as Yup from 'yup'
import * as _ from 'lodash-es'
import { ifPresent, requiredBoolean, requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

const checkUndefined = (value) => {
	if (value !== undefined) {
		return requiredString
	}
	return Yup.mixed().notRequired()
}

const arbeidsadgang = Yup.object({
	arbeidsOmfang: Yup.string().nullable(),
	harArbeidsAdgang: requiredString,
	periode: Yup.object({
		fra: testDatoFom(Yup.date().nullable(), 'til'),
		til: testDatoTom(Yup.date().nullable(), 'fra'),
	}),
	typeArbeidsadgang: Yup.string().nullable(),
	hjemmel: ifPresent(
		'$udistub.arbeidsadgang.hjemmel',
		requiredString.max(255, 'Hjemmel kan ikke være lenger enn 255 tegn').nullable(),
	),
	forklaring: Yup.string().max(4000).nullable(),
})

const oppholdSammeVilkaar = Yup.object({
	oppholdSammeVilkaarPeriode: Yup.object({
		fra: testDatoFom(Yup.date().nullable(), 'til'),
		til: testDatoTom(Yup.date().nullable(), 'fra'),
	}),
	oppholdSammeVilkaarEffektuering: Yup.date().nullable(),
	oppholdstillatelseVedtaksDato: Yup.date().nullable(),
	oppholdstillatelseType: Yup.string().nullable(),
})

const ikkeOppholdSammeVilkaar = Yup.object({
	avslagEllerBortfall: Yup.object({
		avgjorelsesDato: Yup.date().nullable(),
	}),
})

const oppholdStatus = Yup.object()
	.shape({
		eosEllerEFTABeslutningOmOppholdsrett: Yup.lazy(checkUndefined),
		eosEllerEFTAVedtakOmVarigOppholdsrett: Yup.lazy(checkUndefined),
		eosEllerEFTAOppholdstillatelse: Yup.lazy(checkUndefined),
		ikkeOppholdSammeVilkaar: Yup.lazy((value) => {
			if (value !== undefined) {
				return ikkeOppholdSammeVilkaar
			}
			return Yup.mixed().notRequired()
		}),
		oppholdSammeVilkaar: Yup.lazy((value) => {
			if (value !== undefined) {
				return oppholdSammeVilkaar
			}
			return Yup.mixed().notRequired()
		}),
	})
	.nullable()
	// Sjekker om oppholdStatus er et tomt objekt. Objektet blir satt ved å fylle i feltene
	// 'Oppholdsstatus' og 'Type opphold', men disse er ikke en del av selve formet.
	.test('is-not-empty', 'Feltet er påkrevd', (value, context) => {
		const values = context.parent
		if (_.isEmpty(values.oppholdStatus)) {
			return values.harOppholdsTillatelse === false
		}
		return true
	})

export const validation = {
	udistub: ifPresent(
		'$udistub',
		Yup.object({
			arbeidsadgang: ifPresent('$udistub.arbeidsadgang', arbeidsadgang),
			flyktning: ifPresent('$udistub.flyktning', requiredBoolean),
			oppholdStatus: ifPresent('$udistub.oppholdStatus', oppholdStatus),
			soeknadOmBeskyttelseUnderBehandling: ifPresent(
				'$udistub.soeknadOmBeskyttelseUnderBehandling',
				requiredString,
			),
		}),
	),
}
