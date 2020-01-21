import * as Yup from 'yup'
import _has from 'lodash/has'
import { ifPresent, requiredString, requiredBoolean } from '~/utils/YupValidations'

const oppholdsstatusTest = (validation, path) => {
	const statusArray = [
		'udistub.oppholdStatus.eosEllerEFTABeslutningOmOppholdsrett',
		'udistub.oppholdStatus.eosEllerEFTAVedtakOmVarigOppholdsrett',
		'udistub.oppholdStatus.eosEllerEFTAOppholdstillatelse',
		'udistub.oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseType',
		'udistub.harOppholdsTillatelse',
		'udistub.oppholdStatus.uavklart'
	]
	// const filteredStatuser = muligeStatuserArray.filter(path => path !== dennePath)

	return validation.test('harPath', function sjekkPath(val) {
		console.log('val :', val)
		if (!val) return true
		const values = this.options.context
		let harPath = false
		statusArray.forEach(status => {
			console.log('status :', status)
			console.log('values :', values)
			if (_has(values, status)) {
				harPath = true
			}
		})
		// console.log('filteredStatuser :', filteredStatuser)
		// console.log('values :', values)
		console.log('harPath :', harPath)
		return harPath
	})
}

// const oppholdsstatusTest = (validation, dennePath) => {
// 	const statusArray = [
// 		'udistub.oppholdStatus.eosEllerEFTABeslutningOmOppholdsrett',
// 		'udistub.oppholdStatus.eosEllerEFTAVedtakOmVarigOppholdsrett',
// 		'udistub.oppholdStatus.eosEllerEFTAOppholdstillatelse',
// 		'udistub.oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseType',
// 		'udistub.harOppholdsTillatelse',
// 		'udistub.oppholdStatus.uavklart'
// 	]

// 	const filteredStatuser = statusArray.filter(path => path !== dennePath)

// 	return validation.test('harPath', function sjekkPath(val) {
// 		console.log('val :', val)
// 		if (!val) return true
// 		const values = this.options.context
// 		let harPath = false
// 		filteredStatuser.forEach(status => {
// 			console.log('status :', status)
// 			console.log('values :', values)
// 			if (_has(values, status)) {
// 				harPath = true
// 			}
// 		})
// 		// console.log('filteredStatuser :', filteredStatuser)
// 		// console.log('values :', values)
// 		console.log('harPath :', harPath)
// 		return harPath
// 	})
// }

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
	oppholdstillatelseType: oppholdsstatusTest(requiredString)
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
	// eosEllerEFTABeslutningOmOppholdsrett: Yup.string().when(
	// 	['eosEllerEFTAVedtakOmVarigOppholdsrett', 'eosEllerEFTAOppholdstillatelse'],
	// 	{
	// 		is: undefined,
	// 		then: requiredString
	// 	}
	// ),
	// eosEllerEFTABeslutningOmOppholdsrett: Yup.string().when(
	// 	oppholdsstatusTest(Yup.object(), 'udistub.oppholdStatus.eosEllerEFTABeslutningOmOppholdsrett'),
	// 	{
	// 		is: false,
	// 		then: requiredString
	// 	}
	// ),
	eosEllerEFTABeslutningOmOppholdsrett: oppholdsstatusTest(
		requiredString,
		'udistub.oppholdStatus.eosEllerEFTABeslutningOmOppholdsrett'
	),
	// eosEllerEFTABeslutningOmOppholdsrett: requiredString,
	// eosEllerEFTABeslutningOmOppholdsrett: ifPresent(
	// 	'$udistub.oppholdStatus.eosEllerEFTABeslutningOmOppholdsrett',
	// 	requiredString
	// ),
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
	eosEllerEFTAVedtakOmVarigOppholdsrett: Yup.string(),
	// eosEllerEFTAVedtakOmVarigOppholdsrett: Yup.string().when(
	// 	['eosEllerEFTABeslutningOmOppholdsrett', 'eosEllerEFTAOppholdstillatelse'],
	// 	{
	// 		is: undefined,
	// 		then: requiredString
	// 	}
	// ),
	// eosEllerEFTAVedtakOmVarigOppholdsrett: oppholdsstatusTest(requiredString),
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
	eosEllerEFTAOppholdstillatelse: Yup.string(),
	// eosEllerEFTAOppholdstillatelse: Yup.string().when(
	// 	['eosEllerEFTAVedtakOmVarigOppholdsrett', 'eosEllerEFTABeslutningOmOppholdsrett'],
	// 	{
	// 		is: undefined,
	// 		then: requiredString
	// 	}
	// ),
	// eosEllerEFTAOppholdstillatelse: oppholdsstatusTest(requiredString),
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
