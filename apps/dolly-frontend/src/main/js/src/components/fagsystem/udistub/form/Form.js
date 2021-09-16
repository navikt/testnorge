import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
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
	'udistub.soeknadOmBeskyttelseUnderBehandling',
]

const udiAttributt = 'udistub'

export const UdistubForm = ({ formikBag }) => (
	<Vis attributt={attrPaths}>
		<Panel
			heading="UDI"
			hasErrors={panelError(formikBag, attrPaths)}
			iconType="udi"
			startOpen={() => erForste(formikBag.values, [udiAttributt])}
		>
			<Kategori title="Gjeldende oppholdsstatus" vis="udistub.oppholdStatus">
				<Oppholdsstatus formikBag={formikBag} />
			</Kategori>
			<Arbeidsadgang formikBag={formikBag} />
			<Kategori title="Alias" vis="udistub.aliaser" flex={false}>
				<Alias formikBag={formikBag} />
			</Kategori>
			<Annet formikBag={formikBag} />
		</Panel>
	</Vis>
)

UdistubForm.validation = validation
