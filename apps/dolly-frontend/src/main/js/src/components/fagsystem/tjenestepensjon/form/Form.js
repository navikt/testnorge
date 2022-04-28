import React from 'react'

import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { validation } from '~/components/fagsystem/tjenestepensjon/form/validation'

const pensjonAttributt = 'pensjonforvalter'
const path = `${pensjonAttributt}.tp`

const hjelpetekst = 'Ordningen som personen skal ha et forhold til.'

export const TjenestepensjonForm = ({ formikBag }) => (
	<Vis attributt={path}>
		<Panel
			heading="Tjenestepensjon"
			hasErrors={panelError(formikBag, path)}
			iconType="pensjon"
			startOpen={() => erForste(formikBag.values, [pensjonAttributt])}
			informasjonstekst={hjelpetekst}
		>
			<Kategori title="Forhold" vis={path}>
				<React.Fragment>
					<FormikTextInput name={`${path}.ordning`} label="tp-nr" type="text" fastfield="false" />
				</React.Fragment>
			</Kategori>
		</Panel>
	</Vis>
)

TjenestepensjonForm.validation = validation
