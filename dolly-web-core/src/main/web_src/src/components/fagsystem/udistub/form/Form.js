import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import { Oppholdsstatus } from './partials/Oppholdsstatus'
import { Arbeidsadgang } from './partials/Arbeidsadgang'
import { Alias } from './partials/Alias'
import { Annet } from './partials/Annet'

const attrPaths = [
	'udistub.oppholdStatus',
	'udistub.arbeidsadgang',
	'udistub.aliaser',
	'udistub.flyktning',
	'udistub.soeknadOmBeskyttelseUnderBehandling'
]

export const UdistubForm = ({ formikBag }) => (
	<Vis attributt={attrPaths}>
		<Panel heading="UDI" hasErrors={panelError(formikBag)}>
			<Oppholdsstatus formikBag={formikBag} />
			<Arbeidsadgang formikBag={formikBag} />
			<Alias formikBag={formikBag} />
			<Annet formikBag={formikBag} />
		</Panel>
	</Vis>
)

UdistubForm.validation = {
	udistub: validation
}
