import React from 'react'
import _isEmpty from 'lodash/isEmpty'
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

export const UdistubForm = ({ formikBag }) => (
	<Vis attributt={attrPaths}>
		<Panel heading="UDI" hasErrors={panelError(formikBag)} startOpen>
			<Oppholdsstatus formikBag={formikBag} />
			<Arbeidsadgang formikBag={formikBag} />
			<Alias formikBag={formikBag} />
			<Annet formikBag={formikBag} />
		</Panel>
	</Vis>
)

UdistubForm.initialValues = attrs => {
	const initial = {}

	if (attrs.arbeidsadgang) {
		initial.arbeidsadgang = {
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
		initial.oppholdStatus = {}
	}

	if (attrs.aliaser) {
		initial.aliaser = [
			{
				identtype: '',
				nyIdent: false
			}
		]
	}

	if (attrs.flyktning) {
		initial.flyktning = ''
	}

	if (attrs.asylsoker) {
		initial.soeknadOmBeskyttelseUnderBehandling = ''
	}

	return !_isEmpty(initial) && { udistub: initial }
}

UdistubForm.validation = {
	udistub: validation
}
