import * as Yup from 'yup'
import { requiredDate, requiredString, requiredNumber, ifPresent } from '~/utils/YupValidations'

const antallTimerForTimeloennet = Yup.array().of(
	Yup.object({
		periode: Yup.object({
			fom: requiredDate,
			tom: requiredDate
		}),
		antallTimer: requiredNumber
	})
)

const permisjon = Yup.array().of(
	Yup.object({
		permisjonsPeriode: Yup.object({
			fom: requiredDate,
			tom: requiredDate
		}),
		permisjonsprosent: requiredNumber,
		permisjonOgPermittering: requiredString
	})
)

const utenlandsopphold = Yup.array().of(
	Yup.object({
		periode: Yup.object({
			fom: requiredDate,
			tom: requiredDate
		}),
		land: requiredString
	})
)

export const validation = {
	aareg: Yup.array().of(
		Yup.object({
			ansettelsesPeriode: Yup.object({
				fom: requiredDate,
				tom: Yup.date().nullable()
			}),
			arbeidsforholdstype: Yup.string(),
			arbeidsgiver: Yup.object({
				aktoertype: requiredString,
				aktoerId: Yup.string().when('aktoertype', {
					is: 'ORG',
					then: Yup.string()
						.matches(/^[0-9]*$/, 'Orgnummer må være et tall med 9 sifre')
						.test('len', 'Orgnummer må være et tall med 9 sifre', val => val && val.length === 9),
					otherwise: Yup.string()
						.matches(/^[0-9]*$/, 'Ident må være et tall med 11 sifre')
						.test('len', 'Ident må være et tall med 11 sifre', val => val && val.length === 11)
				})
			}),
			arbeidsavtale: Yup.object({
				yrke: requiredString,
				stillingsprosent: requiredNumber,
				endringsdatoStillingsprosent: Yup.date().nullable(),
				arbeidstidsordning: requiredString,
				antallKonverterteTimer: Yup.number(),
				avtaltArbeidstimerPerUke: Yup.number()
			}),
			antallTimerForTimeloennet: ifPresent(
				'$aareg[0].antallTimerForTimeloennet',
				antallTimerForTimeloennet
			),
			permisjon: ifPresent('$aareg[0].permisjon', permisjon),
			utenlandsopphold: ifPresent('$aareg[0].utenlandsopphold', utenlandsopphold)
		})
	)
}
