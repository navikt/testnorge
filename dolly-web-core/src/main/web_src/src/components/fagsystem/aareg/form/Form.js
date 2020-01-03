import React from 'react'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import { ArbeidsforholdForm } from './partials/arbeidsforholdForm'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialValues } from './initialValues'

const aaregAttributt = 'aareg'

export const AaregForm = ({ formikBag }) => (
	<Vis attributt={aaregAttributt}>
		<Panel
			heading="Arbeidsforhold"
			hasErrors={panelError(formikBag, aaregAttributt)}
			startOpen
			iconType="arbeid"
		>
			<DollyFieldArray name="aareg" title="Arbeidsforhold" newEntry={initialValues[0]}>
				{(path, idx) => <ArbeidsforholdForm path={path} key={idx} formikBag={formikBag} />}
			</DollyFieldArray>
		</Panel>
	</Vis>
)

AaregForm.validation = validation
