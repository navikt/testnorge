import React from 'react'
import * as Yup from 'yup'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { Oppholdsstatus } from './partials/Oppholdsstatus'
import { Arbeidsadgang } from './partials/Arbeidsadgang'
import { Alias } from './partials/Alias'
import { Diverse } from './partials/Diverse'

export const UdistubForm = ({ formikBag }) => {
	return (
		<Panel heading="UDI" hasErrors={panelError(formikBag)} startOpen>
			<Oppholdsstatus formikBag={formikBag} />
			<Arbeidsadgang formikBag={formikBag} />
			<Alias formikBag={formikBag} />
			<Diverse formikBag={formikBag} />
		</Panel>
	)
}

UdistubForm.initialValues = {
	udistub: {
		aliaser: [
			{
				identtype: '',
				nyIdent: ''
			}
		],
		arbeidsadgang: {
			arbeidsOmfang: '',
			harArbeidsAdgang: '',
			periode: {
				fra: '',
				til: ''
			},
			typeArbeidsadgang: ''
		},
		flyktning: '',
		oppholdStatus: {},
		soeknadOmBeskyttelseUnderBehandling: ''
	}
}

UdistubForm.validation = {
	udistub: Yup.object({
		aliaser: Yup.array().of(
			Yup.object({
				identtype: Yup.string().when('nyIdent', {
					is: true,
					then: Yup.string().required('Vennligst velg')
				}),
				nyIdent: Yup.boolean().required('Vennligst velg')
			})
		),
		arbeidsadgang: Yup.object({
			arbeidsOmfang: Yup.string(),
			harArbeidsAdgang: Yup.string().required('Vennligst velg'),
			periode: {
				fra: Yup.string().typeError('Formatet må være DD.MM.YYYY'),
				til: Yup.string().typeError('Formatet må være DD.MM.YYYY')
			},
			typeArbeidsadgang: Yup.string()
		}),
		flyktning: Yup.boolean().required('Vennligst velg'),
		oppholdStatus: Yup.object({}),
		soeknadOmBeskyttelseUnderBehandling: Yup.string().required('Vennligst velg')
	})
}

// required:
// --- OPPHOLDSSTATUS
// 	oppholdsstatus
// 	eøs eller efta type opphold
// 	eøs eller efta beslutning om oppholdsrett (grunnlag) (x3 (???) pga av ulike dropdowns)
// 	tredjelands borgere valg
// 	type oppholdstillatelse

// {
// 	requiredField: boolean(),
// 	nested: object().when('requiredField', {
// 	  is: true,
// 	  then: object({
// 		foo: string.required(),
// 		// ...etc
// 	  })
//   }
// }

// validation: yup.string().when('arenaBrukertype', {
// 	is: val => val === 'MED_SERVICEBEHOV',
// 	then: yup.string().required('Velg et servicebehov')
// })
