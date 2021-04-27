import React from 'react'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import { ArbeidsforholdForm } from './partials/arbeidsforholdForm'
import { ArbeidsforholdToggle } from './partials/arbeidsforholdToggle'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialValues } from './initialValues'

const aaregAttributt = 'aareg'

export const AaregForm = ({ formikBag }) => (
	<Vis attributt={aaregAttributt}>
		<Panel
			heading="Arbeidsforhold (Aareg)"
			hasErrors={panelError(formikBag, aaregAttributt)}
			iconType="arbeid"
			startOpen={() => erForste(formikBag.values, [aaregAttributt])}
		>
			<ArbeidsforholdToggle formikBag={formikBag} />
			{/* <FormikDollyFieldArray
				name="aareg"
				header="Arbeidsforhold"
				newEntry={initialValues[0]}
				canBeEmpty={false}
			>
				{(path, idx) => <ArbeidsforholdForm path={path} key={idx} formikBag={formikBag} />}
			</FormikDollyFieldArray> */}
		</Panel>
	</Vis>
)

//TODO: skriv ny validation
// AaregForm.validation = validation
