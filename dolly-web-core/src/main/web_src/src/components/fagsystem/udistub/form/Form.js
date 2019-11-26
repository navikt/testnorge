import React from 'react'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import { Oppholdsstatus } from './partials/Oppholdsstatus'
import { Arbeidsadgang } from './partials/Arbeidsadgang'
import { Alias } from './partials/Alias'
import { Annet } from './partials/Annet'

const attrPaths = [
	pathAttrs.kategori.opphold,
	pathAttrs.kategori.arbeidsadgang,
	pathAttrs.kategori.alias,
	pathAttrs.kategori.annet
].flat()

export const UdistubForm = ({ formikBag }) => {
	const { udistub } = formikBag.values
	return (
		<Vis attributt={attrPaths}>
			<Panel heading="UDI" hasErrors={panelError(formikBag)} startOpen>
				{udistub.oppholdStatus && <Oppholdsstatus formikBag={formikBag} />}
				{udistub.arbeidsadgang && <Arbeidsadgang formikBag={formikBag} />}
				{udistub.aliaser && <Alias formikBag={formikBag} />}
				<Annet formikBag={formikBag} />
			</Panel>
		</Vis>
	)
}

UdistubForm.initialValues = attrs => {
	const initial = {
		udistub: {}
	}

	if (attrs.arbeidsadgang) {
		initial.udistub.arbeidsadgang = {
			arbeidsOmfang: '',
			harArbeidsAdgang: '',
			periode: {
				fra: '',
				til: ''
			},
			typeArbeidsadgang: ''
		}
	}

	if (attrs.oppholdStatus) {
		initial.udistub.oppholdStatus = {}
	}

	if (attrs.aliaser) {
		initial.udistub.aliaser = [
			{
				identtype: '',
				nyIdent: false
			}
		]
	}

	if (attrs.flyktning) {
		initial.udistub.flyktning = ''
	}

	if (attrs.asylsoker) {
		initial.udistub.soeknadOmBeskyttelseUnderBehandling = ''
	}

	return initial
}

UdistubForm.validation = {
	udistub: validation
}
