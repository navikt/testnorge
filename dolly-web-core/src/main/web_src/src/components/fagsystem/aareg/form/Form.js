import React from 'react'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import { ArbeidsforholdForm } from './partials/arbeidsforholdForm'

export const AaregForm = ({ formikBag }) => (
	<Vis attributt="aareg">
		<Panel heading="Arbeidsforhold" hasErrors={panelError(formikBag)} startOpen>
			<ArbeidsforholdForm formikBag={formikBag} />
		</Panel>
	</Vis>
)

AaregForm.validation = validation
