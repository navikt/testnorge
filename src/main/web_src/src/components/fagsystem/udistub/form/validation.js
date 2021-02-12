import * as Yup from 'yup'
import _isEmpty from 'lodash/isEmpty'
import { ifPresent, requiredBoolean, requiredString } from '~/utils/YupValidations'

const checkUndefined = value => {
	if (value !== undefined) {
		return requiredString
	}
	return Yup.mixed().notRequired()
}

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
	oppholdSammeVilkaarPeriode: Yup.object({
		fra: Yup.date().nullable(),
		til: Yup.date().nullable()
	}),
	oppholdSammeVilkaarEffektuering: Yup.date().nullable(),
	oppholdstillatelseVedtaksDato: Yup.date().nullable(),
	oppholdstillatelseType: Yup.string().nullable()
})

const ikkeOppholdSammeVilkaar = Yup.object({
	avslagEllerBortfall: Yup.object({
		avgjorelsesDato: Yup.date().nullable(),
		avslagGrunnlagOverig: Yup.string().nullable(),
		avslagGrunnlagTillatelseGrunnlagEOS: Yup.string().nullable(),
		avslagOppholdsrettBehandlet: Yup.string().nullable(),
		avslagOppholdstillatelseBehandletGrunnlagEOS: Yup.string().nullable(),
		avslagOppholdstillatelseBehandletGrunnlagOvrig: Yup.string().nullable(),
		avslagOppholdstillatelseBehandletUtreiseFrist: Yup.date().nullable(),
		avslagOppholdstillatelseUtreiseFrist: Yup.date().nullable(),
		bortfallAvPOellerBOSDato: Yup.date().nullable(),
		tilbakeKallUtreiseFrist: Yup.date().nullable(),
		formeltVedtakUtreiseFrist: Yup.date().nullable(),
		tilbakeKallVirkningsDato: Yup.date().nullable()
	}),
	ovrigIkkeOppholdsKategoriArsak: Yup.string().nullable(),
	utvistMedInnreiseForbud: Yup.object({
		innreiseForbud: Yup.string().nullable(),
		innreiseForbudVedtaksDato: Yup.date().nullable(),
		varighet: Yup.string().nullable()
	})
})

const oppholdStatus = Yup.object()
	.shape({
		eosEllerEFTABeslutningOmOppholdsrett: Yup.lazy(checkUndefined),
		eosEllerEFTAVedtakOmVarigOppholdsrett: Yup.lazy(checkUndefined),
		eosEllerEFTAOppholdstillatelse: Yup.lazy(checkUndefined),
		ikkeOppholdSammeVilkaar: Yup.lazy(value => {
			if (value !== undefined) {
				return ikkeOppholdSammeVilkaar
			}
			return Yup.mixed().notRequired()
		}),
		oppholdSammeVilkaar: Yup.lazy(value => {
			if (value !== undefined) {
				return oppholdSammeVilkaar
			}
			return Yup.mixed().notRequired()
		})
	})
	.nullable()
	// Sjekker om oppholdStatus er et tomt objekt. Objektet blir satt ved å fylle i feltene
	// 'Oppholdsstatus' og 'Type opphold', men disse er ikke en del av selve formet.
	.test('is-not-empty', function() {
		const values = this.options.context
		if (_isEmpty(values.udistub.oppholdStatus)) {
			return values.udistub.harOppholdsTillatelse === false
		}
		return true
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
