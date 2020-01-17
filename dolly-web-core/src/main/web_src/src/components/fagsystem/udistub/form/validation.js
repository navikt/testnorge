import * as Yup from 'yup'
import { ifPresent, requiredString, requiredBoolean } from '~/utils/YupValidations'

const aliaser = Yup.array().of(
	Yup.object({
		nyIdent: requiredBoolean,
		identtype: Yup.string()
			.when('nyIdent', {
				is: true,
				then: requiredString
			})
			.nullable()
	})
)

const arbeidsadgang = Yup.object({
	arbeidsOmfang: Yup.string().nullable(),
	harArbeidsAdgang: requiredString,
	periode: Yup.object({
		fra: Yup.date().nullable(),
		til: Yup.date().nullable()
	}),
	typeArbeidsadgang: Yup.string().nullable()
})

const oppholdSammeVilkaar = Yup.object({
	oppholdSammeVilkaarPeriode: ifPresent(
		'$udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarPeriode',
		Yup.object({
			fra: Yup.date().nullable(),
			til: Yup.date().nullable()
		})
	),
	oppholdSammeVilkaarEffektuering: ifPresent(
		'$udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering',
		Yup.date().nullable()
	),
	oppholdstillatelseVedtaksDato: ifPresent(
		'$udistub.oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseVedtaksDato',
		Yup.date().nullable()
	),
	oppholdstillatelseType: ifPresent(
		'$udistub.oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseType',
		requiredString
	)
})

const oppholdStatus = Yup.object({
	eosEllerEFTABeslutningOmOppholdsrettPeriode: ifPresent(
		'$udistub.oppholdStatus.eosEllerEFTABeslutningOmOppholdsrettPeriode',
		Yup.object({
			fra: Yup.date().nullable(),
			til: Yup.date().nullable()
		})
	),
	eosEllerEFTABeslutningOmOppholdsrettEffektuering: ifPresent(
		'$udistub.oppholdStatus.eosEllerEFTABeslutningOmOppholdsrettEffektuering',
		Yup.date().nullable()
	),
	eosEllerEFTABeslutningOmOppholdsrett: ifPresent(
		'$udistub.oppholdStatus.eosEllerEFTABeslutningOmOppholdsrett',
		requiredString
	),
	eosEllerEFTAVedtakOmVarigOppholdsrettPeriode: ifPresent(
		'$udistub.oppholdStatus.eosEllerEFTAVedtakOmVarigOppholdsrettPeriode',
		Yup.object({
			fra: Yup.date().nullable(),
			til: Yup.date().nullable()
		})
	),
	eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering: ifPresent(
		'$udistub.oppholdStatus.eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering',
		Yup.date().nullable()
	),
	eosEllerEFTAVedtakOmVarigOppholdsrett: ifPresent(
		'$udistub.oppholdStatus.eosEllerEFTAVedtakOmVarigOppholdsrett',
		requiredString
	),
	eosEllerEFTAOppholdstillatelsePeriode: ifPresent(
		'$udistub.oppholdStatus.eosEllerEFTAOppholdstillatelsePeriode',
		Yup.object({
			fra: Yup.date().nullable(),
			til: Yup.date().nullable()
		})
	),
	eosEllerEFTAOppholdstillatelseEffektuering: ifPresent(
		'$udistub.oppholdStatus.eosEllerEFTAOppholdstillatelseEffektuering',
		Yup.date().nullable()
	),
	eosEllerEFTAOppholdstillatelse: ifPresent(
		'$udistub.oppholdStatus.eosEllerEFTAOppholdstillatelse',
		requiredString
	),
	oppholdSammeVilkaar: ifPresent('$udistub.oppholdStatus.oppholdSammeVilkaar', oppholdSammeVilkaar)
})

export const validation = {
	udistub: ifPresent(
		'$udistub',
		Yup.object({
			aliaser: ifPresent('$udistub.aliaser', aliaser),
			arbeidsadgang: ifPresent('$udistub.arbeidsadgang', arbeidsadgang),
			flyktning: ifPresent('$udistub.flyktning', requiredBoolean).nullable(),
			oppholdStatus: ifPresent('$udistub.oppholdStatus', oppholdStatus),
			soeknadOmBeskyttelseUnderBehandling: ifPresent(
				'$udistub.soeknadOmBeskyttelseUnderBehandling',
				requiredString
			)
		})
	)
}
