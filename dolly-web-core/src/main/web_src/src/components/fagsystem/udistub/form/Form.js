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
		// harOppholdsTillatelse: '',
		oppholdStatus: {
			// fyll ut
		}
	}
}

UdistubForm.validation = {
	udistub: Yup.object({
		// fyll ut
	})
}
