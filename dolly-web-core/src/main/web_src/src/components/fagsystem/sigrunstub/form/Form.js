import React from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { InntektsaarForm } from './partials/inntektsaarForm'
import { requiredString, ifPresent } from '~/utils/YupValidations'

const sigrunAttributt = 'sigrunstub'
export const SigrunstubForm = ({ formikBag }) => (
	<Vis attributt={sigrunAttributt}>
		<Panel
			heading="Inntekt"
			hasErrors={panelError(formikBag, sigrunAttributt)}
			iconType="sigrun"
			startOpen
		>
			<InntektsaarForm formikBag={formikBag} />
		</Panel>
	</Vis>
)

SigrunstubForm.validation = {
	sigrunstub: ifPresent(
		'$sigrunstub',
		Yup.array().of(
			Yup.object({
				grunnlag: Yup.array()
					.of(
						Yup.object({
							tekniskNavn: Yup.string().required('Velg en type inntekt.'),
							verdi: Yup.number()
								.min(0, 'Tast inn et gyldig beløp')
								.required('Oppgi beløpet')
						})
					)
					.test('is-required', 'Legg til minst én inntekt', function checkTjenesteGrunnlag(val) {
						const values = this.options.context
						const path = this.options.path
						const index = path.charAt(path.indexOf('[') + 1)
						console.log('this :', this)
						console.log('values :', values)
						console.log('path :', path)
						if (values.sigrunstub[index].tjeneste === 'BEREGNET_SKATT') {
							if (values.sigrunstub[index].grunnlag.length > 0) {
								return true
							} else return false
						} else if (values.sigrunstub[index].tjeneste === 'SUMMERT_SKATTEGRUNNLAG') {
							if (
								values.sigrunstub[index].grunnlag.length > 0 ||
								values.sigrunstub[index].svalbardGrunnlag.length > 0
							) {
								return true
							} else return false
						} else return true
					}),
				// .min(1, 'Legg til minst én inntekt'),
				inntektsaar: Yup.number()
					.integer('Ugyldig årstall')
					.required('Tast inn et gyldig år'),
				svalbardGrunnlag: Yup.array().of(
					Yup.object({
						tekniskNavn: Yup.string().required('Velg en type inntekt.'),
						verdi: Yup.number()
							.min(0, 'Tast inn et gyldig beløp')
							.required('Oppgi beløpet')
					})
				).test,
				tjeneste: Yup.string().required('Velg en type tjeneste.')
			})
		)
	)
}

// grunnlag: Yup.array().when('tjeneste', {
// 	is: 'BEREGNET_SKATT',
// 	then: Yup.array()
// 		.of(
// 			Yup.object({
// 				tekniskNavn: Yup.string().required('Velg en type inntekt.'),
// 				verdi: Yup.number()
// 					.min(0, 'Tast inn et gyldig beløp')
// 					.required('Oppgi beløpet')
// 			})
// 		)
// 		.min(1, 'Legg til minst én inntekt')
// }),

// svalbardGrunnlag: Yup.array().of(
// 	Yup.object({
// 		tekniskNavn: Yup.string().required('Velg en type inntekt.'),
// 		verdi: Yup.number()
// 			.min(0, 'Tast inn et gyldig beløp')
// 			.required('Oppgi beløpet')
// 	})
// ),

// const delaySchema = yup.object().shape({
// 	shouldCheck: yup.boolean(),
// 	rules: yup.array()
// 	  .when(['shouldCheck'], {
// 		is: (sck) => {
// 		  return sck;
// 		},
// 	  then: yup.array().of(yup.object().shape({
// 		right: yup.string().required(),
// 	  })),
// 	  otherwise: yup.mixed().nullable()
// 	}),
//   });
