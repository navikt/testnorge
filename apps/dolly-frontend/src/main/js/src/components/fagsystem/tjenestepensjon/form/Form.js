import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { validation } from '~/components/fagsystem/tjenestepensjon/form/validation'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

const path = 'pensjonforvalter.tp'
const hjelpetekst = 'Ordningen som personen skal ha et forhold til.'

const initialOrdning = {
	ordning: '',
	ytelser: [],
}

export const TjenestepensjonForm = ({ formikBag }) => (
	<Vis attributt={path}>
		<Panel
			heading="Tjenestepensjon"
			hasErrors={panelError(formikBag, path)}
			iconType="pensjon"
			startOpen={erForste(formikBag.values, [path])}
			informasjonstekst={hjelpetekst}
		>
			<FormikDollyFieldArray name="pensjonforvalter.tp" header="Ordning" newEntry={initialOrdning}>
				{(formPath, idx) => (
					<React.Fragment key={idx}>
						<React.Fragment>
							<div className="flexbox--flex-wrap">
								<FormikTextInput name={`${formPath}.ordning`} label="tp-nr" type="text" />
							</div>
						</React.Fragment>
					</React.Fragment>
				)}
			</FormikDollyFieldArray>
		</Panel>
	</Vis>
)

TjenestepensjonForm.validation = validation
